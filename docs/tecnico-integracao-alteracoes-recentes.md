# Tasks-Service — Notas técnicas de integração (alterações recentes)

Documento para desenvolvedores que consomem este microsserviço (frontend, BFF, Motivation-Service ou outros clientes Feign).

**Versão do artefato:** `0.0.1-SNAPSHOT`  
**Base URL local:** `http://localhost:8085`  
**Autenticação:** token de máquina no path (`{token}`), validado via Auth-For-MService (`TokenService.validateToken`).

---

## Resumo das mudanças

| Área | Mudança | Impacto no consumidor |
|------|---------|------------------------|
| Timestamps + fuso | Header `X-User-Timezone`; 4 campos datetime em mutações e GETs | Enviar fuso IANA; parsear `OffsetDateTime`; GET retorna `TaskViewDTO` |
| Usuário responsável | Nova API `/ms/tasks/responsible`; criação preenche `idResponsibleUser = idUser` | Atribuir/consultar via `/responsible`; `TaskViewDTO` expõe `idResponsibleUser` nas listagens |
| Subtarefas | Nova API `/ms/tasks/subtask` | Usar endpoints dedicados; não esperar subtarefas nas listagens antigas |
| Listagens | Filtro `idParentTask == null` | Listas por data/status/objetivo retornam só tarefas pai |
| Adiamento diário | Novo `POST .../postpone-day/{token}` | Integrar job/noturno ou ação do usuário para virar `LATE` e mover datas |
| Adiamento + subtarefas | `TODO` filho ignorado no fluxo `LATE` | Subtarefas não viram atrasadas automaticamente |
| Autorização | Removida checagem dono×tarefa no repositório | BFF deve garantir que `idUser` do path é o dono (ou política equivalente) |

---

## 1. Usuário responsável por tarefa

Feature **independente** do CRUD e das listagens em `/ms/tasks` quanto a contratos HTTP dedicados. A criação de tarefa, subtarefa e cópia recorrente **persiste** `idResponsibleUser = idUser`. Esse campo **não** aparece em `ResponseTaskDTO` / `ResponseSubtaskDTO`, mas **sim** em `TaskViewDTO` (GETs).

### Modelo

| Campo | Tipo | Significado |
|-------|------|-------------|
| `idUser` | UUID | Dono da tarefa (escopo histórico das queries por usuário) |
| `idResponsibleUser` | UUID, nullable | Usuário responsável pela execução; na **criação** recebe o mesmo valor de `idUser`; `null` = não atribuído (após desatribuição ou registros legados) |

Coluna MySQL: `id_responsible_user` (`CHAR(36) NULL`). Sem Flyway no repositório — aplicar DDL no ambiente ou confiar em `ddl-auto` em dev:

```sql
ALTER TABLE tasks ADD COLUMN id_responsible_user CHAR(36) NULL;
```

Não há validação Feign de existência do usuário responsável neste serviço.

### Endpoints

Prefixo: `/ms/tasks/responsible`  
Tag Swagger: **Responsible User Task**  
Processamento: `@Async` → `CompletableFuture<ResponseEntity<...>>`

#### `PUT /{token}` — Atribuir ou desatribuir responsável

**Body (`RequestUpdateResponsibleUserDTO`):**

```json
{
  "idTask": "uuid",
  "idUser": "uuid",
  "idResponsibleUser": "uuid"
}
```

| Campo | Obrigatório | Observação |
|-------|-------------|------------|
| idTask | sim | Tarefa alvo |
| idUser | sim | Repassado a `findById` (não validado como dono no repositório) |
| idResponsibleUser | não | `null` remove a atribuição |

**Respostas:**

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseResponsibleUserDTO` | Sucesso |
| 401 | vazio | Token inválido ou ausente |
| 404 | `ExceptionResponse` | Tarefa não encontrada (`NotFoundException`) |
| 500 | `ExceptionResponse` | Erro de persistência (`DataBaseException`) |

**`ResponseResponsibleUserDTO`:**

```json
{
  "idTask": "uuid",
  "idResponsibleUser": "uuid"
}
```

`idResponsibleUser` pode ser `null` na resposta.

#### `GET /{idUser}/{idTask}/{token}` — Consultar responsável

**Respostas:**

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseResponsibleUserDTO` | Sucesso (`idResponsibleUser` pode ser `null`) |
| 401 | vazio | Token inválido |
| 404 | `ExceptionResponse` | Tarefa inexistente |
| 500 | `ExceptionResponse` | Erro ao buscar |

