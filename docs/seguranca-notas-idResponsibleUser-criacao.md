# Notas de segurança — Responsável padrão na criação (`idResponsibleUser = idUser`)

Análise objetiva da alteração de 2026-05-29. Complementa [tecnico-idResponsibleUser-criacao-default.md](./tecnico-idResponsibleUser-criacao-default.md).

---

## Escopo analisado

Alterações em:

- `Task.java` — construtores definem `idResponsibleUser = dto.idUser()`
- `SubtaskService.java` — `setIdResponsibleUser(dto.idUser())` na criação de subtarefa

---

## Secrets e dados sensíveis no Git

| Verificação | Resultado |
|-------------|-----------|
| Credenciais, tokens ou chaves no diff | **Nenhum** identificado |
| `application.properties` alterado nesta entrega | **Não** |

---

## Modelo de autorização

| Aspecto | Situação |
|---------|----------|
| Token de máquina no path | Continua obrigatório nos endpoints afetados; validado por `TokenService` (Auth-For-MService) |
| Checagem dono×tarefa no repositório | **Removida** anteriormente — responsabilidade do **Gateway/BFF** |
| `idResponsibleUser = idUser` na criação | Default coerente: quem cria (dono) é responsável inicial; não amplia privilégios além do ownership já assumido pelo fluxo |

---

## Validação do UUID do responsável

O serviço **não valida** (antes nem depois desta alteração) se o UUID em `idResponsibleUser` ou `idUser` corresponde a um usuário existente em outro microsserviço. Isso é uma **limitação conhecida** da API `/ms/tasks/responsible` e permanece igual para o default na criação.

**Recomendação para BFF:** validar existência do usuário antes de criar tarefa ou delegar, se a regra de negócio exigir.

---

## Exposição do campo na API

| Camada | Expõe `idResponsibleUser`? |
|--------|----------------------------|
| `POST /ms/tasks` (response) | Não |
| `POST /ms/tasks/subtask` (response) | Não |
| Listagens legadas por data/status/objetivo | Não |
| `GET /ms/tasks/responsible/...` | Sim (endpoint dedicado) |

**Efeito:** reduz superfície de vazamento em contratos legados, mas exige chamada explícita ao `/responsible` para exibir o responsável — integradores não devem inferir o valor apenas pelo JSON de criação.

---

## Delegação posterior

Atribuir responsável a **outro** usuário ou remover (`null`) continua exclusivamente via `PUT /ms/tasks/responsible/{token}`. A alteração de criação não bypassa esse fluxo para delegação cruzada.

---

## Riscos não introduzidos por esta alteração

- Não há novo endpoint público.
- Não há bypass de autenticação.
- Não há alteração de variáveis de ambiente ou config server.
- Tarefas antigas com `id_responsible_user = null` permanecem assim até atualização manual via API.

---

## Referências

- [README.md](../README.md) — seções de segurança e token
- [tecnico-integracao-alteracoes-recentes.md](./tecnico-integracao-alteracoes-recentes.md)
