# 🛒 Plataforma de E-commerce (Full-Stack)

Este projeto é uma plataforma de E-commerce desenvolvida utilizando **Java com Spring Boot** no Back-end e **React** no Front-end.

O sistema implementa o gerenciamento de catálogo de produtos, carrinho de compras e controle de pedidos (checkout), aplicando boas práticas de engenharia de software.

## 🚀 Tecnologias Utilizadas

**Back-end:**
* Java 21+
* Spring Boot (Web, Data JPA, Validation)
* Banco de Dados em Memória (H2)
* Hibernate Envers (Auditoria/Histórico)
* Lombok (Redução de boilerplate)
* Maven

**Front-end:**
* React.js
* Fetch API para consumo de APIs REST

## 🏛️ Arquitetura e Padrões de Projeto

A aplicação foi desenhada aplicando conceitos do **Domain-Driven Design (DDD)** para separação de Bounded Contexts, e **Clean Code/SOLID** através do design em 3 camadas:

1. **Camada de Controle (Controllers):** Expõe as APIs REST e lida com requisições HTTP.
2. **Camada de Serviço (Services):** Contém as regras de negócio e orquestra a comunicação entre os domínios.
3. **Camada de Repositório (Repositories):** Interface de acesso aos dados persistidos.

### Bounded Contexts (Domínios)
* **Catálogo:** Gerencia os produtos e o estoque.
* **Carrinho:** Gerencia a sessão de compras do usuário antes da efetivação.
* **Pedido:** Responsável pelo processo de checkout e status da compra.

---

## 🗄️ Persistência, Auditoria e Testes

* **Persistência e Histórico:** Utiliza **Spring Data JPA** com banco **H2**. Implementa **Hibernate Envers** (`@Audited`) para rastrear o histórico de alterações no banco (como variações de preço e mudança de status do pedido) sem sujar a regra de negócio. Também utiliza *Query Methods* customizados.
* **Testes Automatizados:** A estabilidade da persistência é garantida com testes de integração utilizando **JUnit 5**, **AssertJ** e `@DataJpaTest`, validando repositórios e queries em um banco em memória isolado.

---

## 📊 Diagramas de Arquitetura

Os diagramas abaixo ilustram o design e o fluxo de dados da aplicação.

### Diagrama de Componentes

```mermaid
graph TD
    Client[Application] -->|Chamadas HTTP/REST| API[Back-end: Spring Boot API]
    
    subgraph Back-end Spring Boot
        direction TB
        API --> CatController[Catálogo Controller]
        API --> CarController[Carrinho Controller]
        API --> PedController[Pedido Controller]
        
        CatController --> CatService[Catálogo Service]
        CarController --> CarService[Carrinho Service]
        PedController --> PedService[Pedido Service]
        
        CarService -.->|Consulta Produto| CatService
        PedService -.->|Consulta Carrinho| CarService
        
        CatService --> Repositories[(Spring Data JPA)]
        CarService --> Repositories
        PedService --> Repositories
    end
    
    Repositories --> DB[(H2 Database)]
  ```

```mermaid
sequenceDiagram
    participant UI as Interface React
    participant PedidoCtrl as PedidoController
    participant PedidoSrv as PedidoService
    participant CarrinhoSrv as CarrinhoService
    participant DB as Banco de Dados
    
    UI->>PedidoCtrl: POST /api/pedidos/checkout/{carrinhoId}
    PedidoCtrl->>PedidoSrv: realizarCheckout(carrinhoId)
    
    PedidoSrv->>CarrinhoSrv: buscarPorId(carrinhoId)
    CarrinhoSrv-->>PedidoSrv: Retorna Carrinho com Itens
    
    PedidoSrv->>PedidoSrv: Calcula o Total e Cria o Pedido
    
    PedidoSrv->>DB: save(Pedido)
    DB-->>PedidoSrv: Pedido Salvo (Status: PENDENTE)
    
    PedidoSrv-->>PedidoCtrl: Retorna Dados do Pedido
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