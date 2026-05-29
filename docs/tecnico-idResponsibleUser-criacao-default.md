# Tasks-Service — Responsável padrão na criação (`idResponsibleUser = idUser`)

Documento técnico para integradores (frontend, BFF, QA e microsserviços consumidores).

**Data da alteração:** 2026-05-29  
**Versão do artefato:** `0.0.1-SNAPSHOT`  
**Base URL local:** `http://localhost:8085`  
**Autenticação:** token de máquina no path (`{token}`), validado via Auth-For-MService.

Documento complementar: [tecnico-integracao-alteracoes-recentes.md](./tecnico-integracao-alteracoes-recentes.md) (API `/ms/tasks/responsible`).

---

## Resumo

Ao criar uma tarefa, subtarefa ou cópia recorrente, o serviço passa a persistir **`id_responsible_user` com o mesmo UUID de `idUser`**. O criador/dono da tarefa é o responsável inicial pela execução.

**Motivação:** evitar que o consumidor precise chamar `PUT /ms/tasks/responsible` apenas para obter o responsável padrão após a criação.

**Importante:** os DTOs de resposta de criação (`ResponseTaskDTO`, `ResponseSubtaskDTO`) **não expõem** `idResponsibleUser`. Para exibir o responsável na UI, use `GET /ms/tasks/responsible/...`.

---

## Endpoints envolvidos (contrato HTTP inalterado)

Nenhuma URL, verbo ou parâmetro foi alterado. A mudança é apenas na **persistência** do campo `id_responsible_user`.

| Operação | Método e path | Efeito em `id_responsible_user` |
|----------|---------------|----------------------------------|
| Criar tarefa | `POST /ms/tasks/{token}` | `= idUser` do body |
| Criar subtarefa | `POST /ms/tasks/subtask/{token}` | `= idUser` do body |
| Repetir tarefa (cópias) | `POST /ms/tasks/repeat/{idUser}/{idTask}/{startDate}/{endDate}/{token}` | `= idUser` da tarefa original em cada cópia gerada |

---

## DTOs

### Request (sem alteração)

**`RequestTaskDTO`** (`POST /ms/tasks/{token}`):

```json
{
  "nameTask": "string",
  "descriptionTask": "string",
  "dateTask": "2026-05-29",
  "idObjective": "uuid",
  "idUser": "uuid"
}
```

**`RequestSubtaskDTO`** (`POST /ms/tasks/subtask/{token}`):

```json
{
  "nameTask": "string",
  "descriptionTask": "string",
  "dateTask": "2026-05-29",
  "idParentTask": "uuid",
  "idUser": "uuid"
}
```

Não há campo `idResponsibleUser` no request — o valor é derivado no backend a partir de `idUser`.

### Response de criação (sem alteração)

**`ResponseTaskDTO`** e **`ResponseSubtaskDTO`** continuam **sem** o campo `idResponsibleUser`. Exemplo de resposta de tarefa:

```json
{
  "id": "uuid",
  "nameTask": "string",
  "descriptionTask": "string",
  "status": "TODO",
  "dateTask": "2026-05-29",
  "idObjective": "uuid",
  "idUser": "uuid",
  "cancellationReason": null
}
```

---

## Comportamento por fluxo

| Fluxo | Valor persistido em `id_responsible_user` | Consultável via GET responsible? |
|-------|---------------------------------------------|----------------------------------|
| Nova tarefa | `idUser` do body | Sim — retorna o mesmo UUID |
| Nova subtarefa | `idUser` do body | Sim |
| Cópia recorrente | `idUser` da tarefa base | Sim |
| Atualizar tarefa (`PUT /ms/tasks/...`) | Não altera o responsável | Valor anterior mantido |
| Delegar (`PUT /ms/tasks/responsible/...`) | UUID informado no body | Sim |
| Desatribuir (`PUT` com `idResponsibleUser: null`) | `null` | Sim — retorna `null` |

---

## Como consultar o responsável

**`GET /ms/tasks/responsible/{idUser}/{idTask}/{token}`**

Resposta (`ResponseResponsibleUserDTO`):

```json
{
  "idTask": "uuid",
  "idResponsibleUser": "uuid"
}
```

Após criar uma tarefa com sucesso, uma chamada a este endpoint deve retornar `idResponsibleUser` igual ao `idUser` usado na criação.

---

## Como delegar ou remover responsável

Inalterado. Ver seção 1 de [tecnico-integracao-alteracoes-recentes.md](./tecnico-integracao-alteracoes-recentes.md).

- **Delegar:** `PUT /ms/tasks/responsible/{token}` com body contendo `idResponsibleUser` (UUID do novo responsável).
- **Remover:** mesmo endpoint com `"idResponsibleUser": null`.

---

## Retornos HTTP possíveis (criação)

### `POST /ms/tasks/{token}`

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseTaskDTO` | Tarefa criada (`idResponsibleUser` persistido, mas **não** no JSON) |
| 401 | vazio | Token inválido ou ausente |
| 404 | `ExceptionResponse` | Recurso relacionado não encontrado (se aplicável) |
| 500 | `ExceptionResponse` | Erro de persistência |

### `POST /ms/tasks/subtask/{token}`

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseSubtaskDTO` | Subtarefa criada |
| 401 | vazio | Token inválido |
| 404 | `ExceptionResponse` | Pai inexistente ou `idParentTask` ausente |
| 500 | `ExceptionResponse` | Erro de persistência |

### `POST /ms/tasks/repeat/...`

| HTTP | Corpo | Condição |
|------|-------|----------|
| 200 | `ResponseRepeatTaskDTO` | Cópias geradas conforme dias da semana |
| 401 | vazio | Token inválido |
| 404 | `ExceptionResponse` | Tarefa original não encontrada |
| 500 | `ExceptionResponse` | Erro de persistência |

---

## Dados legados

Tarefas criadas **antes** desta alteração podem ter `id_responsible_user = null` no banco. Não há job de migração automática. Integradores podem:

- tratar `null` como "não atribuído" na UI, ou
- chamar `PUT /ms/tasks/responsible/{token}` para definir explicitamente.

---

## Checklist QA

- [ ] Criar tarefa via `POST /ms/tasks/{token}` → `GET .../responsible/...` retorna `idResponsibleUser == idUser`.
- [ ] Criar subtarefa via `POST /ms/tasks/subtask/{token}` → GET responsible retorna `idResponsibleUser == idUser`.
- [ ] Repetir tarefa → cada cópia nova tem responsável igual ao `idUser` da série.
- [ ] Delegar a outro UUID via `PUT /ms/tasks/responsible/{token}` → GET retorna o novo UUID.
- [ ] Remover delegação com `idResponsibleUser: null` → GET retorna `null`.
- [ ] Resposta de `POST /ms/tasks` **não** contém campo `idResponsibleUser` (contrato legado).

---

## Referências

- [CHANGELOG.md](../CHANGELOG.md) — seção `[Unreleased]`
- [seguranca-notas-idResponsibleUser-criacao.md](./seguranca-notas-idResponsibleUser-criacao.md)
- Swagger: `http://localhost:8085/swagger-ui/index.html`
