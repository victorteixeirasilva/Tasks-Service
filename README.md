# TaskFlow Engine - Microservico de Alta Performance para Gestao de Tarefas

> **Transforme operacoes complexas em execucao previsivel, rastreavel e escalavel.**

## Slogan
**"Menos atrito operacional. Mais entregas concluidas no prazo."**
<img width="1536" height="1024" alt="Generatedimage_1774911403346" src="https://github.com/user-attachments/assets/11740170-698b-4829-8f35-d70e35d143a1" />

## Introducao e Visao Geral
Em ambientes digitais orientados a metas, tarefas mal estruturadas geram retrabalho, atrasos e queda de produtividade. O `Tasks-Service` nasce para resolver exatamente esse gargalo: centralizar, organizar e automatizar o ciclo de vida das tarefas com inteligencia de negocio e integrações entre microsservicos.

A solucao foi desenvolvida como um microservico robusto em Java com Spring Boot, focado em:
- governanca de tarefas por usuario e objetivo;
- automacao de tarefas recorrentes;
- acompanhamento por status e janelas de data;
- integracao com autenticacao e demais servicos do ecossistema.

### Beneficios diretos para o negocio
- **Aumento de previsibilidade operacional:** tarefas com status claros e historico rastreavel.
- **Melhoria de produtividade:** filtros inteligentes e operacoes em lote reduzem trabalho manual.
- **Escalabilidade arquitetural:** desenho de microservico pronto para evolucao em ambientes distribuidos.
- **Confianca para tomada de decisao:** dados organizados por periodo, objetivo e prioridade.

## Funcionalidades Principais
### 1) Cadastro e gestao completa de tarefas
- Cria tarefas com padrao consistente de dados.
- Atualiza conteudo, data e atributos criticos de execucao.
- Remove tarefas simples ou conjuntos de repeticao futura.
- **Valor agregado:** reduz ambiguidade operacional e acelera fluxo de trabalho.

### 2) Motor de recorrencia inteligente
- Replica tarefas com base em periodo e dias da semana.
- Permite atualizar series recorrentes sem perder controle do historico.
- Remove recorrencias futuras com seguranca de negocio.
- **Valor agregado:** automatiza rotinas e elimina operacoes repetitivas manuais.

### 3) Controle de status orientado a execucao
- Fluxo de status com estados como `TODO`, `IN_PROGRESS`, `DONE`, `LATE` e `CANCELED`.
- Endpoints especificos para transicao de estado.
- Consulta por status em data unica ou intervalo de datas.
- **Valor agregado:** oferece visao operacional clara para gestao de performance.

### 4) Consultas avancadas para analise operacional
- Busca tarefas por usuario, objetivo, data e janela temporal.
- Recupera tarefas atrasadas e segmenta por situacao.
- Retorna informacoes prontas para alimentar dashboards ou camadas de BI.
- **Valor agregado:** permite diagnostico rapido de gargalos e priorizacao de execucao.

### 5) Subtarefas vinculadas a tarefas pai
- CRUD dedicado em `/ms/tasks/subtask` (criar, listar por pai, promover, excluir).
- Heranca de objetivo da tarefa pai e flag `hasSubtasks` na pai.
- Listagens historicas de tarefas retornam apenas tarefas raiz; subtarefas sao consultadas pelo endpoint proprio.
- **Valor agregado:** decompoe entregas complexas sem poluir visoes de calendario e status.

### 6) Adiamento em lote por dia de referencia
- `POST /ms/tasks/date/postpone-day/{token}`: tarefas `TODO` (pai) viram `LATE` e avancam um dia; `IN_PROGRESS` apenas avancam a data.
- Subtarefas em `TODO` nao entram no fluxo automatico de atraso.
- **Valor agregado:** suporta rotinas diarias (ex.: virada de dia) com resposta numerica de quantas tarefas foram afetadas.

### 7) Seguranca e autorizacao entre microsservicos
- Validacao de token de maquina no path via Auth-For-MService em operacoes sensiveis.
- A checagem de que o `idUser` do path e dono da tarefa e responsabilidade do **Gateway/BFF** (removida no repositorio a partir de maio/2026).
- Integracao com servico de autenticacao dedicado.
- **Valor agregado:** flexibiliza chamadas entre microsservicos mantendo token obrigatorio na borda.

### 8) Usuario responsavel por tarefa
- API dedicada em `/ms/tasks/responsible`, independente do CRUD legado de tarefas.
- `PUT /{token}` atribui ou remove (`null`) o usuario responsavel; `GET /{idUser}/{idTask}/{token}` consulta o responsavel atual.
- Campo `idResponsibleUser` na entidade (distinto de `idUser`, que permanece o dono da tarefa).
- **Valor agregado:** permite delegacao sem alterar contratos existentes de criacao ou listagem.

