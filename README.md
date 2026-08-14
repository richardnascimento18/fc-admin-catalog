# FC Admin Catalog

Microsserviço responsável pelo gerenciamento do catálogo de uma plataforma de streaming. O projeto foi estruturado para aplicar conceitos de Domain-Driven Design, arquitetura limpa e separação de responsabilidades em uma aplicação Java.

## Sobre o projeto

O sistema concentra as regras de negócio relacionadas às categorias do catálogo. Atualmente, o domínio contempla:

- criação de categorias;
- alteração de nome, descrição e status de uma categoria;
- ativação e desativação de categorias;
- consulta de uma categoria por identificador;
- listagem paginada com suporte a termos de busca, ordenação e direção de ordenação;
- validação das regras do domínio;
- controle das datas de criação, atualização e desativação;
- persistência das categorias em um banco de dados MySQL.

As categorias possuem identificador único, nome, descrição, status de atividade e informações de auditoria. A desativação é representada pelo status inativo e pela data de desativação, preservando o registro no banco de dados.

## Arquitetura

O código é dividido em três módulos Gradle, cada um com uma responsabilidade específica:

### `domain`

Contém o núcleo do negócio e não depende de frameworks externos. Nesse módulo estão as entidades, agregados, identificadores, objetos de valor, validações, exceções de domínio, gateways, paginação e a entidade `Category`.

### `application`

Implementa os casos de uso da aplicação e depende apenas do módulo de domínio. Inclui os fluxos de criação, atualização, remoção, consulta por identificador e listagem de categorias, além dos comandos e objetos de saída utilizados por esses fluxos.

### `infrastructure`

Conecta a aplicação ao ambiente externo. Esse módulo inicializa o Spring Boot, configura o servidor web, implementa a persistência com Spring Data JPA, disponibiliza o gateway MySQL e executa as migrações do banco de dados com Flyway.

Essa divisão mantém as regras de negócio isoladas dos detalhes de persistência e configuração. O domínio define as interfaces necessárias, enquanto a infraestrutura fornece as implementações concretas.

## Tecnologias utilizadas

- Java;
- Gradle, com Gradle Wrapper;
- Spring Boot 2.6.7;
- Spring Web;
- Undertow como servidor web embutido;
- Spring Data JPA e Hibernate;
- MySQL;
- Flyway para versionamento do schema do banco;
- H2 para testes de integração;
- JUnit 5 para testes automatizados;
- Mockito para testes dos casos de uso;
- Vavr para apoio à programação funcional na camada de aplicação;
- Docker Compose para execução local do MySQL.

## Banco de dados

O arquivo `V1__Initial.sql` cria a tabela `category`, com os seguintes campos:

- `id`: identificador da categoria;
- `name`: nome da categoria;
- `description`: descrição opcional;
- `active`: indica se a categoria está ativa;
- `created_at`: data de criação;
- `updated_at`: data da última atualização;
- `deleted_at`: data de desativação, quando aplicável.

As configurações de conexão estão separadas por ambiente nos arquivos `application-development.yml`, `application-test.yml` e `application-production.yml`.

## Pré-requisitos

- JDK compatível com a versão utilizada pelo projeto;
- Docker e Docker Compose, para executar o MySQL;
- acesso à porta `3306` para o banco de dados;
- acesso à porta `8080` para a aplicação.

## Como executar

### 1. Inicie o MySQL

Na raiz do projeto, execute:

```bash
docker compose up -d
```

O container será criado com o nome `adm_videos_mysql`, o banco `adm_videos`, o usuário `root` e a senha padrão `123456`.

### 2. Execute as migrações

Com o MySQL disponível, execute:

```bash
./gradlew :infrastructure:flywayMigrate
```

### 3. Inicie a aplicação

```bash
./gradlew :infrastructure:bootRun
```

Por padrão, a aplicação utiliza o perfil `development`, inicia na porta `8080` e se conecta ao MySQL em `localhost:3306`.

Também é possível gerar o arquivo executável:

```bash
./gradlew clean build
```

O arquivo `application.jar` será gerado em `build/libs`.

## Testes

Para executar todos os testes:

```bash
./gradlew test
```

Os testes abrangem as regras e validações do domínio, os casos de uso da aplicação e a integração da persistência. Os testes que utilizam o perfil de teste usam um banco H2 em memória configurado para compatibilidade com o MySQL.

## Estado atual

O projeto estabelece a base do microsserviço e implementa o domínio de categorias, seus casos de uso, a configuração da aplicação e o mapeamento de persistência. A camada de entrada HTTP ainda não possui controladores REST neste estágio. Além disso, alguns métodos do gateway MySQL, como consulta, atualização, remoção e listagem, permanecem como pontos de continuidade do desenvolvimento.
