# Notas de segurança — Timestamps de tarefas com fuso horário

Análise objetiva da alteração de 2026-05-29. Complementa [tecnico-timestamps-fuso-horario.md](./tecnico-timestamps-fuso-horario.md).

---

## Escopo analisado

Alterações em:

- `Task.java` — campos `createdAt`, `inProgressAt`, `completedAt`, `cancelledAt` (`Instant`, UTC)
- `UserTimezoneResolver`, `TaskTimestampHelper`, `InvalidTimezoneException`
- `TaskController`, `SubtaskController`, `DateTaskController` — header `X-User-Timezone`
- `ResponseTaskDTO`, `ResponseSubtaskDTO`, `TaskViewDTO` — serialização `OffsetDateTime`
- `RestExceptionHandler` — HTTP 400 para timezone inválido

---

## Secrets e dados sensíveis no Git

| Verificação | Resultado |
|-------------|-----------|
| Credenciais, tokens ou chaves no diff da feature | **Nenhum** identificado |
| Novas variáveis de ambiente obrigatórias | **Nenhuma** — apenas header HTTP |

---

## Header `X-User-Timezone`

| Aspecto | Situação |
|---------|----------|
| Validação | Apenas identificadores IANA válidos (`ZoneId.of`); valor inválido → **400** |
| Fallback | `America/Sao_Paulo` quando header ausente ou em branco |
| Impacto na gravação | **Nenhum** — timestamps persistidos em UTC independente do header |
| Spoofing de fuso | Cliente pode informar fuso incorreto; afeta **somente exibição** na resposta, não integridade dos dados no banco |

**Recomendação para BFF:** repassar o fuso real do usuário autenticado (browser), não um valor fixo genérico, para consistência de UX.

---

## Persistência e exposição de dados

| Aspecto | Situação |
|---------|----------|
| Armazenamento | UTC no MySQL — adequado para auditoria e analytics futuros |
| Resposta API | Convertida para o fuso informado pelo cliente |
| Timestamps | Não contêm PII adicional além do que já existe na entidade tarefa |
| Superfície ampliada | `TaskViewDTO` expõe `idResponsibleUser` e timestamps em **todas** as listagens GET |

Integradores devem tratar respostas no contexto do usuário autenticado no BFF (mesma responsabilidade de ownership já documentada).

---

## Modelo de autorização

| Aspecto | Situação |
|---------|----------|
| Token de máquina no path | **Inalterado** — validado por `TokenService` (Auth-For-MService) |
| Header de timezone | **Não substitui** autenticação nem autorização |
| Checagem dono×tarefa | Continua responsabilidade do **Gateway/BFF** (removida no repositório em maio/2026) |

---

## Limitações conhecidas

1. **Exceção genérica em `validateToken`:** se a mensagem não for exatamente `"Invalid token"`, o fluxo pode não retornar 401 nem chegar a validar timezone — comportamento **pré-existente**, não introduzido por esta feature.
2. **Fuso incorreto do cliente:** não altera o instante gravado; pode confundir exibição se o BFF enviar timezone errado.
3. **Registros legados:** timestamps `null` até nova transição — não expõe dados antigos inexistentes, mas pode gerar UI inconsistente se não tratado.

---

## Riscos não introduzidos por esta alteração

- Não há novo bypass de autenticação.
- Não há exposição de `Instant` UTC cru na API (substituído por `OffsetDateTime` no fuso do usuário).
- Não há alteração nos endpoints `/responsible` ou `postpone-day` quanto a auth.
- Fluxos `LATE` automáticos e adiamento diário **não** gravam timestamps de ciclo de vida (sem efeito colateral indesejado em auditoria).

---

## Referências

- [README.md](../README.md) — seções de segurança e token
- [tecnico-timestamps-fuso-horario.md](./tecnico-timestamps-fuso-horario.md)
- [tecnico-integracao-alteracoes-recentes.md](./tecnico-integracao-alteracoes-recentes.md)
