# Minha Estante 📚

API REST para gerenciar uma estante de livros, desenvolvida individualmente com **Java 21**, **Spring Boot 4.1.2** e **PostgreSQL 15**.

O projeto foi utilizado na atividade **Trabalho Individual de Pesquisa e Aplicação — Aulas 10 e 11**, cujo objetivo é aplicar testes automatizados, empacotamento executável, configuração externa, Docker e observabilidade em uma API Spring Boot.

## Funcionalidades

- Cadastrar livros.
- Listar os livros cadastrados.
- Buscar um livro pelo identificador (UUID).
- Atualizar os dados de um livro.
- Excluir um livro.
- Filtrar a listagem por título, autor, gênero, tipo de leitura e status de leitura, inclusive combinando filtros.
- Validar os dados recebidos e retornar erros HTTP apropriados.

Cada livro possui **título, autor, número de páginas, página atual, gênero, tipo de leitura e status de leitura**, além do UUID.

## Tecnologias

- Java 21 e Spring Boot 4.1.2
- Spring Web, Spring Data JPA e Bean Validation
- PostgreSQL 15
- Maven e Maven Wrapper
- JUnit 5, Mockito e MockMvc
- Testes de integração de repositório com PostgreSQL
- Docker e Docker Compose
- Spring Boot Actuator e logs com SLF4J
- Lombok

## Organização da aplicação

A API utiliza separação em camadas:

| Camada | Responsabilidade |
| --- | --- |
| **Controller** | Receber requisições HTTP e devolver as respostas da API. |
| **Service** | Aplicar validações e regras de negócio. |
| **Repository** | Realizar as operações de persistência no PostgreSQL. |
| **Entity** | Representar os livros armazenados no banco. |
| **DTOs** | Definir os dados de entrada e saída da API. |
| **Tratamento de exceções** | Traduzir erros de validação e registros inexistentes para respostas HTTP. |

## Pré-requisitos

- **Docker** com Docker Compose, para executar o banco e os containers.
- **Java 21** para executar localmente o projeto ou o JAR.
- Não é necessário instalar o Maven separadamente: o projeto utiliza o Maven Wrapper (`mvnw` / `mvnw.cmd`).
- Um cliente HTTP, como **Postman**, para testar os endpoints.

> Para executar **apenas pelos containers Docker**, não é necessário instalar Java no sistema, desde que o JAR já tenha sido gerado. Para gerar o JAR localmente, é necessário o JDK.

## Configuração do banco de dados

As credenciais não ficam gravadas no código-fonte. O arquivo `.env` é utilizado pelo Docker Compose e **não deve ser enviado ao GitHub**.

Crie um arquivo `.env` na raiz do projeto com valores próprios:

```dotenv
POSTGRES_DB=minha_estante
POSTGRES_USER=seu_usuario
POSTGRES_PASSWORD=sua_senha_segura
```

O `application.properties` recebe a conexão por variáveis de ambiente:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

Os arquivos `application-dev.properties` e `application-prod.properties` contêm as configurações específicas dos profiles `dev` e `prod`.

**Atenção:** o `.env` é lido pelo Docker Compose para interpolar suas variáveis, mas não é carregado automaticamente pelo Spring Boot quando a API é iniciada diretamente pelo IntelliJ, Maven ou `java -jar`.

## Executar com Docker Compose (recomendado)

O `docker-compose.yaml` configura dois serviços:

- **`postgres`**: banco PostgreSQL 15, com volume `postgres_data` para persistência.
- **`api`**: aplicação Spring Boot, conectada ao banco pelo nome do serviço `postgres`, na rede interna do Compose.

Na raiz do projeto, com o `.env` configurado:

**1. Gere o JAR:**

```powershell
.\mvnw.cmd clean package
```

> O comando executa também os testes. Os testes de integração exigem um banco de testes PostgreSQL separado e as variáveis `TEST_DB_*` descritas abaixo. Caso ainda não tenha preparado esse banco, siga primeiro a seção **Testes automatizados**.

**2. Construa e inicie os containers:**

```powershell
docker compose up --build -d
```

**3. Confira o estado dos serviços:**

```powershell
docker compose ps
```

**4. Acesse a API:**

```http
GET http://localhost:8080/livros
```

**5. Consulte os logs:**

```powershell
docker compose logs --tail=50 api
```

**6. Pare os containers quando necessário:**

```powershell
docker compose down
```

O comando acima **não remove o volume persistente**. Evite `docker compose down -v` se quiser preservar os dados cadastrados.

> No Docker, a API acessa `jdbc:postgresql://postgres:5432/minha_estante` (considerando `POSTGRES_DB=minha_estante`). Dentro do container da API, `localhost` identifica o próprio container, não o PostgreSQL.

## Executar localmente sem Docker para a API

