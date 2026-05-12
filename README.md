# 🛒 Plataforma de E-commerce (Full-Stack)

Este projeto é uma plataforma de E-commerce desenvolvida como demonstração de competências em desenvolvimento Full-Stack, utilizando **Java com Spring Boot** no Back-end e **React** no Front-end.

O sistema implementa o gerenciamento de catálogo de produtos, carrinho de compras e controle de pedidos (checkout), aplicando boas práticas de engenharia de software.

## 🚀 Tecnologias Utilizadas

**Back-end:**
* Java 17+
* Spring Boot (Web, Data JPA, Validation)
* Banco de Dados em Memória (H2)
* Lombok (Redução de boilerplate)
* Maven

**Front-end:**
* React.js
* Fetch API para consumo de APIs REST

## 🏛️ Arquitetura e Padrões de Projeto

A aplicação foi desenhada seguindo o princípio de **Monolito Modular**, aplicando conceitos do **Domain-Driven Design (DDD)** para separação de Bounded Contexts, e **Clean Code/SOLID** através do design em 3 camadas:

1. **Camada de Controle (Controllers):** Expõe as APIs REST e lida com requisições HTTP.
2. **Camada de Serviço (Services):** Contém as regras de negócio e orquestra a comunicação entre os domínios.
3. **Camada de Repositório (Repositories):** Interface de acesso aos dados persistidos.

### Bounded Contexts (Domínios)
* **Catálogo:** Gerencia os produtos e o estoque.
* **Carrinho:** Gerencia a sessão de compras do usuário antes da efetivação.
* **Pedido:** Responsável pelo processo de checkout e status da compra.

---

## 📊 Diagramas de Arquitetura

Os diagramas abaixo ilustram o design da aplicação. *(Visualizável no GitHub ou editores com suporte a Mermaid)*.

### Diagrama de Componentes

```mermaid
graph TD
    Client[Front-end: React Application] -->|Chamadas HTTP/REST| API[Back-end: Spring Boot API]
    
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