### O que **não** muda para o consumidor

- Respostas de criar/atualizar tarefa (`POST`/`PUT` em `/ms/tasks`) e criar subtarefa **não expõem** `idResponsibleUser` no JSON de mutação (`ResponseTaskDTO` / `ResponseSubtaskDTO`).
- Atualizar tarefa (`PUT`) **não altera** o responsável já persistido.

### O que **muda** nas respostas (desde 2026-05-29)

- `ResponseTaskDTO` e `ResponseSubtaskDTO` incluem `createdAt`, `inProgressAt`, `completedAt`, `cancelledAt` (`OffsetDateTime` no fuso de `X-User-Timezone`).
- Listagens GET retornam `TaskViewDTO` (inclui `idResponsibleUser` e os 4 timestamps).

### O que **muda** na persistência (desde 2026-05-29)

- Criar tarefa (`POST /ms/tasks`), subtarefa (`POST /ms/tasks/subtask`) e cópia recorrente passam a gravar `id_responsible_user = idUser` no banco.
- Registros criados antes dessa data podem manter `id_responsible_user` nulo.

Detalhes: [tecnico-idResponsibleUser-criacao-default.md](./tecnico-idResponsibleUser-criacao-default.md)

### Integração BFF / frontend

1. Após criar tarefa, `GET .../responsible/...` retorna `idResponsibleUser` igual ao `idUser` da criação (sem `PUT` extra no caso padrão).
2. Ao delegar a outro usuário, chamar `PUT .../responsible/{token}` com o UUID escolhido.
3. Para remover delegação, `PUT` com `"idResponsibleUser": null`.
4. Garantir no Gateway que o `idUser` do path/body respeita a política de ownership do produto.

---

## 2. Subtarefas

### Modelo

Registros em `tasks` com:

- `idParentTask`: UUID da tarefa pai (null = tarefa raiz).
- `hasSubtasks`: `true` na tarefa pai quando existe ao menos uma subtarefa.

Subtarefa criada herda `idObjective` da pai, inicia em `TODO`, `hasSubtasks=false`, `isCopy=false`.

### Endpoints

Prefixo: `/ms/tasks/subtask`

#### `POST /{token}` — Criar subtarefa

**Header:** `X-User-Timezone` (recomendado)

**Body (`RequestSubtaskDTO`):**

```json
{
  "nameTask": "string",
  "descriptionTask": "string",
  "dateTask": "2026-05-16",
  "idParentTask": "uuid",
  "idUser": "uuid"
}
```

