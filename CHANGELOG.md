# Changelog

Todas as mudanças relevantes deste projeto são documentadas neste arquivo.

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e as versões seguem [Semantic Versioning](https://semver.org/lang/pt-BR/) quando aplicável.

## [Unreleased]

### Adicionado

- Campos de timestamp de ciclo de vida na entidade `Task`: `createdAt`, `inProgressAt`, `completedAt`, `cancelledAt` (UTC / `Instant`), com DDL em `tasks/src/main/resources/db/task-timestamps.sql`.
- Header HTTP `X-User-Timezone` (IANA) para serializar timestamps no fuso do usuario; utilitarios `UserTimezoneResolver`, `TaskTimestampHelper` e excecao `InvalidTimezoneException` (HTTP 400).
- DTO `TaskViewDTO` para respostas de GET (substitui serializacao crua da entidade JPA); timestamps `OffsetDateTime` em `ResponseTaskDTO` e `ResponseSubtaskDTO`.
- Testes unitarios da feature: `UserTimezoneResolverTest`, `TaskTimestampHelperTest`, `TaskTimestampsDtoTest`, `SimpleTaskServiceTimestampsSuccess`, `SubtaskServiceTimestampsSuccess`, `DateTaskServiceTimestampsSuccess`, `TaskTimestampsControllerWebMvcTest`, `RestExceptionHandlerTimezoneTest` e extensoes correlatas.
- API de **usuario responsavel por tarefa** (`/ms/tasks/responsible`): `PUT /{token}` para atribuir ou desatribuir (`idResponsibleUser` nulo) e `GET /{idUser}/{idTask}/{token}` para consultar o responsavel.
- Campo `idResponsibleUser` (UUID, nullable) na entidade `Task`, persistido como `id_responsible_user` no MySQL.
- DTOs `RequestUpdateResponsibleUserDTO` e `ResponseResponsibleUserDTO` (contrato enxuto, sem alterar `ResponseTaskDTO`).
- Servico e controller dedicados: `ResponsibleUserTaskService`, `ResponsibleUserTaskController` (tag Swagger **Responsible User Task**).
- Testes unitarios: `ResponsibleUserTaskServiceSuccess` e `ResponsibleUserTaskServiceFaliure`.
- Testes unitarios da regra **idResponsibleUser = idUser na criacao** (Domain, Service, Repository e Controller): `TaskDefaultResponsibleUserSuccess`, extensoes em `SimpleTaskServiceSuccess`, `SubtaskServiceSuccess`, `TaskRepositorySucess`, `TaskControllerDefaultResponsibleUserSuccess`, `SubtaskControllerDefaultResponsibleUserSuccess`.
- API de **subtarefas** (`/ms/tasks/subtask`): criar, listar por tarefa pai, promover a tarefa independente e excluir.
- Endpoint **adiar tarefas de um dia** `POST /ms/tasks/date/postpone-day/{token}` com DTOs `RequestPostponeTasksForDayDTO` e `ResponsePostponeTasksForDayDTO`.
- Campos de modelo `idParentTask` e `hasSubtasks` na entidade `Task` para hierarquia pai/filho.
- Teste unitário garantindo que subtarefas em `TODO` não entram no fluxo automático de atraso no adiamento diário.

### Alterado

- GETs de tarefas e subtarefas passam a retornar `TaskViewDTO` (breaking change no JSON: DTO estavel + timestamps convertidos + `idResponsibleUser` nas listagens).
- Respostas de mutacao (`POST`/`PUT` de tarefa, subtarefa, status, data, recorrencia) incluem quatro campos `OffsetDateTime` no fuso informado por `X-User-Timezone`.
- Construtor `@AllArgsConstructor` de `Task` passa a exigir **18** argumentos (inclui `idResponsibleUser` e 4 `Instant` de timestamp); testes existentes que instanciam `new Task(...)` foram ajustados.
- Na criacao de tarefa (`POST /ms/tasks`), subtarefa (`POST /ms/tasks/subtask`) e copia recorrente, `idResponsibleUser` passa a ser persistido automaticamente com o mesmo valor de `idUser` (responsavel padrao = dono). Registros antigos com `id_responsible_user` nulo nao sao migrados retroativamente.
- Consultas legadas de listagem em `TaskService` passam a retornar **apenas tarefas pai** (`idParentTask == null`); subtarefas devem ser obtidas via `/ms/tasks/subtask`.
- No adiamento diário, tarefas `TODO` com `idParentTask` preenchido **não** são marcadas como `LATE` nem têm a data alterada; tarefas `IN_PROGRESS` do dia continuam tendo apenas a data adiada (+1 dia), inclusive subtarefas.
- `TaskRepository.findById` deixa de validar se `idUser` do path corresponde ao dono da tarefa; a autorização por dono deve ser garantida pelo **Gateway/BFF** ou pelo chamador (token de máquina).

### Notas de implantacao

- Aplicar DDL de timestamps antes do deploy (se `ddl-auto` nao criar as colunas automaticamente):

```sql
ALTER TABLE tasks ADD COLUMN created_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN in_progress_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN completed_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN cancelled_at TIMESTAMP(6) NULL;
```

- BFF deve repassar o header `X-User-Timezone` do browser nas chamadas de tarefa/subtarefa (ver [docs/tecnico-timestamps-fuso-horario.md](./docs/tecnico-timestamps-fuso-horario.md)).
- Aplicar DDL antes do deploy da feature de responsavel (se `ddl-auto` nao criar a coluna automaticamente):

```sql
ALTER TABLE tasks ADD COLUMN id_responsible_user CHAR(36) NULL;
```

- Gateway/BFF ainda precisa expor rotas que repassem o token de maquina para `/ms/tasks/responsible` (fora do escopo deste servico).

### Removido

- Exceção `UserWithoutAuthorizationAboutTheTaskException` e o handler HTTP `403` associado no `RestExceptionHandler`.

## [0.0.1-SNAPSHOT] - 2026-04-29

### Adicionado

- README inicial com visão do produto, stack e referências ao Swagger/Actuator.
- Fluxo de atualização de data de tarefa em `DateTaskController`.
