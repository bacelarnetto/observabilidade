# Observabilidade — Guia do Projeto

Projeto de estudo de **observabilidade para microsserviços**: logging estruturado em JSON gerado por um serviço **Spring Boot (Java 11)**, coletado pelo **Fluent Bit**, processado no **Logstash**, indexado no **OpenSearch** e visualizado no **OpenSearch Dashboards**. Inclui também os manifests **Kubernetes** (DaemonSet do Fluent Bit, Deployment do microsserviço) e **Helm charts** para rodar o mesmo pipeline em cluster.

---

## Como iniciar este guia

> Todos os comandos devem ser rodados a partir da **raiz do projeto** (`/work/observabilidade`).

### Opção recomendada — serve (sem instalação)

```bash
npx serve docs/guide
```

Acesse: **http://localhost:3000**

### Alternativa — porta customizada

```bash
npx serve docs/guide -l 4000
```

Acesse: **http://localhost:4000**

> Os diagramas Mermaid **não renderizam** ao abrir o `index.html` diretamente no navegador (`file://`). É necessário um servidor HTTP.

---

## Subindo o stack

```bash
docker-compose up -d --build
```

Sobe `fluent-bit`, `opensearch-node1`, `opensearch-dashboards`, `logstash` e o microsserviço `ms-obs` — tudo em um único host, ligado pela rede `opensearch-net`. Veja [Docker Compose](docker-compose) para os detalhes de portas e do arquivo `docker-compose-observer.yml` (stack de observabilidade isolada, sem app).

---

## O que está neste guia

| Página | Conteúdo |
|---|---|
| [Arquitetura](arquitetura) | Pipeline completo de logs, topologia local vs. híbrida (K8s + observer externo) |
| [Fluxo de Logs](fluxo-logs) | Do request HTTP no `ms-obs` até o documento indexado no OpenSearch |
| [Microsserviço ms-obs](ms-obs) | Interceptors de logging, MDC, logback JSON, endpoints REST |
| [Fluent Bit](fluent-bit) | Configuração Docker Compose vs. DaemonSet K8s (`infra` com PV vs. `infra-no-pv`) |
| [Logstash + OpenSearch](logstash-opensearch) | Pipeline de filtros, índice gerado, dashboards |
| [Kubernetes](kubernetes) | Namespaces, Deployment do app, Helm charts |
| [Docker Compose](docker-compose) | Portas, variáveis de ambiente, diferenças entre os dois compose files |

---

## Links rápidos

- Compose completo: [`docker-compose.yml`](../../docker-compose.yml)
- Compose apenas observabilidade: [`docker-compose-observer.yml`](../../docker-compose-observer.yml)
- Pipeline Logstash: [`logstash/pipeline/logstash.conf`](../../logstash/pipeline/logstash.conf)
- Config Fluent Bit (compose): [`fluent-bit/conf/fluent-bit.conf`](../../fluent-bit/conf/fluent-bit.conf)
- Manifests Kubernetes: [`kubernetes/`](../../kubernetes)
- Código do microsserviço: [`ms-obs/src/main/java/br/com/ms`](../../ms-obs/src/main/java/br/com/ms)
