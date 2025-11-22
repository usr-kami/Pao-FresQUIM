
## Descrição do Projeto

O projeto **Pao-FresQUIM** é uma aplicação para gestão de padaria, composta por um backend em Java (Spring Boot) e um frontend web simples.

---

## Tecnologias Utilizadas

**Backend:**
- Java 25+ (Spring Boot)
- Maven
- JPA/Hibernate
- SQLite (banco de dados local, via JDBC)
- Estrutura modular: controllers, services, DTOs, entidades, repositórios

**Frontend:**
- HTML5, CSS3, JavaScript puro
- Consome API REST do backend

---

## Estrutura da Aplicação

- `backend/`: Código fonte do backend, configurações, scripts SQL e CSV para banco de dados.
- `frontend/`: Interface web, arquivos estáticos e recursos visuais.
- `logs/`: Diretório para arquivos de log da aplicação.

---

## Banco de Dados: Criação e Atualização
- O banco é criado e atualizado automaticamente pelo Hibernate/JPA (`ddl-auto: update` no `application.yaml`).
- Scripts SQL para criação das tabelas e constraints estão em `backend/paofresquim/src/main/resources/database/schemas/` e `constraints/`.
- O arquivo `initial_schema.sql` executa os scripts de criação e importa dados dos arquivos CSV para popular as tabelas.
- O carregamento inicial de dados é feito via CSV (`seeding-csv/`) e automatizado por classes Java como `DataLoader.java`.

---

## Portas de Rede Utilizadas
- **Backend (Spring Boot):** Porta padrão configurada é **8080** (`server.port: 8080` no `application.yaml`).
- **Frontend (Live Server para desenvolvimento):** Porta configurada é **5501** (`frontend/.vscode/settings.json`).
- O frontend faz requisições para `http://localhost:8080/api`.

---

## Configurações de Ambiente

### Pré-requisitos
- Java JDK 25 ou superior
- Maven
- SQLite

### Configuração do Backend
1. Para a primeira execução apenas altere:
`ddl-auto: update` para `ddl-auto: create`

---

### Configuração do Frontend
1. Basta abrir o arquivo `frontend/index.html` em um navegador moderno.

---

## Observações
- Scripts SQL para criação e povoamento do banco estão em `backend/paofresquim/src/main/resources/database/`.
- Para desenvolvimento, recomenda-se o uso do VS Code ou IntelliJ IDEA.

---

## Plugins VS Code Utilizados

- **Live Server** (ritwickdey.LiveServer): Para rodar o frontend localmente.
- **Java Extension Pack** (vscjava.vscode-java-pack): Suporte ao desenvolvimento Java.
- **Spring Boot Extension Pack** (Pivotal.vscode-boot-dev-pack): Ferramentas para Spring Boot.
- **SQLite** (alexcvzz.vscode-sqlite): Visualização e manipulação do banco SQLite.
- **Prettier** (esbenp.prettier-vscode): Formatação de código.
- **GitLens** (eamodio.gitlens): Ferramentas avançadas para Git.
