# Arquitetura

## Pipeline de Observabilidade

O `ms-obs` nunca fala diretamente com o Logstash ou o OpenSearch. Ele escreve **logs JSON estruturados no stdout**; a coleta e o encaminhamento são responsabilidade da infraestrutura (driver de log do Docker ou DaemonSet do Fluent Bit no Kubernetes).

```mermaid
graph LR
    APP["ms-obs\nSpring Boot\nlogback JSON no stdout"]
    FB["Fluent Bit\nforward :24224 · http :24225"]
    LS["Logstash\nhttp input :8090"]
    OS[("OpenSearch\n:9200")]
    OSD["OpenSearch Dashboards"]

    APP -->|"driver fluentd\n(docker logging)"| FB
    FB -->|"OUTPUT http\nformat json"| LS
    LS -->|"OUTPUT opensearch\nindex microservicesaws-YYYY.MM.dd"| OS
    OS --> OSD

    style APP fill:#eff6ff,stroke:#3b82f6
    style FB fill:#fef9c3,stroke:#f59e0b
    style LS fill:#fdf2f8,stroke:#ec4899
    style OS fill:#ecfdf5,stroke:#10b981
    style OSD fill:#f0fdf4,stroke:#22c55e
```

> O `ms-obs` **não conhece** Fluent Bit, Logstash ou OpenSearch — a única integração é o `logging.driver: fluentd` configurado no `docker-compose.yml`, que redireciona o stdout do container para o Fluent Bit na porta `24224`.

---

## Duas topologias de execução

### 1. Local — tudo em um único `docker-compose.yml`

Usada para desenvolvimento: todos os componentes sobem juntos na rede `opensearch-net`.

```mermaid
graph TB
    subgraph "docker-compose.yml — um único host"
        APP["ms-obs :8080"]
        FB["fluent-bit :24224/:24225"]
        LS["logstash :8090"]
        OS["opensearch-node1 :9200"]
        OSD["opensearch-dashboards"]
    end
    APP -->|fluentd driver| FB --> LS --> OS --> OSD
```

### 2. Híbrida — app + coleta no Kubernetes, observabilidade externa

Os manifests em `kubernetes/` sobem **apenas** o microsserviço (Deployment `ms-obs`) e o Fluent Bit (DaemonSet, um pod por nó). O Fluent Bit é configurado com `FLB_HTTP_HOST` apontando para o **IP do host** onde roda separadamente o `docker-compose-observer.yml` (OpenSearch + Dashboards + Logstash, sem app e sem Fluent Bit).

```mermaid
graph TB
    subgraph "Cluster Kubernetes"
        subgraph "namespace corp-app-obs"
            APPK["Deployment ms-obs\n(sboot-app.yaml)"]
        end
        subgraph "namespace kube-logging"
            FBD["DaemonSet fluent-bit\n(1 pod por nó)\ntail /var/log/containers/*.log"]
        end
    end

    subgraph "Host externo — docker-compose-observer.yml"
        LSX["logstash :8090"]
        OSX["opensearch-node1 :9200"]
        OSDX["opensearch-dashboards"]
    end

    APPK -.->|"stdout do container\n(coletado via hostPath /var/log)"| FBD
    FBD -->|"OUTPUT http\nFLB_HTTP_HOST=<ip-do-host>"| LSX --> OSX --> OSDX

    style APPK fill:#eff6ff,stroke:#3b82f6
    style FBD fill:#fef9c3,stroke:#f59e0b
    style LSX fill:#fdf2f8,stroke:#ec4899
    style OSX fill:#ecfdf5,stroke:#10b981
```

> O `FLB_HTTP_HOST` é hardcoded como IP (`172.23.53.166` em `infra/`, `172.24.132.180` em `infra-no-pv/`) — não é resolvido por DNS de serviço, então precisa ser atualizado manualmente se o host observer mudar de endereço. Ver [Fluent Bit](fluent-bit).

---

## Por que duas variantes de Fluent Bit no Kubernetes (`infra/` vs. `infra-no-pv/`)

| | `kubernetes/infra/` | `kubernetes/infra-no-pv/` |
|---|---|---|
| Coleta de logs do host | Sim — `INPUT tail` em `/var/log/containers/*.log` + filtro `kubernetes` (enriquece com metadados do pod) | Não — sem `hostPath` de `/var/log` nem `/var/lib/docker/containers` |
| Persistência de posição de leitura | `storage.type filesystem` + `DB /var/log/flb_kube.db` (sobrevive a restart do pod) | Sem armazenamento em disco |
| Uso pretendido | Cluster com volumes persistentes disponíveis (produção/homologação) | Cluster restrito, sem PV disponível (ex.: ambiente de estudo/sandbox) |

Ambas mantêm o mesmo `OUTPUT http` para o Logstash externo. Ver detalhamento completo em [Fluent Bit](fluent-bit).

---

## Estrutura de Pastas

```
observabilidade/
├── ms-obs/                    ← microsserviço Spring Boot (fonte dos logs)
├── fluent-bit/conf/           ← config do Fluent Bit para o docker-compose local
├── logstash/                  ← pipeline + config do Logstash
├── docker-compose.yml         ← stack completa (app + coleta + observabilidade)
├── docker-compose-observer.yml← apenas observabilidade (sem app, sem Fluent Bit)
├── kubernetes/
│   ├── app/                   ← Deployment/Service/Namespace do ms-obs
│   ├── infra/                 ← DaemonSet Fluent Bit com PV/hostPath
│   ├── infra-no-pv/           ← DaemonSet Fluent Bit sem PV
│   └── helm/                  ← charts Helm (fluent-bit-helm, nginxhelm)
└── docs/guide/                ← este guia
```