**Respostas:**

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseSubtaskDTO` | Sucesso |
| 400 | `ExceptionResponse` | Timezone inválido (`InvalidTimezoneException`) |
| 401 | vazio | Token inválido ou ausente |
| 404 | `ExceptionResponse` | Pai não encontrado ou `idParentTask` ausente no body |
| 500 | `ExceptionResponse` | Erro de persistência |

**`ResponseSubtaskDTO`:**

```json
{
  "id": "uuid",
  "nameTask": "string",
  "descriptionTask": "string",
  "status": "TODO",
  "dateTask": "2026-05-16",
  "idObjective": "uuid",
  "idUser": "uuid",
  "idParentTask": "uuid",
  "cancellationReason": null,
  "createdAt": "2026-05-29T10:15:00-03:00",
  "inProgressAt": null,
  "completedAt": null,
  "cancelledAt": null
}
```

#### `GET /{idUser}/{idParentTask}/{token}` — Listar subtarefas

**Header:** `X-User-Timezone` (recomendado)

**Respostas:**

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `TaskViewDTO[]` | Lista (pode ser vazia) |
| 400 | `ExceptionResponse` | Timezone inválido |
| 401 | vazio | Token inválido |
| 404 | `ExceptionResponse` | Tarefa pai inexistente |

#### `PUT /promote/{idUser}/{idTask}/{token}` — Promover a tarefa pai

**Header:** `X-User-Timezone` (recomendado)

Remove `idParentTask` da subtarefa. Atualiza `hasSubtasks` da pai anterior se era a última filha.

| HTTP | Corpo |
|------|-------|
| 200 | `ResponseSubtaskDTO` |
| 400 | `ExceptionResponse` | Timezone inválido |
| 401 | vazio |
| 404 | `ExceptionResponse` |

#### `DELETE /{idUser}/{idTask}/{token}` — Excluir subtarefa

| HTTP | Corpo |
|------|-------|
| 200 | `ResponseMessageDTO` |
| 401 | vazio |
| 404 | `ExceptionResponse` |

### Efeitos colaterais na tarefa pai

- **Atualizar objetivo** da pai (`SimpleTaskService`): subtarefas recebem o mesmo `idObjective`.
- **Excluir** a pai: subtarefas são excluídas em cascata.

---

## 3. Listagens legadas (somente tarefas pai)

**Header:** `X-User-Timezone` (recomendado em todos os GETs abaixo)

**Tipo de retorno:** `List<TaskViewDTO>` (antes: entidade JPA `Task` serializada diretamente).

Os métodos abaixo em `TaskService` aplicam `.filter(task -> task.getIdParentTask() == null)`:

- `getTasksInDateRange`
- `getTasksInDate`
- `getTasksLate`
- `getTasksStatusInDateRange`
- `getTasksStatusInDate`
- `getTasksInDateRangeByObjectiveId`
- `getTasksByObjectiveId`

**Endpoints afetados** (prefixo `/ms/tasks`):

- `GET /{idUser}/{startDate}/{endDate}/{token}`
- `GET /{idUser}/{date}/{token}`
- `GET /late/{idUser}/{token}`
- `GET /status/{status}/...` (todo, progress, done, canceled — por data ou intervalo)
- `GET /{idUser}/{idObjective}/{startDate}/{endDate}/{token}`
- `GET /objective/{idUser}/{idObjective}/{token}`

**Comportamento:** se existirem apenas subtarefas na consulta, a API pode responder **404** com exceções como `NotFoundTasksInDateException` (lista vazia após filtro).

**Integração recomendada:** para exibir checklist de uma tarefa, chamar `GET /ms/tasks/subtask/{idUser}/{idParentTask}/{token}` após carregar a tarefa pai.

---

## 4. Adiar tarefas de um dia (`postpone-day`)

### `POST /ms/tasks/date/postpone-day/{token}`

**Body (`RequestPostponeTasksForDayDTO`):**

```json
{
  "idUser": "uuid",
  "referenceDay": "2026-05-16"
}
```

`referenceDay` é dia civil (ISO-8601 date), sem hora.

### Regras de negócio

Para o usuário `idUser` e o dia `referenceDay`:

1. **TODO** na data de referência:
   - Se `idParentTask == null`: status → `LATE`, `dateTask` → `referenceDay + 1`.
   - Se `idParentTask != null` (**subtarefa**): **nenhuma alteração** (permanece `TODO` na mesma data).
2. **IN_PROGRESS** na data de referência:
   - Mantém status; `dateTask` → `referenceDay + 1` (**inclui subtarefas**).

### Resposta (`ResponsePostponeTasksForDayDTO`)

```json
{
  "referenceDay": "2026-05-16",
  "nextDay": "2026-05-17",
  "todosMarkedLateAndMoved": 1,
  "inProgressDatesMoved": 0,
  "totalTasksUpdated": 1
}
```

| Campo | Significado |
|-------|-------------|
| `todosMarkedLateAndMoved` | Tarefas pai `TODO` que viraram `LATE` e foram movidas |
| `inProgressDatesMoved` | Tarefas `IN_PROGRESS` (pai ou filha) com data adiada |
| `totalTasksUpdated` | Soma dos dois contadores |

**Respostas HTTP:**

| HTTP | Condição |
|------|----------|
| 200 | Processamento concluído (pode ser 0 tarefas alteradas) |
| 401 | Token inválido |
| 500 | `DataBaseException` |

### Outro endpoint de data

- `PUT /ms/tasks/date/{token}` — atualiza data de **uma** tarefa via `RequestUpdateDateTaskDTO` (`idTask`, `idUser`, `dateTask`). Requer header `X-User-Timezone`. Resposta: `ResponseTaskDTO` com timestamps. **Não altera** os campos de ciclo de vida além da conversão para exibição.

---

## 5. Autorização por dono da tarefa

**Antes:** `findById(idUser, idTask)` lançava `UserWithoutAuthorizationAboutTheTaskException` → HTTP **403** quando `task.idUser != idUser`.

**Agora:** `findById` retorna a tarefa pelo `idTask` apenas; `idUser` no path **não é validado** nesta camada.

**Implicação:** clientes que chamam diretamente o Tasks-Service devem validar ownership no Gateway ou no serviço chamador. Microsserviços internos com token de máquina podem operar em nome de qualquer `idUser` informado no contrato.

**Respostas de erro globais** (`ExceptionResponse`: `{ "simpleName", "message" }`):

| Exceção | HTTP |
|---------|------|
| `InvalidTimezoneException` | 400 |
| `NotFoundException` | 404 |
| `NotFoundTasksInDateException` e similares | 404 |
| `DataBaseException` | 500 |

---

## 6. DTOs — referência rápida

### `RequestUpdateResponsibleUserDTO`

| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| idTask | UUID | sim |
| idUser | UUID | sim |
| idResponsibleUser | UUID | não (`null` desatribui) |

### `ResponseResponsibleUserDTO`

| Campo | Tipo |
|-------|------|
| idTask | UUID |
| idResponsibleUser | UUID ou `null` |

### `RequestSubtaskDTO`

| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| nameTask | string | sim |
| descriptionTask | string | sim |
| dateTask | LocalDate (ISO date) | sim |
| idParentTask | UUID | sim |
| idUser | UUID | sim |

### `RequestPostponeTasksForDayDTO`

| Campo | Tipo |
|-------|------|
| idUser | UUID |
| referenceDay | LocalDate |

### `ResponsePostponeTasksForDayDTO`

| Campo | Tipo |
|-------|------|
| referenceDay | LocalDate |
| nextDay | LocalDate |
| todosMarkedLateAndMoved | int |
| inProgressDatesMoved | int |
| totalTasksUpdated | int |

### `ResponseTaskDTO` / `ResponseSubtaskDTO` (campos de timestamp)

| Campo | Tipo |
|-------|------|
| createdAt | OffsetDateTime ou `null` |
| inProgressAt | OffsetDateTime ou `null` |
| completedAt | OffsetDateTime ou `null` |
| cancelledAt | OffsetDateTime ou `null` |

### `TaskViewDTO` (GETs)

Campos da entidade `Task` expostos na API + os 4 timestamps acima + `idResponsibleUser`. Ver exemplo completo em [tecnico-timestamps-fuso-horario.md](./tecnico-timestamps-fuso-horario.md).

---

## 7. Swagger

Contratos interativos: `http://localhost:8085/swagger-ui/index.html`  
Tags: **Responsible User Task**, **Subtasks**, **Date Task**, **Tasks**.

