# Observabilidade — Pipeline de Logs para Microsserviços

## Descrição

Projeto de estudo de **observabilidade para microsserviços**: um serviço Spring Boot gera logs estruturados em JSON, que são coletados, processados e indexados por um pipeline completo de observabilidade — do container até o dashboard de busca. O objetivo é demonstrar, de ponta a ponta, como plugar logging estruturado em uma aplicação e rotear esses logs através de **Fluent Bit → Logstash → OpenSearch**, tanto em Docker Compose (ambiente local) quanto em Kubernetes (DaemonSet de coleta + Deployment do app).

## Recursos Principais

- **Logging estruturado (JSON):** o microsserviço `ms-obs` (Spring Boot + Java 11) usa interceptors customizados (`LoggingInterceptor`, `RequestBodyInterceptor`, `ResponseBodyInterceptor`) para logar toda requisição/resposta HTTP com contexto via MDC (`nameApp`, `versaoApp`, `groupIdApp`, `ip`, `verb`, `path`), formatado em JSON pelo `logback-json-classic`.
- **Coleta com Fluent Bit:** recebe logs via driver `fluentd` do Docker (ambiente local) ou faz `tail` direto nos logs dos containers de um nó Kubernetes (DaemonSet), com filtros de enriquecimento de metadados do pod.
- **Processamento com Logstash:** pipeline de filtros que parseia o payload JSON, separa o contexto da aplicação (MDC) do conteúdo do log, e indexa no OpenSearch.
- **Armazenamento e busca com OpenSearch:** índice diário (`microservicesaws-YYYY.MM.dd`), explorável via OpenSearch Dashboards.
- **Duas topologias de execução:** stack completa em um único `docker-compose.yml` para desenvolvimento, ou app + coleta no Kubernetes apontando para uma stack de observabilidade externa (`docker-compose-observer.yml`).
- **Manifests Kubernetes e Helm:** Deployment/Service do microsserviço, DaemonSet do Fluent Bit (com e sem volume persistente) e charts Helm.

## Arquitetura

```
ms-obs (logback JSON) → Fluent Bit → Logstash → OpenSearch → OpenSearch Dashboards
```

Descrição completa da arquitetura, dos dois modos de execução e de cada componente do pipeline está no [guia do projeto](#documentação).

## Como Usar

### Pré-requisitos

- Docker e Docker Compose
- No Windows, rodar via WSL2 com Docker Desktop:
  ```bash
  wsl -d docker-desktop
  sysctl -w vm.max_map_count=262144
  ```
  (`vm.max_map_count` precisa ser aumentado para o OpenSearch subir corretamente)

### Subindo a stack completa

```bash
docker-compose up -d --build
```

### Acessando os serviços

| Serviço | URL |
|---|---|
| API `ms-obs` (Swagger em `/swagger-ui.html`) | http://localhost:8080 |
| OpenSearch REST API | http://localhost:9200 |
| OpenSearch Dashboards | http://localhost:5601 |
| Logstash (endpoint HTTP de entrada) | http://localhost:8090 |

### Subindo só a stack de observabilidade (sem o app)

```bash
docker-compose -f docker-compose-observer.yml up -d
```

Útil no cenário híbrido em que o app e o Fluent Bit rodam separadamente em um cluster Kubernetes (ver `kubernetes/`) apontando para esta stack externa.

## Próximos Passos

O projeto cobre hoje apenas o pilar de **logs**. Tracing distribuído (OpenTelemetry) e métricas (Micrometer/Prometheus) ainda não estão implementados — detalhes de como se encaixariam no [guia do projeto](docs/guide/proximos-passos.md).

## Documentação

Guia completo do projeto (arquitetura do pipeline, microsserviço `ms-obs`, Fluent Bit, Logstash/OpenSearch, Docker Compose e Kubernetes) em [`docs/guide/`](docs/guide/README.md):

```bash
npx serve docs/guide
```

Acesse **http://localhost:3000**.

## Estrutura do Projeto

```
observabilidade/
├── ms-obs/                     ← microsserviço Spring Boot (fonte dos logs)
├── fluent-bit/conf/            ← config do Fluent Bit para o docker-compose local
├── logstash/                   ← pipeline + config do Logstash
├── kubernetes/                 ← manifests K8s (app, DaemonSet Fluent Bit, Helm charts)
├── rest-client/                ← requisições .http de exemplo
├── docker-compose.yml          ← stack completa (app + coleta + observabilidade)
├── docker-compose-observer.yml ← apenas observabilidade (sem app, sem Fluent Bit)
└── docs/guide/                 ← guia do projeto (docsify)
```
