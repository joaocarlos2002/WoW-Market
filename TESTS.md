# WowMarket Backend API

Backend service para uma plataforma que fornece análise da casa de leilões, inteligência de preços e insights de mercado para jogadores de World of Warcraft.

## Requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 14+ (para produção)
- H2 Database (para testes locais)

## Configuração Local

### 1. Clonar o repositório
```bash
git clone <repository-url>
cd wowmarket
```

### 2. Variáveis de Ambiente
Para desenvolvimento local, as credenciais de teste estão configuradas em `src/main/resources/application-local.properties`:

```properties
blizzard.client-id=test-client-id
blizzard.client-secret=test-client-secret
```

Para produção, configure as variáveis de ambiente:
```bash
export BLIZZARD_CLIENT_ID=seu_client_id
export BLIZZARD_CLIENT_SECRET=seu_client_secret
export DB_PASSWORD=sua_senha_db
```

### 3. Executar a Aplicação

**Modo desenvolvimento (com H2):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

**Modo produção:**
```bash
./mvnw spring-boot:run
```

## Testes

### Executar todos os testes
```bash
./mvnw test
```

### Executar teste específico
```bash
./mvnw test -Dtest=HealthCheckerServiceTest
```

### Limpar e executar testes
```bash
./mvnw clean test
```

### Cobertura de testes
```bash
./mvnw clean test jacoco:report
```

## Estrutura de Testes

### Testes Unitários
- **HealthCheckerServiceTest** - Testa os checks de saúde (disco, memória, threads, CPU, banco de dados)
- **BlizzardTokenDtoTest** - Testa o DTO de token da API da Blizzard

### Testes de Integração
- **WowMarketApplicationTests** - Testa o carregamento da aplicação com Spring Boot

## API Endpoints

### Health Checker
- `GET /api/HealthChecker/` - Status geral da saúde da aplicação
- `GET /api/HealthChecker/disk` - Status do disco
- `GET /api/HealthChecker/database` - Status do banco de dados
- `GET /api/HealthChecker/memory` - Status da memória
- `GET /api/HealthChecker/threads` - Status das threads
- `GET /api/HealthChecker/cpu` - Status da CPU

### Blizzard OAuth
- `GET /api/oauth/blizzard/callback` - Callback da autenticação OAuth da Blizzard

## Build com Docker

### Construir imagem
```bash
docker build -t wowmarket:latest .
```

### Executar container
```bash
docker-compose up
```

## CI/CD

Este projeto usa GitHub Actions para CI/CD. O workflow automaticamente:
1. Compila o código
2. Executa todos os testes
3. Gera relatórios de teste
4. Faz upload dos artefatos

Veja `.github/workflows/build.yml` para mais detalhes.

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/wowmarket/wowmarket/
│   │   ├── config/           # Configurações (Security, WebClient)
│   │   ├── controller/       # Endpoints REST
│   │   ├── domain/          # Enumerações e tipos de domínio
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exceções customizadas
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Data Access Layer
│   │   ├── service/         # Lógica de negócio
│   │   └── util/            # Utilitários
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties
│       └── logback-spring.xml
└── test/
    └── java/com/wowmarket/wowmarket/
        ├── dto/
        ├── service/
        └── controller/
```

## Logging

O logging está configurado em `src/main/resources/logback-spring.xml`:

- **Nível ROOT**: INFO
- **Nível COM.WOWMARKET**: DEBUG (desenvolvimento)
- **Arquivo**: `logs/wowmarket.log`

## Troubleshooting

### Problema: "WebClient não configurado"
**Solução**: Configure as variáveis de ambiente `BLIZZARD_CLIENT_ID` e `BLIZZARD_CLIENT_SECRET`

### Problema: Testes falhando por falta de conexão
**Solução**: Os testes usam H2 em memória. Verifique se nenhuma porta está bloqueada.

### Problema: Build falha com erro de compilação
**Solução**: Limpe o cache do Maven:
```bash
./mvnw clean
```

## Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Faça commit das suas mudanças (`git commit -m 'Add some AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

Todos os PRs devem ter testes e passar no CI/CD.

## Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

## Contato

Para dúvidas ou sugestões, abra uma issue no repositório.

