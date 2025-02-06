# Projeto de Gerenciamento de Usuários

Este projeto é uma aplicação Spring Boot para gerenciamento de usuários, incluindo funcionalidades de registro, login e validação de token JWT. A aplicação utiliza AWS Cognito para autenticação e autorização.

## Endpoints da API

### Registro de Usuário

**POST /user/register**

Registra um novo usuário.

- **Request Body:**
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

- **Responses:**
  - **201 Created**
    ```json
    {
      "message": "Usuário registrado com sucesso!"
    }
    ```
  - **409 Conflict**
    ```json
    {
      "message": "Usuário já cadastrado."
    }
    ```
  - **400 Bad Request**
    ```json
    {
      "message": "Erro de validação de email."
    }
    ```
  - **500 Internal Server Error**
    ```json
    {
      "message": "Erro interno: descrição do erro."
    }
    ```

### Login de Usuário

**POST /user/login**

Realiza o login de um usuário.

- **Request Body:**
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

- **Responses:**
  - **200 OK**
    ```json
    {
      "token": "string"
    }
    ```
  - **400 Bad Request**
    ```json
    {
      "message": "Descrição do erro."
    }
    ```

### Validação de Token

**GET /user/validateToken**

Valida um token JWT e retorna o email associado.

- **Request Parameters:**
  - `token`: O token JWT a ser validado.

- **Responses:**
  - **200 OK**
    ```json
    {
      "email": "string"
    }
    ```
  - **403 Forbidden**
    ```json
    {
      "message": "Descrição do erro."
    }
    ```

## Variáveis de Ambiente

Para que a aplicação funcione corretamente, é necessário configurar as seguintes variáveis de ambiente:

- `AWS_ACCESS_KEY_ID`: Chave de acesso da AWS.
- `AWS_SECRET_KEY`: Chave secreta da AWS.
- `AWS_SESSION_TOKEN`: Token de sessão da AWS.
- `AWS_COGNITO_USER_POOL_ID`: ID do pool de usuários do AWS Cognito.
- `AWS_COGNITO_APP_CLIENT_ID`: ID do cliente da aplicação no AWS Cognito.
- `AWS_COGNITO_APP_CLIENT_SECRET`: Segredo do cliente da aplicação no AWS Cognito.
- `AWS_REGION`: Região da AWS onde os serviços estão configurados.

## Rodando a Aplicação com Docker

Para rodar a aplicação utilizando Docker, siga os passos abaixo:

1. Certifique-se de ter o Docker e o Docker Compose instalados em sua máquina.

2. Configure as variáveis de ambiente necessárias no arquivo `docker-compose.yaml` ou defina-as diretamente no ambiente.

3. No diretório raiz do projeto, execute o comando abaixo para construir e iniciar os containers:
   ```sh
   docker-compose up --build
   ```

4. A aplicação estará disponível na porta `8080`.

5. Para parar os containers, utilize o comando:
   ```sh
   docker-compose down
   ```
