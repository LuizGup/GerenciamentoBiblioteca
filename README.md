<h1 >📚 Sistema de Gerenciamento de Biblioteca - API</h1>

<p>
API RESTful completa para gerenciar livros, usuários e empréstimos.<br>
Desenvolvida com <b>Java 21</b> e <b>Spring Boot 3</b> como parte de um desafio técnico.
</p>

<p>
  <img src="https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen.svg?style=for-the-badge&logo=spring" alt="Spring Boot 3.5.5">
  <img src="https://img.shields.io/badge/PostgreSQL-14-blue.svg?style=for-the-badge&logo=postgresql" alt="PostgreSQL 14">
  <img src="https://img.shields.io/badge/Maven-3.6+-orange.svg?style=for-the-badge&logo=apache-maven" alt="Maven">
  <img src="https://img.shields.io/badge/JUnit-5-green.svg?style=for-the-badge&logo=junit5" alt="JUnit 5">
</p>

---

## Sobre o Projeto
Esta é uma **API RESTful** desenvolvida para gerenciar as operações de uma biblioteca, permitindo o controle de **livros, usuários e empréstimos**, seguindo um conjunto de regras de negócio.

A API foi construída utilizando uma **arquitetura em camadas** (Controller, Service, Repository) para garantir organização, testabilidade e manutenibilidade do código.

---

## Tecnologias Utilizadas
- Java 21  
- Spring Boot 3.5.5  
- Spring Data JPA (Hibernate)  
- PostgreSQL 14  
- Maven (Gerenciador de Dependências)  
- JUnit 5 & Mockito (Testes Unitários)  
- Lombok (Redução de Boilerplate)  
- SpringDoc (OpenAPI 3 - Documentação da API)  

---

## API Endpoints

### Livros (`/api/books`)
| Método | Rota     | Descrição                          |
|--------|----------|------------------------------------|
| POST   | `/`      | Cadastra um novo livro             |
| GET    | `/`      | Lista todos os livros (paginado)   |
| GET    | `/{id}`  | Busca um livro pelo ID             |
| PUT    | `/{id}`  | Atualiza os dados de um livro      |
| DELETE | `/{id}`  | Remove um livro                    |

### Usuários (`/api/users`)
| Método | Rota     | Descrição                          |
|--------|----------|------------------------------------|
| POST   | `/`      | Cadastra um novo usuário           |
| GET    | `/`      | Lista todos os usuários (paginado) |
| GET    | `/{id}`  | Busca um usuário pelo ID           |
| PUT    | `/{id}`  | Atualiza os dados de um usuário    |
| DELETE | `/{id}`  | Remove um usuário                  |

### Empréstimos (`/api/loans`)
| Método | Rota            | Descrição                              |
|--------|-----------------|----------------------------------------|
| POST   | `/`             | Realiza um novo empréstimo             |
| GET    | `/`             | Lista todos os empréstimos (paginado)  |
| GET    | `/user/{userId}`| Lista todos os empréstimos de um usuário |
| PUT    | `/{id}/return`  | Registra a devolução de um empréstimo  |

---

## Como Começar

### Pré-requisitos
- JDK 21 ou superior  
- Maven 3.6 ou superior  
- PostgreSQL 14 ou superior  
- IDE de sua preferência (ex: IntelliJ IDEA, VS Code)

### Instalação e Configuração
Clone o repositório:
```bash
git clone https://github.com/seu-usuario/biblioteca-api.git

cd biblioteca-api
```

Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE biblioteca;
```

Configure o arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres
```

---

## Executando a Aplicação

### 1. Pela IDE (Recomendado)

* Importe o projeto como **Maven Project**.
* Localize a classe `BibliotecaApiApplication.java`.
* Execute o método `main`.
* A aplicação iniciará na porta `8080`.

### 2. Pelo terminal com Maven

```bash
mvn spring-boot:run
```

---

## Documentação da API

A API está documentada com **SpringDoc (Swagger UI)**.
Após iniciar a aplicação, acesse:
 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Executando os Testes

Para rodar os testes unitários:

```bash
mvn test
```

Se estiver usando uma IDE, também é possível rodar os testes diretamente nela.

---
