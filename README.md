# TaskSync – Sistema de Gestão de Tarefas

Projeto desenvolvido para a disciplina Gestão e Qualidade de Software (GQS) no Centro Universitário UNA.

## Objetivo

Desenvolver um sistema funcional de gerenciamento de tarefas, com operações básicas de CRUD, versionamento de código utilizando Git Flow, commits semânticos e aplicação de boas práticas de desenvolvimento e testes unitários.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Maven
- JPA (Hibernate)
- MySQL
- Docker (opcional)
- JUnit 5 (testes unitários)

## Funcionalidades

- Cadastro de tarefas
- Edição de tarefas
- Exclusão de tarefas
- Registro com data e descrição
- Testes unitários aplicados
- Versionamento com Git Flow e commits semânticos

## Como Executar

```bash
# 1. Clonar o repositório
git clone https://github.com/SEU-USUARIO/taskSync.git

# 2. Acessar o diretório
cd taskSync

# 3. Configurar o banco de dados (MySQL) ou usar docker-compose.yml

# 4. Executar a aplicação
./mvnw spring-boot:run
