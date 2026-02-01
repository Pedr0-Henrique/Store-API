# Store (API + Frontend)

Projeto full-stack para gerenciamento de uma loja, com **API REST** e **Frontend web**.

O sistema permite:

- **Cadastrar e gerenciar categorias, produtos e clientes**
- **Criar pedidos com múltiplos itens** (produto + quantidade)
- **Atualizar status do pedido** (fluxo dedicado via `PATCH`)
- **Aplicar regras de negócio/validações** (ex.: não excluir categoria com produtos)

## Tecnologias

### Frontend

- **Linguagens**: TypeScript
- **Framework**: React 18
- **Build/Dev Server**: Vite
- **HTTP Client**: Axios
- **UI/Estilos**: Tailwind CSS
- **Icons**: lucide-react
- **Toasts**: sonner

### Backend

- **Linguagem**: Java 17
- **Framework**: Spring Boot 3
- **Persistência**: Spring Data JPA (Hibernate)
- **Migrações**: Flyway
- **Documentação da API**: Springdoc OpenAPI (Swagger UI)

### Banco de dados

- **PostgreSQL** (padrão: `storedb` em `localhost:5432`)

## Estrutura do repositório

- `store-api/` — backend Spring Boot
- `store-frontend/` — frontend React

## Como rodar localmente

### 1) Banco de dados (PostgreSQL)

Crie um banco chamado `storedb` (ou ajuste as variáveis abaixo).

Variáveis suportadas pelo backend (`store-api/src/main/resources/application.yml`):

- `DB_URL` (padrão: `jdbc:postgresql://localhost:5432/storedb`)
- `DB_USERNAME` (padrão: `postgres`)
- `DB_PASSWORD` (padrão: `root`)

> O Flyway executa as migrações automaticamente ao iniciar a API.

### 2) Backend (store-api)

Pré-requisitos:

- Java 17
- Maven

Rodar:

- `mvn spring-boot:run`

A API sobe por padrão em:

- `http://localhost:8080`

Swagger:

- `http://localhost:8080/swagger-ui.html`

### 3) Frontend (store-frontend)

Pré-requisitos:

- Node.js (recomendado: 18+)

Rodar:

- `npm install`
- `npm run dev`

O Vite roda por padrão em:

- `http://localhost:5173`

#### Proxy/URL da API

Por padrão o frontend usa `baseURL = /api/v1` e o Vite faz proxy de `/api` para `http://localhost:8080` (configurado em `store-frontend/vite.config.ts`).

Opcionalmente você pode apontar diretamente para a API via variável:

- `VITE_API_URL` (ex.: `http://localhost:8080/api/v1`)

## Funcionalidades

### Categorias

- **Cadastrar categoria**
- **Listar categorias com paginação**
- **Editar categoria**
  - Atualização parcial via `PATCH`
- **Excluir categoria**
  - Bloqueia exclusão se existir **produto vinculado** (retorna **409 Conflict** com mensagem clara)

### Produtos

- **Cadastrar produto** (associando uma categoria)
- **Listar produtos com paginação**
- **Editar produto**
  - Atualização parcial via `PATCH` (ex.: alterar só preço ou só categoria)
- **Excluir produto**
  - Bloqueia exclusão se o produto estiver referenciado em **itens de pedido** (retorna **409 Conflict**)

### Clientes

- **Cadastrar cliente**
  - Validação de **email único**
- **Listar clientes com paginação**
- **Editar cliente**
  - Atualização parcial via `PATCH`
- **Excluir cliente**
  - Bloqueia exclusão se o cliente possuir **pedidos** (retorna **409 Conflict**)
- **Contagem de pedidos por cliente**
  - `GET /api/v1/customers/{id}/orders/count`
  - Retorna `total` e `open` (em aberto = não `DELIVERED` e não `CANCELED`)

### Pedidos

- **Criar pedido**
  - Com **um ou mais itens** (produto + quantidade)
  - Cálculo automático do **total** (somatório de subtotais)
- **Listar pedidos com paginação**
- **Editar pedido**
  - Alterações estruturais (cliente/itens) via `PUT /orders/{id}`
- **Editar status do pedido**
  - Endpoint dedicado: `PATCH /orders/{id}/status`
  - Mantém validações de transição e evita efeitos colaterais ao editar itens
- **Excluir pedido**

### Frontend (Web)

- Páginas:
  - **Categorias**
  - **Produtos**
  - **Clientes**
  - **Pedidos**
- **CRUD completo via modais** (criar/editar)
- **Paginação** nas listagens
- **Tratamento de erros** exibindo mensagens retornadas pela API (toasts)
- No modal de pedido, exibe **contagem de pedidos do cliente** (total e em aberto)

## Regras de negócio e validações

- Atualizações parciais: endpoints `PATCH` aceitam apenas os campos que o usuário deseja alterar.
- Restrições de exclusão:
  - Categoria não pode ser excluída se tiver produtos.
  - Cliente não pode ser excluído se tiver pedidos.
  - Produto não pode ser excluído se estiver em itens de pedidos.
- Erros de regra de negócio retornam **409 Conflict** com mensagem.

## Endpoints (resumo)

- **Categorias**: `/api/v1/categories`
- **Produtos**: `/api/v1/products`
- **Clientes**: `/api/v1/customers`
- **Pedidos**: `/api/v1/orders`
- **Status do pedido**: `PATCH /api/v1/orders/{id}/status`
- **Pedidos por cliente (contagem)**: `GET /api/v1/customers/{id}/orders/count`

## Como testar rapidamente (exemplos)

Atualizar status do pedido:

```bash
curl -X PATCH http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"DELIVERED"}'
```

Ver contagem de pedidos do cliente:

```bash
curl http://localhost:8080/api/v1/customers/1/orders/count
```

## Observações técnicas

- **CORS** configurado para permitir o frontend em `http://localhost:5173`.
- **Swagger UI** disponível em `http://localhost:8080/swagger-ui.html`.
- Banco com **Flyway** e `ddl-auto: validate` (o schema é controlado por migrações).
