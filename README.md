# FanConnect API 

A FanConnect é uma plataforma focada na gestão acadêmica integrada, conectando o feed de notícias da instituição diretamente à agenda pessoal do aluno.

## Repositórios

- 🖥️ **Frontend:** [fanconnect-front](https://github.com/PedrooHSN-Dev/FanConnect-Front)

##  Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.3.0**
* **Spring Security & JWT** (Autenticação profissional)
* **Spring Data JPA** (Persistência de dados)
* **H2 Database** (Em memória para desenvolvimento)
* **BCrypt** (Hashing de senhas)
* **Maven** (Gestão de dependências)

##  Arquitetura do Sistema

* **Autenticação:** Sistema stateless utilizando tokens JWT.
* **Segurança:** Filtros de segurança personalizados para bloqueio de rotas não autorizadas.
* **Backend:** Arquitetura baseada em Controller/Service/Repository.
* **Notificações:** Job agendado (@Scheduled) com disparo de e-mails via SMTP.

##  Como rodar o projeto

1. **Clone o repositório.**
2. **Configuração de Ambiente:**
   Crie as seguintes variáveis de ambiente no seu IDE ou sistema:
    * `FANCONNECT_EMAIL_USER`: Seu e-mail de teste.
    * `FANCONNECT_EMAIL_PASS`: Senha de app do Google.
    * `JWT_SECRET`: Uma chave secreta longa e aleatória para assinar os tokens.

3. **Rodar:**
   Execute a classe `FanconnectApiApplication.java` como uma aplicação Java padrão.
   A API estará disponível em: `http://localhost:8080`

##  Documentação de Segurança

* A API utiliza autenticação baseada em Token.
* Para acessar rotas protegidas (como listar agenda ou ver feed), você deve enviar o token no header:
  `Authorization: Bearer <seu_token_aqui>`
* As rotas de `/api/usuarios/login` e `/api/usuarios` (cadastro) estão liberadas publicamente.

##  Endpoints Principais

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| POST | `/api/usuarios` | Cadastra um novo aluno. |
| POST | `/api/usuarios/login` | Realiza login e retorna token JWT. |
| GET | `/api/feed` | Lista postagens por relevância. |
| GET | `/api/agenda/global` | Lista eventos oficiais da instituição. |

##  Centro de Comando e Links Úteis

Aqui estão os atalhos e comandos necessários para o desenvolvimento diário:

###  Links Importantes
* [Postman Collection - FanConnect](https://pedroohsn-dev-3150744.postman.co/workspace/FanConnect~d2d31c2c-a069-4c8b-84be-3f9437a1012a/collection/53017966-9b73ce00-8606-4f62-b20d-00838a191630?action=share&source=copy-link&creator=53017966)
* [Console H2 (Em memória)](http://localhost:8080/h2-console/)

###  Comandos Úteis (SQL para Testes)
Para popular o banco com um usuário de teste (Aluno padrão):

```sql
INSERT INTO usuarios (nome, email, senha, matricula, tipo_perfil, data_criacao)
VALUES ('aluno', 'aluno@gmail.com', '$2a$10$TKv6u6GkCKHlVJlBub8qSeRJtWE0.UDa0YkbVcX0UL6bmCmCvXoFi', '2026001', 'ALUNO', CURRENT_TIMESTAMP);
```

---