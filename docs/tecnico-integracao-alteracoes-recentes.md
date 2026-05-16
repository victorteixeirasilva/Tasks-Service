# Tasks-Service — Notas técnicas de integração (alterações recentes)

Documento para desenvolvedores que consomem este microsserviço (frontend, BFF, Motivation-Service ou outros clientes Feign).

**Versão do artefato:** `0.0.1-SNAPSHOT`  
**Base URL local:** `http://localhost:8085`  
**Autenticação:** token de máquina no path (`{token}`), validado via Auth-For-MService (`TokenService.validateToken`).

---

## Resumo das mudanças

| Área | Mudança | Impacto no consumidor |
|------|---------|------------------------|
| Subtarefas | Nova API `/ms/tasks/subtask` | Usar endpoints dedicados; não esperar subtarefas nas listagens antigas |
| Listagens | Filtro `idParentTask == null` | Listas por data/status/objetivo retornam só tarefas pai |
| Adiamento diário | Novo `POST .../postpone-day/{token}` | Integrar job/noturno ou ação do usuário para virar `LATE` e mover datas |
| Adiamento + subtarefas | `TODO` filho ignorado no fluxo `LATE` | Subtarefas não viram atrasadas automaticamente |
| Autorização | Removida checagem dono×tarefa no repositório | BFF deve garantir que `idUser` do path é o dono (ou política equivalente) |

---

## 1. Subtarefas

### Modelo

Registros em `tasks` com:

- `idParentTask`: UUID da tarefa pai (null = tarefa raiz).
- `hasSubtasks`: `true` na tarefa pai quando existe ao menos uma subtarefa.

Subtarefa criada herda `idObjective` da pai, inicia em `TODO`, `hasSubtasks=false`, `isCopy=false`.

### Endpoints

Prefixo: `/ms/tasks/subtask`

#### `POST /{token}` — Criar subtarefa

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
  "cancellationReason": null
}
```

#### `GET /{idUser}/{idParentTask}/{token}` — Listar subtarefas

**Respostas:**

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `Task[]` (entidade JPA) | Lista (pode ser vazia) |
| 401 | vazio | Token inválido |
| 404 | `ExceptionResponse` | Tarefa pai inexistente |

#### `PUT /promote/{idUser}/{idTask}/{token}` — Promover a tarefa pai

Remove `idParentTask` da subtarefa. Atualiza `hasSubtasks` da pai anterior se era a última filha.

| HTTP | Corpo |
|------|-------|
| 200 | `ResponseSubtaskDTO` |
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

## 2. Listagens legadas (somente tarefas pai)

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

## 3. Adiar tarefas de um dia (`postpone-day`)

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

### Outro endpoint de data (inalterado neste pacote)

- `PUT /ms/tasks/date/{token}` — atualiza data de **uma** tarefa via `RequestUpdateDateTaskDTO` (`idTask`, `idUser`, `dateTask`).

---

## 4. Autorização por dono da tarefa

**Antes:** `findById(idUser, idTask)` lançava `UserWithoutAuthorizationAboutTheTaskException` → HTTP **403** quando `task.idUser != idUser`.

**Agora:** `findById` retorna a tarefa pelo `idTask` apenas; `idUser` no path **não é validado** nesta camada.

**Implicação:** clientes que chamam diretamente o Tasks-Service devem validar ownership no Gateway ou no serviço chamador. Microsserviços internos com token de máquina podem operar em nome de qualquer `idUser` informado no contrato.

**Respostas de erro globais** (`ExceptionResponse`: `{ "simpleName", "message" }`):

| Exceção | HTTP |
|---------|------|
| `NotFoundException` | 404 |
| `NotFoundTasksInDateException` e similares | 404 |
| `DataBaseException` | 500 |

---

## 5. DTOs — referência rápida

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

---

## 6. Swagger

Contratos interativos: `http://localhost:8085/swagger-ui/index.html`  
Tags: **Subtasks**, **Date Task**, **Tasks**.

---

## Histórico de commits relacionados

| Data | Commit | Descrição |
|------|--------|-----------|
| 2026-04-29 | `df74bda` | Endpoint `postpone-day` e `DateTaskService.postponeTasksForReferenceDay` |
| 2026-05-06 | `5519613` | API de subtarefas |
| 2026-05-06 | `44c4656` | Filtro de subtarefas nas listagens legadas |
| 2026-05-15 | `747c529` | Remoção da validação dono×tarefa em `findById` |
| 2026-05-16 | `ee8ca4f` | Subtarefas excluídas do fluxo automático `TODO` → `LATE` no adiamento |
