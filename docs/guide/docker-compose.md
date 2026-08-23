# Docker Compose

Existem dois arquivos de compose no repositório com propósitos diferentes.

## `docker-compose.yml` — stack completa

```bash
docker-compose up -d --build
```

| Serviço | Imagem | Portas | Rede |
|---|---|---|---|
| `fluent-bit` | `fluent/fluent-bit` | `24224` (forward, tcp/udp), `24225` (http, tcp/udp) | `opensearch-net` |
| `opensearch-node1` | `opensearchproject/opensearch:latest` | `9200` (REST), `9600` (Performance Analyzer) | `opensearch-net` |
| `opensearch-dashboards` | `opensearchproject/opensearch-dashboards:latest` | `network_mode: host` | host |
| `logstash` | `opensearchproject/logstash-oss-with-opensearch-output-plugin:8.4.0` | `5044` (beats, não usado), `8090` (http input) | `opensearch-net` |
| `ms-obs` | `bacelarnetto/ms-obs:latest` (build local) | `8080` | `opensearch-net` |

Pontos de atenção:

- `ms-obs` usa `logging.driver: fluentd` apontando para `localhost:24224` — depende do `links: [fluent-bit]` para resolver o hostname corretamente no Compose v3.
- `opensearch-dashboards` roda em `network_mode: host`, então não participa da rede `opensearch-net`; ele acessa o OpenSearch via `http://opensearch-node1:9200`, o que só funciona se o hostname for resolvido pelo host (ajustar `/etc/hosts` ou usar `localhost:9200` se `opensearch-node1` publicar a porta no host).
- `DISABLE_SECURITY_PLUGIN=true` e `DISABLE_SECURITY_DASHBOARDS_PLUGIN=true` — segurança do OpenSearch desabilitada, adequado apenas para ambiente local/estudo.
- Volume nomeado `opensearch-data1` persiste os dados do OpenSearch entre `docker-compose down`/`up` (mas não entre `docker-compose down -v`).

## `docker-compose-observer.yml` — apenas a stack de observabilidade

```bash
docker-compose -f docker-compose-observer.yml up -d
```

Sobe **somente** `opensearch-node1`, `opensearch-dashboards` e `logstash` — sem `ms-obs` e sem `fluent-bit`. Usado no cenário híbrido descrito em [Arquitetura](arquitetura): o app e o Fluent Bit rodam em um cluster Kubernetes separado, e esta stack fica de pé em outra máquina/host, recebendo logs via HTTP na porta `8090` que o Fluent Bit do cluster aponta via `FLB_HTTP_HOST`.

Diferença notável em relação ao compose completo: aqui os serviços têm `depends_on` explícito (`opensearch-dashboards` e `logstash` dependem de `opensearch-node1`), o que não existe no `docker-compose.yml` principal.

## Acessando os serviços

| URL | O quê |
|---|---|
| `http://localhost:8080` | API `ms-obs` (Swagger em `/swagger-ui.html`) |
| `http://localhost:9200` | OpenSearch REST API |
| `http://localhost:5601` (padrão do OpenSearch Dashboards) | Interface de visualização — buscar o índice `microservicesaws-*` |
| `http://localhost:8090` | Endpoint HTTP do Logstash (recebe do Fluent Bit) |