É possível executar o PostgreSQL pelo Docker e a aplicação pelo Maven ou pelo JAR.

**1.** Inicie o PostgreSQL, caso ainda não esteja em execução:

```powershell
docker compose up -d postgres
```

**2.** Configure as variáveis no PowerShell atual, usando as credenciais escolhidas para o banco:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/minha_estante"
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha_segura"
$env:SPRING_PROFILES_ACTIVE="dev"
```

**3.** Execute com Maven:

```powershell
.\mvnw.cmd spring-boot:run
```

Ou, após gerar o pacote, execute sem a IDE:

```powershell
java -jar .\target\minha-estante-0.0.1-SNAPSHOT.jar
```

> Para usar a porta `8080` na execução local, pare antes o container `api` ou outra instância da aplicação que esteja utilizando essa porta.

## Endpoints

URL base: `http://localhost:8080`

| Método | Endpoint | Descrição | Resposta esperada no sucesso |
| --- | --- | --- | --- |
| `POST` | `/livros` | Cadastrar livro | `201 Created`* |
| `GET` | `/livros` | Listar e filtrar livros | `200 OK` |
| `GET` | `/livros/{id}` | Buscar livro pelo UUID | `200 OK` |
| `PUT` | `/livros/{id}` | Atualizar livro | `200 OK` |
| `DELETE` | `/livros/{id}` | Excluir livro | `204 No Content` |
| `GET` | `/actuator/health` | Consultar saúde da aplicação | `200 OK`, quando saudável |

\* O código de sucesso do `POST` deve ser conferido com a resposta efetiva do Controller; os demais códigos refletem o comportamento validado durante o desenvolvimento.

### Exemplo de cadastro

```http
POST http://localhost:8080/livros
Content-Type: application/json
```

```json
{
  "titulo": "O Hobbit",
  "autor": "J. R. R. Tolkien",
  "paginas": 310,
  "paginaAtual": 0,
  "genero": "FANTASIA",
  "tipoLeitura": "FISICO",
  "statusLeitura": "QUERO_LER"
}
```

Os campos de enumeração aceitam os valores definidos nos respectivos enums Java da aplicação.

### Exemplos de filtros

```http
GET /livros?titulo=hobbit
GET /livros?autor=tolkien
GET /livros?genero=FANTASIA
GET /livros?tipoLeitura=FISICO&statusLeitura=LENDO
GET /livros?titulo=hobbit&genero=FANTASIA
```

Os filtros de título e autor são parciais e não diferenciam letras maiúsculas de minúsculas. Os filtros por enum usam os nomes das constantes definidas no projeto.

### Erros tratados

- **`400 Bad Request`** para dados inválidos ou regras de validação não atendidas.
- **`404 Not Found`** quando o livro solicitado não existe.

## Testes automatizados — Aula 10

O projeto possui testes automatizados para diferentes responsabilidades:

| Tipo | Ferramentas | O que verifica |
| --- | --- | --- |
| **Service** | JUnit 5 + Mockito | Regras de negócio, cenários de sucesso e erro, sem acessar o banco real. |
| **Controller** | JUnit 5 + MockMvc | Endpoints HTTP, códigos de resposta, entradas inválidas e integração da camada web com o Service simulado. |
| **Tratamento de exceções** | JUnit 5 | Respostas HTTP para erros esperados. |
| **Repository** | Spring Data JPA + PostgreSQL real | Persistência e filtros combináveis no banco. |

Nos testes unitários, `@Mock` simula o Repository, enquanto `@InjectMocks` injeta os mocks na classe testada. `when(...).thenReturn(...)` define um comportamento simulado; as asserções verificam resultados; `verify(...)` verifica interações; e `assertThrows(...)` verifica exceções esperadas.

Os testes seguem a organização **Arrange, Act, Assert**:

1. **Arrange:** preparar dados, dependências e comportamento dos mocks.
2. **Act:** executar a operação que está sendo testada.
3. **Assert:** verificar o resultado e, quando necessário, a interação com as dependências.

### Banco separado para testes de integração

Os testes do `LivroRepositoryTest` utilizam um banco PostgreSQL de testes, independente do banco principal.

Com o container do PostgreSQL em execução, crie o banco **uma única vez** (ajuste o nome de usuário se necessário):

```powershell
docker exec -it postgres-minha-estante psql -U seu_usuario -d minha_estante
```

No terminal `psql`:

```sql
CREATE DATABASE minha_estante_test;
```

Configure no PowerShell:

```powershell
$env:TEST_DB_URL="jdbc:postgresql://localhost:5432/minha_estante_test"
$env:TEST_DB_USER="seu_usuario"
$env:TEST_DB_PASSWORD="sua_senha_segura"
```

Execute todos os testes:

```powershell
.\mvnw.cmd test
```

