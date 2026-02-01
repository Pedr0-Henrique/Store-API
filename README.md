# Store (API + Frontend)

Projeto full-stack de exemplo para gerenciamento de uma loja (categorias, produtos, clientes e pedidos).

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

## Funcionalidades principais

- **Categorias**
  - CRUD via `/api/v1/categories`
- **Produtos**
  - CRUD via `/api/v1/products`
- **Clientes**
  - CRUD via `/api/v1/customers`
  - Contagem de pedidos por cliente via `/api/v1/customers/{id}/orders/count` (total e em aberto)
- **Pedidos**
  - CRUD via `/api/v1/orders`
  - Atualização de status via `PATCH /api/v1/orders/{id}/status`

## Observações

- Atualizações parciais (UX): `PATCH` foi adotado para editar recursos sem precisar enviar todos os campos.
- Algumas exclusões podem ser bloqueadas por relacionamentos (ex.: categoria com produtos, cliente com pedidos). Nesses casos, a API responde com **409 Conflict** e uma mensagem explicando o motivo.