Endpoints de tarefa/subtarefa/data documentam o header `X-User-Timezone` via `@RequestHeader` no código.

---

## 8. Timestamps e fuso horário

Resumo da feature de 2026-05-29. **Guia completo:** [tecnico-timestamps-fuso-horario.md](./tecnico-timestamps-fuso-horario.md)

| Item | Detalhe |
|------|---------|
| Header | `X-User-Timezone` (IANA); fallback `America/Sao_Paulo` |
| Persistência | UTC (`created_at`, `in_progress_at`, `completed_at`, `cancelled_at`) |
| Resposta | `OffsetDateTime` ISO-8601 com offset do usuário |
| Retransição | Última entrada em IN PROGRESS / DONE / CANCELLED sobrescreve o campo |
| Excluídos | `postpone-day`, status `LATE`, `PUT` genérico de tarefa não gravam timestamps de status |
| Erro | Timezone inválido → HTTP 400 |

**Notas de segurança:** [seguranca-notas-timestamps-fuso-horario.md](./seguranca-notas-timestamps-fuso-horario.md)

---

## Histórico de commits relacionados

| Data | Commit | Descrição |
|------|--------|-----------|
| 2026-04-29 | `df74bda` | Endpoint `postpone-day` e `DateTaskService.postponeTasksForReferenceDay` |
| 2026-05-06 | `5519613` | API de subtarefas |
| 2026-05-06 | `44c4656` | Filtro de subtarefas nas listagens legadas |
| 2026-05-15 | `747c529` | Remoção da validação dono×tarefa em `findById` |
| 2026-05-16 | `ee8ca4f` | Subtarefas excluídas do fluxo automático `TODO` → `LATE` no adiamento |
| 2026-05-17 | — | API `/ms/tasks/responsible`, campo `idResponsibleUser` e testes unitários do serviço |
| 2026-05-29 | — | `idResponsibleUser` preenchido automaticamente na criação (tarefa, subtarefa, cópia recorrente) |
| 2026-05-29 | — | Timestamps de ciclo de vida (`createdAt`, etc.) + header `X-User-Timezone` + `TaskViewDTO` nos GETs |
