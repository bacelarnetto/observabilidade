Linux com doker
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144

## Documentação

Guia completo do projeto (arquitetura do pipeline de observabilidade, microsserviço `ms-obs`, Fluent Bit, Logstash/OpenSearch, Docker Compose e Kubernetes) em [`docs/guide/`](docs/guide/README.md):

```bash
npx serve docs/guide
```

Acesse **http://localhost:3000**.