# Tasks-Service — Timestamps de tarefas com fuso horário do usuário

Documento técnico para **frontend**, **BFF**, **QA** e demais consumidores da API.

**Versão do artefato:** `0.0.1-SNAPSHOT`  
**Base URL local:** `http://localhost:8085`  
**Data da feature:** 2026-05-29

Documento consolidado de alterações: [tecnico-integracao-alteracoes-recentes.md](./tecnico-integracao-alteracoes-recentes.md)  
Notas de segurança: [seguranca-notas-timestamps-fuso-horario.md](./seguranca-notas-timestamps-fuso-horario.md)

---

## Resumo

A API passa a registrar **quatro momentos** do ciclo de vida da tarefa:

| Campo JSON | Coluna MySQL | Preenchido quando |
|------------|--------------|-------------------|
| `createdAt` | `created_at` | Criação (tarefa, subtarefa, cópia recorrente) |
| `inProgressAt` | `in_progress_at` | Status → `IN PROGRESS` |
| `completedAt` | `completed_at` | Status → `DONE` |
| `cancelledAt` | `cancelled_at` | Status → `CANCELLED` |

- **Persistência:** UTC (`Instant` no servidor / `TIMESTAMP(6)` no MySQL).
- **Resposta HTTP:** `OffsetDateTime` ISO-8601 no fuso informado pelo header `X-User-Timezone` (ex.: `2026-05-29T12:00:00-03:00`).
- **Registros legados:** timestamps permanecem `null` até haver nova transição relevante.

### Breaking changes

| Antes | Depois |
|-------|--------|
| GETs retornavam entidade JPA `Task` (serialização crua) | GETs retornam `TaskViewDTO` (DTO estável + timestamps convertidos) |
| Respostas de mutação sem campos de data/hora de ciclo | `ResponseTaskDTO` / `ResponseSubtaskDTO` incluem 4 `OffsetDateTime` |
| Sem header de fuso | Header `X-User-Timezone` recomendado em mutações e consultas de tarefa |

---

## Header HTTP — `X-User-Timezone`

| Propriedade | Valor |
|-------------|-------|
| Nome | `X-User-Timezone` |
| Formato | Identificador IANA (ex.: `America/Sao_Paulo`, `UTC`, `Europe/Lisbon`) |
| Obrigatório | Recomendado; se ausente ou em branco, o serviço usa **`America/Sao_Paulo`** |
| Inválido | HTTP **400** — `InvalidTimezoneException` |

### Exemplo no browser (PWA / frontend)

```javascript
const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
// "America/Sao_Paulo"
```

### Exemplo no BFF (repasse ao microsserviço)

```http
POST /ms/tasks/{token} HTTP/1.1
X-User-Timezone: America/Sao_Paulo
Content-Type: application/json
```

O BFF deve **repassar** o fuso do usuário final em todas as chamadas listadas abaixo. A gravação no banco usa sempre `Instant.now()` (UTC); o header afeta **apenas a serialização** da resposta.

---

## Endpoints afetados

### Com header `X-User-Timezone`

**Prefixo `/ms/tasks` — mutações**

| Método | Padrão de rota | Resposta |
|--------|----------------|----------|
| POST | `/ms/tasks/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/{idUser}/{idTask}/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/repeat/{idUser}/{idTask}/{endDate}/{token}` | `ResponseUpdateRepeatTaskDTO` (contém tarefa atualizada) |
| PUT | `/ms/tasks/status/todo/{idUser}/{idTask}/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/status/progress/{idUser}/{idTask}/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/status/done/{idUser}/{idTask}/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/status/late/{idUser}/{idTask}/{token}` | `ResponseTaskDTO` |
| PUT | `/ms/tasks/status/canceled/{token}` | `ResponseTaskDTO` |

**Prefixo `/ms/tasks` — consultas (retorno `TaskViewDTO` ou `List<TaskViewDTO>`)**

| Método | Padrão de rota |
|--------|----------------|
| GET | `/ms/tasks/{idUser}/{startDate}/{endDate}/{token}` |
| GET | `/ms/tasks/{idUser}/{idObjective}/{startDate}/{endDate}/{token}` |
| GET | `/ms/tasks/objective/{idUser}/{idObjective}/{token}` |
| GET | `/ms/tasks/{idUser}/{date}/{token}` |
| GET | `/ms/tasks/late/{idUser}/{token}` |
| GET | `/ms/tasks/status/todo/{idUser}/{startDate}/{endDate}/{token}` |
| GET | `/ms/tasks/status/todo/{idUser}/{date}/{token}` |
| GET | `/ms/tasks/status/progress/...` |
| GET | `/ms/tasks/status/done/...` |
| GET | `/ms/tasks/status/canceled/...` |
| GET | `/ms/tasks/task/{idUser}/{idTask}/{token}` |

**Prefixo `/ms/tasks/subtask`**

| Método | Rota | Resposta |
|--------|------|----------|
| POST | `/ms/tasks/subtask/{token}` | `ResponseSubtaskDTO` |
| GET | `/ms/tasks/subtask/{idUser}/{idParentTask}/{token}` | `List<TaskViewDTO>` |
| PUT | `/ms/tasks/subtask/promote/{idUser}/{idTask}/{token}` | `ResponseSubtaskDTO` |

**Prefixo `/ms/tasks/date`**

| Método | Rota | Resposta |
|--------|------|----------|
| PUT | `/ms/tasks/date/{token}` | `ResponseTaskDTO` |

### Sem header `X-User-Timezone` (inalterados nesta feature)

