# Changelog

Todas as mudanças relevantes deste projeto são documentadas neste arquivo.

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e as versões seguem [Semantic Versioning](https://semver.org/lang/pt-BR/) quando aplicável.

## [Unreleased]

### Adicionado

- API de **usuario responsavel por tarefa** (`/ms/tasks/responsible`): `PUT /{token}` para atribuir ou desatribuir (`idResponsibleUser` nulo) e `GET /{idUser}/{idTask}/{token}` para consultar o responsavel.
- Campo `idResponsibleUser` (UUID, nullable) na entidade `Task`, persistido como `id_responsible_user` no MySQL.
- DTOs `RequestUpdateResponsibleUserDTO` e `ResponseResponsibleUserDTO` (contrato enxuto, sem alterar `ResponseTaskDTO`).
- Servico e controller dedicados: `ResponsibleUserTaskService`, `ResponsibleUserTaskController` (tag Swagger **Responsible User Task**).
- Testes unitarios: `ResponsibleUserTaskServiceSuccess` e `ResponsibleUserTaskServiceFaliure`.
- API de **subtarefas** (`/ms/tasks/subtask`): criar, listar por tarefa pai, promover a tarefa independente e excluir.
- Endpoint **adiar tarefas de um dia** `POST /ms/tasks/date/postpone-day/{token}` com DTOs `RequestPostponeTasksForDayDTO` e `ResponsePostponeTasksForDayDTO`.
- Campos de modelo `idParentTask` e `hasSubtasks` na entidade `Task` para hierarquia pai/filho.
- Teste unitário garantindo que subtarefas em `TODO` não entram no fluxo automático de atraso no adiamento diário.

### Alterado

- Construtor `@AllArgsConstructor` de `Task` passa a exigir 14 argumentos; testes existentes que instanciam `new Task(...)` foram ajustados com o 14º parametro (`idResponsibleUser`, em geral `null`).
- Consultas legadas de listagem em `TaskService` passam a retornar **apenas tarefas pai** (`idParentTask == null`); subtarefas devem ser obtidas via `/ms/tasks/subtask`.
- No adiamento diário, tarefas `TODO` com `idParentTask` preenchido **não** são marcadas como `LATE` nem têm a data alterada; tarefas `IN_PROGRESS` do dia continuam tendo apenas a data adiada (+1 dia), inclusive subtarefas.
- `TaskRepository.findById` deixa de validar se `idUser` do path corresponde ao dono da tarefa; a autorização por dono deve ser garantida pelo **Gateway/BFF** ou pelo chamador (token de máquina).

### Notas de implantacao

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