### 9) Operacao assíncrona e observabilidade
- Endpoints com processamento assíncrono via `CompletableFuture`.
- Instrumentacao com `Spring Boot Actuator`.
- Swagger/OpenAPI para descoberta e teste de contrato.
- **Valor agregado:** melhora responsividade e facilita operacao em producao.

## Tecnologias Utilizadas
### Linguagens e plataforma
- **Java 21**
- **Maven Wrapper (`mvnw`)**

### Frameworks e bibliotecas principais
- **Spring Boot 3.4.5**
- **Spring Web**
- **Spring Data JPA**
- **Spring Cloud OpenFeign**
- **Spring Cloud Config**
- **Spring Boot Actuator**
- **Springdoc OpenAPI (Swagger UI)**
- **Lombok**
- **Auth0 Java JWT**

### Persistencia e infraestrutura
- **MySQL Connector/J**
- **Docker / Docker Compose**
- **GitHub Actions (CI/CD com build e deploy via SSH)**

### Testes e qualidade
- **Spring Boot Starter Test**
- **Rest Assured**
- **Java Faker**

### Por que essas escolhas tecnicas importam
- **Spring Boot + JPA:** aceleram desenvolvimento com padrao corporativo e baixo custo de manutencao.
- **OpenFeign + Config Server:** simplificam comunicacao entre microsservicos e centralizam configuracoes.
- **Docker + CI/CD:** garantem consistencia de ambiente e releases mais confiaveis.

## Demonstracao Visual
<!-- Inclua evidencias visuais para elevar impacto comercial do portifolio.

> Substitua os placeholders abaixo por imagens reais, GIFs ou links para video. -->

<!-- ### Sugestoes de midia -->
<!-- - `[Screenshot 1]` Visao geral da API no Swagger. -->
<!-- - `[Screenshot 2]` Fluxo de criacao e atualizacao de tarefas.
- `[GIF 1]` Execucao de recorrencia e atualizacao em lote.
- `[Video Demo]` Jornada completa: criar, filtrar, concluir e analisar tarefas. -->

![Swagger do Tasks-Service](./docs/images/swagger-overview.png)
![Swagger do Tasks-Service2](./docs/images/swagger-overview-2.png)

### 4) Endpoints e monitoramento

**Base:** `http://localhost:8085`

| Grupo | Prefixo | Exemplos |
|-------|---------|----------|
| Tarefas | `/ms/tasks` | CRUD, status, listagens por data/objetivo (somente tarefas pai) |
| Datas | `/ms/tasks/date` | `PUT /{token}`, `POST /postpone-day/{token}` |
| Subtarefas | `/ms/tasks/subtask` | `POST /{token}`, `GET /{idUser}/{idParentTask}/{token}` |
| Responsavel | `/ms/tasks/responsible` | `PUT /{token}`, `GET /{idUser}/{idTask}/{token}` |

- Swagger UI: `http://localhost:8085/swagger-ui/index.html`
- Actuator: `http://localhost:8085/actuator`
- Documentacao tecnica para integradores: [docs/tecnico-integracao-alteracoes-recentes.md](./docs/tecnico-integracao-alteracoes-recentes.md)
- Historico de mudancas: [CHANGELOG.md](./CHANGELOG.md)

### 5) Parar ambiente
```bash
docker-compose down --volumes --remove-orphans
```

## Contribuicao
Contribuicoes sao bem-vindas para evoluir qualidade tecnica e valor de negocio.

1. Faça um fork do projeto.
2. Crie uma branch de feature: `git checkout -b feature/minha-melhoria`
3. Commit suas alteracoes: `git commit -m "feat: minha melhoria"`
4. Envie para o seu fork: `git push origin feature/minha-melhoria`
5. Abra um Pull Request com contexto tecnico e impacto esperado.

## Licenca
Este projeto esta distribuido sob a licenca **MIT**.  
Sinta-se livre para usar, adaptar e evoluir com os devidos creditos.

## Agradecimentos
- Ao ecossistema Spring, pela maturidade e produtividade na construcao de APIs.
- A comunidade open source, pelas bibliotecas e praticas que aceleram entregas de alto nivel.
- Ao time InEvolving, pela visao de produto e foco em excelencia operacional.

## Contato
Quer conversar sobre arquitetura, produto ou oportunidades?

- **LinkedIn:** [https://www.linkedin.com/in/victor-teixeira-354a131a3/](https://www.linkedin.com/in/victor-teixeira-354a131a3/)
- **GitHub:** [https://github.com/victorteixeirasilva](https://github.com/victorteixeirasilva)
- **E-mail:** victor.teixeira@inovasoft.tech

---
**Projeto ideal para portifolio tecnico-comercial:** demonstra dominio em backend corporativo, arquitetura de microsservicos, integracoes seguras e entrega orientada a resultado de negocio.