| Grupo | Exemplos |
|-------|----------|
| Exclusões | `DELETE` em tarefas e subtarefas |
| Adiamento em lote | `POST /ms/tasks/date/postpone-day/{token}` |
| Responsável | `/ms/tasks/responsible/*` |
| Recorrência (criação) | `POST /ms/tasks/repeat/...` (sem timezone na resposta de contagem) |

---

## Contratos JSON

### Campos de timestamp (comuns)

Todos são `OffsetDateTime` ou `null`, serializados em ISO-8601 com offset:

```json
"createdAt": "2026-05-29T10:15:00-03:00",
"inProgressAt": null,
"completedAt": null,
"cancelledAt": null
```

### `ResponseTaskDTO` (mutações de tarefa)

```json
{
  "id": "uuid",
  "nameTask": "string",
  "descriptionTask": "string",
  "status": "TODO",
  "dateTask": "2026-05-29",
  "idObjective": "uuid",
  "idUser": "uuid",
  "cancellationReason": null,
  "createdAt": "2026-05-29T10:15:00-03:00",
  "inProgressAt": null,
  "completedAt": null,
  "cancelledAt": null
}
```

**Nota:** `idResponsibleUser` **não** está neste DTO. Use `GET /ms/tasks/responsible/...` ou `TaskViewDTO` nas listagens.

### `ResponseSubtaskDTO` (criar / promover subtarefa)

Mesmos 4 campos de timestamp, mais:

```json
{
  "id": "uuid",
  "nameTask": "string",
  "descriptionTask": "string",
  "status": "TODO",
  "dateTask": "2026-05-29",
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

### `TaskViewDTO` (consultas GET)

Espelha os campos expostos anteriormente pela entidade `Task`, **incluindo** `idResponsibleUser` e os 4 timestamps convertidos:

```json
{
  "id": "uuid",
  "nameTask": "string",
  "descriptionTask": "string",
  "status": "TODO",
  "dateTask": "2026-05-29",
  "idObjective": "uuid",
  "idUser": "uuid",
  "idParentTask": null,
  "idOriginalTask": null,
  "hasSubtasks": false,
  "blockedByObjective": false,
  "isCopy": false,
  "cancellationReason": null,
  "idResponsibleUser": "uuid",
  "createdAt": "2026-05-29T10:15:00-03:00",
  "inProgressAt": null,
  "completedAt": null,
  "cancelledAt": null
}
```

---

## Semântica de negócio

### Quando cada timestamp é gravado

| Evento | Campo atualizado |
|--------|------------------|
| `POST /ms/tasks`, `POST /subtask`, cópia recorrente | `createdAt` |
| Status → `IN PROGRESS` | `inProgressAt` |
| Status → `DONE` | `completedAt` |
| Status → `CANCELLED` | `cancelledAt` |
| Status → `TODO` ou `LATE` | **Nenhum** dos quatro campos |
| `PUT` atualizar tarefa (nome, objetivo, etc.) | **Nenhum** timestamp de status alterado |
| `PUT /date` (data civil) | **Nenhum** timestamp alterado |
| `POST /postpone-day` | **Nenhum** timestamp alterado (apenas `status`/`dateTask`) |

### Retransições

Ao voltar para `IN PROGRESS`, `DONE` ou `CANCELLED`, o campo correspondente é **sempre sobrescrito** com o instante da última transição (não é histórico acumulado).

### Registros legados

Tarefas criadas antes do deploy mantêm `created_at` e demais colunas `NULL`. A API retorna `null` nos campos JSON até que ocorra a primeira transição relevante.

---

## Respostas HTTP

| HTTP | Condição | Corpo |
|------|----------|-------|
| 200 | Sucesso | DTO com timestamps no fuso informado |
| 400 | `X-User-Timezone` inválido | `ExceptionResponse`: `{ "simpleName": "InvalidTimezoneException", "message": "Invalid timezone: ..." }` |
| 401 | Token inválido ou ausente | vazio |
| 404 | Tarefa/recurso não encontrado | `ExceptionResponse` |
| 500 | Erro de persistência | `ExceptionResponse` |

Processamento continua **assíncrono** (`CompletableFuture`); clientes HTTP devem aguardar conclusão da requisição assíncrona (Spring MVC resolve automaticamente).

---

## Implantação (DDL)

Aplicar manualmente quando `ddl-auto` não estiver ativo:

```sql
ALTER TABLE tasks ADD COLUMN created_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN in_progress_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN completed_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN cancelled_at TIMESTAMP(6) NULL;
```

Script versionado: `tasks/src/main/resources/db/task-timestamps.sql`

---

## Checklist de migração (consumidor)

- [ ] Enviar `X-User-Timezone` em todas as mutações e GETs de tarefa/subtarefa listados acima.
- [ ] Obter o fuso do browser via `Intl.DateTimeFormat().resolvedOptions().timeZone` e repassar pelo BFF.
- [ ] Parsear respostas como `OffsetDateTime` (ISO-8601 com offset), não como UTC fixo.
- [ ] Tratar `null` nos quatro campos (registros legados ou status ainda não atingido).
- [ ] Atualizar modelos TypeScript/interfaces: GET retorna `TaskViewDTO`, não entidade JPA.
- [ ] Exibir `idResponsibleUser` a partir de `TaskViewDTO` nas listagens (ou continuar usando `/responsible` para detalhe).
- [ ] Tratar HTTP **400** para timezone inválido (validar IANA no cliente antes de chamar, se possível).
- [ ] QA: validar transições TODO → IN PROGRESS → DONE e cancelamento com timestamps preenchidos na resposta.

---

## Swagger

Contratos interativos incluem o header nos endpoints afetados:  
`http://localhost:8085/swagger-ui/index.html` — tags **Tasks**, **Subtasks**, **Date Task**.
