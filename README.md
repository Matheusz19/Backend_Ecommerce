# 🛒 Plataforma de E-commerce (Arquitetura de Microsserviços)
Este projeto é uma plataforma de E-commerce Full-Stack desenvolvida utilizando **Java com Spring Boot** no Back-end e **React** no Front-end.

O sistema passou por uma evolução arquitetural, saindo de um monolito modular (DDD) para uma **Arquitetura Distribuída**, implementando o gerenciamento de catálogo, carrinho e controle de pedidos, com um microsserviço dedicado para transações financeiras.
## 🚀 Tecnologias Utilizadas

**Back-end:**
* Java 21+
* Spring Boot (Web, Data JPA, Validation)
* **Spring Cloud OpenFeign** (Comunicação Síncrona entre Microsserviços)
* Banco de Dados em Memória (H2)
* Hibernate Envers (Auditoria/Histórico)
* Lombok (Redução de boilerplate)
* Maven
* JUnit 5 & Mockito (Testes Unitários e de Integração)

**Front-end:**
* React.js
* Fetch API para consumo de APIs REST

## 🏛️ Arquitetura e Padrões de Projeto

A aplicação foi desenhada aplicando conceitos do **Domain-Driven Design (DDD)** para separação de Bounded Contexts, e **Clean Code/SOLID** através do design em 3 camadas:
### Serviços e Bounded Contexts
1. **Plataforma Core (Porta 8080):**
    * **Catálogo:** Gerencia os produtos e o estoque.
    * **Carrinho:** Gerencia a sessão de compras do usuário.
    * **Pedido:** Responsável pelo processo de checkout e orquestração do pagamento.
2. **Microsserviço de Pagamentos (Porta 8081):**
    * Serviço isolado responsável pela aprovação da transação financeira.

A comunicação entre a Plataforma Core e o Microsserviço de Pagamento ocorre via **OpenFeign**, garantindo o isolamento de responsabilidades.

1. **Camada de Controle (Controllers):** Expõe as APIs REST e lida com requisições HTTP.
2. **Camada de Serviço (Services):** Contém as regras de negócio e orquestra a comunicação entre os domínios.
3. **Camada de Repositório (Repositories):** Interface de acesso aos dados persistidos.

---

## 🗄️ Persistência, Auditoria e Testes

* **Persistência e Histórico:** Utiliza **Spring Data JPA** com banco **H2**. Implementa **Hibernate Envers** (`@Audited`) para rastrear o histórico de alterações no banco (como variações de preço e mudança de status do pedido) sem sujar a regra de negócio. Também utiliza *Query Methods* customizados.
* **Testes Automatizados:** A estabilidade da persistência é garantida com testes de integração utilizando **JUnit 5**, **AssertJ** e `@DataJpaTest`, validando repositórios e queries em um banco em memória isolado. A lógica de comunicação externa e orquestração de serviços é validada utilizando **Mockito** e `@WebMvcTest`.

---

## 📊 Diagramas de Arquitetura

Os diagramas abaixo ilustram o design e o fluxo de dados da aplicação.

### Diagrama de Componentes

```mermaid
graph TD
UI[Front-end React] -->|Chamadas HTTP/REST| Core[Back-end: Plataforma Core :8080]
UI -.->|Consulta Status Pagamento| Pag[Back-end: Microsserviço Pagamento :8081]

subgraph Core Spring Boot
direction TB
Core --> CatController[Catálogo Controller]
Core --> CarController[Carrinho Controller]
Core --> PedController[Pedido Controller]

CatController --> CatService[Catálogo Service]
CarController --> CarService[Carrinho Service]
PedController --> PedService[Pedido Service]

CarService -.->|Consulta Produto| CatService
PedService -.->|Consulta Carrinho| CarService

CatService --> Repositories[(Spring Data JPA)]
CarService --> Repositories
PedService --> Repositories

PedService --> Feign[PagamentoClient - OpenFeign]
end

Repositories --> DB1[(H2 DB - Core)]

Feign == Chamada Síncrona ==> Pag
Pag --> DB2[(H2 DB - Pagamentos)]
  ```

```mermaid
sequenceDiagram
    participant UI as Interface React
    participant PedidoCtrl as PedidoController
    participant PedidoSrv as PedidoService
    participant PagClient as PagamentoClient (Feign)
    participant PagMS as Microsserviço Pagamento
    participant DB as Banco de Dados Core

    UI->>PedidoCtrl: POST /api/pedidos/checkout/{carrinhoId}
    PedidoCtrl->>PedidoSrv: realizarCheckout(carrinhoId)

    PedidoSrv->>PedidoSrv: Busca Carrinho, Calcula o Total

    PedidoSrv->>DB: save(Pedido)
    DB-->>PedidoSrv: Pedido Salvo (Status: PENDENTE)

    PedidoSrv->>PagClient: processarPagamento(pedidoId, total)
    PagClient->>PagMS: POST /api/pagamentos/processar
    PagMS-->>PagClient: 200 OK (Status: APROVADO)

    PedidoSrv->>DB: Atualiza Status para PAGO

    PedidoSrv-->>PedidoCtrl: Retorna Dados do Pedido Atualizado
    PedidoCtrl-->>UI: 201 Created (JSON)
```

```mermaid
    erDiagram
    PRODUTO {
        Long id PK
        String nome
        String descricao
        BigDecimal preco
        Integer quantidadeEstoque
    }
    CARRINHO {
        Long id PK
    }
    ITEM_CARRINHO {
        Long id PK
        Long carrinho_id FK
        Long produtoId "Referência ao Catálogo"
        Integer quantidade
        BigDecimal precoUnitario
    }
    PEDIDO {
        Long id PK
        String status
        BigDecimal total
        LocalDateTime dataCriacao
    }
    ITEM_PEDIDO {
        Long id PK
        Long pedido_id FK
        Long produtoId "Referência ao Catálogo"
        Integer quantidade
        BigDecimal precoUnitario
    }

    CARRINHO ||--o{ ITEM_CARRINHO : "contém"
    PEDIDO ||--o{ ITEM_PEDIDO : "contém"
  ```