Ou apenas os testes de persistência:

```powershell
.\mvnw.cmd -Dtest=LivroRepositoryTest test
```

**Resultado registrado no desenvolvimento:** 35 testes executados com sucesso, sem falhas ou erros. Esse número corresponde à execução registrada durante a atividade e pode mudar com novas alterações no código.

> Testes automatizados ajudam a identificar regressões, mas um alto percentual de cobertura não garante, sozinho, a ausência de erros.

## Build e JAR executável — Aula 11

O **build** transforma o código-fonte e seus recursos em um artefato executável. Com o Maven Wrapper:

```powershell
.\mvnw.cmd clean package
```

- `clean` remove os artefatos anteriores do diretório `target`.
- `package` compila, executa os testes aplicáveis e empacota a aplicação.
- O resultado é o JAR `target/minha-estante-0.0.1-SNAPSHOT.jar`.

O JAR pode ser iniciado pelo Java, sem IntelliJ:

```powershell
java -jar .\target\minha-estante-0.0.1-SNAPSHOT.jar
```

Para essa execução, o Java 21 deve estar disponível, assim como as variáveis de ambiente da conexão com o banco.

## Dockerfile e imagem Docker

O projeto utiliza um Dockerfile com Java 21:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/minha-estante-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

- **`FROM`:** escolhe a imagem-base com Java 21.
- **`WORKDIR`:** define o diretório de trabalho do container.
- **`COPY`:** coloca o JAR gerado dentro da imagem.
- **`EXPOSE`:** documenta a porta utilizada pela aplicação.
- **`ENTRYPOINT`:** define o comando executado ao iniciar o container.

Para construir somente a imagem:

```powershell
docker build -t minha-estante-api .
```

**Imagem** é o pacote utilizado para criar containers; **container** é uma instância dessa imagem em execução. O Docker Compose organiza a execução conjunta da API e do banco.

## Logs e observabilidade

A API utiliza logs com **SLF4J** para registrar operações relevantes, como cadastro, atualização e solicitação de exclusão de livros, identificadas pelo UUID.

Consulte os logs no Docker:

```powershell
docker compose logs --tail=50 api
```

Os registros ajudam no acompanhamento da aplicação e na investigação de falhas. **Senhas, tokens e outras informações sensíveis não devem ser registrados em logs.**

### Spring Boot Actuator

O projeto inclui o Actuator para expor o endpoint de saúde:

```http
GET http://localhost:8080/actuator/health
```

Exemplo de resposta obtida durante os testes:

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

`UP` indica que as verificações de saúde configuradas passaram; isso **não significa** que todas as regras de negócio tenham sido testadas. A exposição dos endpoints do Actuator deve ser limitada ao necessário.

## Cuidados e limitações

- O projeto é uma **API acadêmica de nível intermediário**, sem autenticação, microsserviços ou deploy em nuvem.
- O `.env` deve permanecer fora do Git; o `.gitignore` contém uma regra para ignorá-lo.
- Nunca inclua senhas reais no README, no Dockerfile ou nos commits.
- O profile de desenvolvimento utiliza `spring.jpa.hibernate.ddl-auto=update`. Na configuração acadêmica do Docker, o banco também pode ser inicializado com `update`; em produção real, recomenda-se controlar alterações do esquema com migrações.
- O volume Docker preserva os dados entre recriações comuns de containers, mas não substitui backups.

## Evidências e relatório acadêmico

Esta documentação descreve como executar e testar o projeto. **A pesquisa conceitual, as respostas às questões das Aulas 10 e 11 e o estudo de caso são entregues em relatório separado**, conforme as orientações da atividade.

As evidências registradas durante o desenvolvimento incluem:

- Saída do Maven mostrando os testes aprovados.
- Execução do JAR sem IntelliJ e requisição no Postman.
- API e PostgreSQL em execução pelo Docker Compose.
- Requisições CRUD e filtros no Postman.
- Endpoint `/actuator/health` retornando `UP`.
- Logs do Docker mostrando cadastro e exclusão de livros.

## Referências

- **Spring — Spring Boot Reference Documentation.** <https://docs.spring.io/spring-boot/reference/>. Acesso em: 23 nov. 2026.
- **JUnit Team — JUnit User Guide.** <https://docs.junit.org/>. Acesso em: 23 nov. 2026.
- **Mockito — Mockito Documentation.** <https://site.mockito.org/>. Acesso em: 23 nov. 2026.
- **Apache Software Foundation — Maven Introduction to the Build Lifecycle.** <https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html>. Acesso em: 23 nov. 2026.
- **Docker Inc. — Docker Docs.** <https://docs.docker.com/>. Acesso em: 23 nov. 2026.

---

**Atividade individual — Aulas 10 e 11 | Minha Estante**
