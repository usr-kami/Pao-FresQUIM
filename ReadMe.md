
## Descrição do Projeto

O projeto **Pao-FresQUIM** é uma aplicação para gestão de padaria, composta por um backend em Java (Spring Boot) e um frontend web simples.

---

## Tecnologias Utilizadas

**Backend:**
- Java 25
- Spring Boot 3.5.7
- Maven 4
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

## 📋 TABELA DE ENDPOINTS - Pao FresQUIM

### 👥 ENTIDADE: CLIENTES

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/clientes` | - | `[{idCliente, nome, email, telefone, dataCadastro}]` | Lista todos os clientes |
| GET | `/api/clientes/{id}` | - | `{idCliente, nome, email, telefone, dataCadastro}` | Busca cliente por ID |
| GET | `/api/clientes/busca?nome={nome}` | - | `[{idCliente, nome, email, telefone, dataCadastro}]` | Busca clientes por nome |
| POST | `/api/clientes` | `{nome, email, telefone}` | `{idCliente, nome, email, telefone, dataCadastro}` | Cria novo cliente |
| PUT | `/api/clientes/{id}` | `{nome, email, telefone}` | `{idCliente, nome, email, telefone, dataCadastro}` | Atualiza cliente |
| DELETE | `/api/clientes/{id}` | - | - | Deleta cliente |

### 🍞 ENTIDADE: PRODUTOS

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/produtos` | - | `[{idProduto, nomeProduto, precoKg}]` | Lista todos os produtos |
| GET | `/api/produtos/{id}` | - | `{idProduto, nomeProduto, precoKg}` | Busca produto por ID |
| GET | `/api/produtos/busca?nome={nome}` | - | `[{idProduto, nomeProduto, precoKg}]` | Busca produtos por nome |
| GET | `/api/produtos/faixa-preco?precoMin=X&precoMax=Y` | - | `[{idProduto, nomeProduto, precoKg}]` | Busca por faixa de preço |
| POST | `/api/produtos` | `{nomeProduto, precoKg}` | `{idProduto, nomeProduto, precoKg}` | Cria novo produto |
| PUT | `/api/produtos/{id}` | `{nomeProduto, precoKg}` | `{idProduto, nomeProduto, precoKg}` | Atualiza produto |
| DELETE | `/api/produtos/{id}` | - | - | Deleta produto |

### 📦 ENTIDADE: ESTOQUE INGREDIENTES

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/estoque-ingredientes` | - | `[{idIngrediente, nomeIngrediente, quantidadeEstoque, unidadeMedida, estoqueMinimo, custoMedio, dataAtualizacao, precisaRepor}]` | Lista todos os ingredientes |
| GET | `/api/estoque-ingredientes/{id}` | - | `{idIngrediente, nomeIngrediente, quantidadeEstoque, unidadeMedida, estoqueMinimo, custoMedio, dataAtualizacao, precisaRepor}` | Busca ingrediente por ID |
| GET | `/api/estoque-ingredientes/busca?nome={nome}` | - | `[{idIngrediente, nomeIngrediente, ...}]` | Busca ingredientes por nome |
| GET | `/api/estoque-ingredientes/estoque-minimo` | - | `[{idIngrediente, nomeIngrediente, ...}]` | Busca ingredientes com estoque mínimo |
| GET | `/api/estoque-ingredientes/alerta-reposicao` | - | `[{idIngrediente, nomeIngrediente, ...}]` | Busca ingredientes para reposição |
| POST | `/api/estoque-ingredientes` | `{nomeIngrediente, quantidadeEstoque, unidadeMedida, estoqueMinimo, custoMedio}` | `{idIngrediente, nomeIngrediente, ...}` | Cria novo ingrediente |
| PUT | `/api/estoque-ingredientes/{id}` | `{nomeIngrediente, quantidadeEstoque, unidadeMedida, estoqueMinimo, custoMedio}` | `{idIngrediente, nomeIngrediente, ...}` | Atualiza ingrediente |
| PATCH | `/api/estoque-ingredientes/{id}/quantidade?novaQuantidade=X` | - | `{idIngrediente, nomeIngrediente, ...}` | Atualiza quantidade do ingrediente |
| PATCH | `/api/estoque-ingredientes/{id}/custo?novoCusto=X` | - | `{idIngrediente, nomeIngrediente, ...}` | Atualiza custo do ingrediente |
| DELETE | `/api/estoque-ingredientes/{id}` | - | - | Deleta ingrediente |

### 👨‍💼 ENTIDADE: FUNCIONÁRIOS

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/funcionarios` | - | `[{idFuncionario, nome, telefone, email, cargo, salarioBase, dataAdmissao, ativo}]` | Lista todos os funcionários |
| GET | `/api/funcionarios/{id}` | - | `{idFuncionario, nome, telefone, email, cargo, salarioBase, dataAdmissao, ativo}` | Busca funcionário por ID |
| GET | `/api/funcionarios/busca?nome={nome}` | - | `[{idFuncionario, nome, ...}]` | Busca funcionários por nome |
| GET | `/api/funcionarios/cargo?cargo={cargo}` | - | `[{idFuncionario, nome, ...}]` | Busca funcionários por cargo |
| GET | `/api/funcionarios/status?ativo={true/false}` | - | `[{idFuncionario, nome, ...}]` | Busca funcionários por status |
| POST | `/api/funcionarios` | `{nome, telefone, email, cargo, salarioBase, ativo}` | `{idFuncionario, nome, ...}` | Cria novo funcionário |
| PUT | `/api/funcionarios/{id}` | `{nome, telefone, email, cargo, salarioBase, ativo}` | `{idFuncionario, nome, ...}` | Atualiza funcionário |
| PATCH | `/api/funcionarios/{id}/inativar` | - | `{idFuncionario, nome, ...}` | Inativa funcionário |
| PATCH | `/api/funcionarios/{id}/ativar` | - | `{idFuncionario, nome, ...}` | Ativa funcionário |
| DELETE | `/api/funcionarios/{id}` | - | - | Deleta funcionário |

### 🏪 ENTIDADE: EXPEDIENTE

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/expediente` | - | `[{idExpediente, idFuncionario, nomeFuncionario, cargoFuncionario, diaSemana, horaEntrada, horaSaida, turno}]` | Lista todos os expedientes |
| GET | `/api/expediente/{id}` | - | `{idExpediente, idFuncionario, nomeFuncionario, ...}` | Busca expediente por ID |
| GET | `/api/expediente/funcionario/{idFuncionario}` | - | `[{idExpediente, idFuncionario, nomeFuncionario, ...}]` | Busca expedientes por funcionário |
| GET | `/api/expediente/dia/{diaSemana}` | - | `[{idExpediente, idFuncionario, nomeFuncionario, ...}]` | Busca expedientes por dia da semana |
| GET | `/api/expediente/turno/{turno}` | - | `[{idExpediente, idFuncionario, nomeFuncionario, ...}]` | Busca expedientes por turno |
| POST | `/api/expediente` | `{idFuncionario, diaSemana, horaEntrada, horaSaida, turno}` | `{idExpediente, idFuncionario, nomeFuncionario, ...}` | Cria novo expediente |
| PUT | `/api/expediente/{id}` | `{idFuncionario, diaSemana, horaEntrada, horaSaida, turno}` | `{idExpediente, idFuncionario, nomeFuncionario, ...}` | Atualiza expediente |
| DELETE | `/api/expediente/{id}` | - | - | Deleta expediente |

### 🏖️ ENTIDADE: FÉRIAS

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/ferias` | - | `[{idFerias, idFuncionario, nomeFuncionario, cargoFuncionario, dataInicio, dataFim, diasSolicitados, status, dataSolicitacao, observacoes}]` | Lista todas as férias |
| GET | `/api/ferias/{id}` | - | `{idFerias, idFuncionario, nomeFuncionario, ...}` | Busca férias por ID |
| GET | `/api/ferias/funcionario/{idFuncionario}` | - | `[{idFerias, idFuncionario, nomeFuncionario, ...}]` | Busca férias por funcionário |
| GET | `/api/ferias/status/{status}` | - | `[{idFerias, idFuncionario, nomeFuncionario, ...}]` | Busca férias por status |
| POST | `/api/ferias` | `{idFuncionario, dataInicio, dataFim, observacoes}` | `{idFerias, idFuncionario, nomeFuncionario, ...}` | Solicita férias |
| PUT | `/api/ferias/{id}` | `{idFuncionario, dataInicio, dataFim, observacoes}` | `{idFerias, idFuncionario, nomeFuncionario, ...}` | Atualiza férias |
| PATCH | `/api/ferias/{id}/status?status={status}` | - | `{idFerias, idFuncionario, nomeFuncionario, ...}` | Atualiza status das férias |
| DELETE | `/api/ferias/{id}` | - | - | Deleta férias |

### 💰 ENTIDADE: VENDAS

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/vendas` | - | `[{idVenda, idCliente, nomeCliente, idProduto, nomeProduto, pesoVendido, precoKg, total, formaPagamento, statusPagamento, dataVenda, dataVencimento}]` | Lista todas as vendas |
| GET | `/api/vendas/{id}` | - | `{idVenda, idCliente, nomeCliente, ...}` | Busca venda por ID |
| GET | `/api/vendas/cliente/{idCliente}` | - | `[{idVenda, idCliente, nomeCliente, ...}]` | Busca vendas por cliente |
| GET | `/api/vendas/produto/{idProduto}` | - | `[{idVenda, idCliente, nomeCliente, ...}]` | Busca vendas por produto |
| GET | `/api/vendas/periodo?inicio={data}&fim={data}` | - | `[{idVenda, idCliente, nomeCliente, ...}]` | Busca vendas por período |
| GET | `/api/vendas/status-pagamento?status={status}` | - | `[{idVenda, idCliente, nomeCliente, ...}]` | Busca vendas por status de pagamento |
| GET | `/api/vendas/forma-pagamento?forma={forma}` | - | `[{idVenda, idCliente, nomeCliente, ...}]` | Busca vendas por forma de pagamento |
| POST | `/api/vendas` | `{idProduto, idCliente, pesoVendido, precoKg, formaPagamento, statusPagamento}` | `{idVenda, idCliente, nomeCliente, ...}` | Cria nova venda |
| PUT | `/api/vendas/{id}` | `{idProduto, idCliente, pesoVendido, precoKg, formaPagamento, statusPagamento}` | `{idVenda, idCliente, nomeCliente, ...}` | Atualiza venda |
| PATCH | `/api/vendas/{id}/status-pagamento?status={status}` | - | `{idVenda, idCliente, nomeCliente, ...}` | Atualiza status de pagamento |
| DELETE | `/api/vendas/{id}` | - | - | Deleta venda |

### 📊 ENTIDADE: DASHBOARD

| Método HTTP | Endpoint | Body Request JSON | Body Response JSON | Descrição |
|-------------|----------|-------------------|-------------------|-----------|
| GET | `/api/dashboard` | - | `{metricasVendas: {vendasHoje, vendasMes, totalVendasHoje, totalVendasMes, ticketMedio, crescimentoPercentual}, produtosMaisVendidos: [{idProduto, nomeProduto, quantidadeVendida, totalVendas, numeroVendas}], clientesTop: [{idCliente, nomeCliente, totalCompras, numeroCompras, ticketMedio}], metricasFuncionarios: {totalFuncionarios, funcionariosAtivos, funcionariosFerias, expedientesHoje}, alertasEstoque: {ingredientesParaRepor, produtosEstoqueMinimo, alertasCriticos}}` | Obtém dados completos do dashboard |
| GET | `/api/dashboard/vendas-hoje` | - | `number` | Obtém vendas do dia atual |
| GET | `/api/dashboard/vendas-mes` | - | `number` | Obtém vendas do mês atual |
| GET | `/api/dashboard/produtos-mais-vendidos` | - | `[{idProduto, nomeProduto, quantidadeVendida, totalVendas, numeroVendas}]` | Obtém produtos mais vendidos |
| GET | `/api/dashboard/clientes-top` | - | `[{idCliente, nomeCliente, totalCompras, numeroCompras, ticketMedio}]` | Obtém clientes top |
| GET | `/api/dashboard/metricas-funcionarios` | - | `{totalFuncionarios, funcionariosAtivos, funcionariosFerias, expedientesHoje}` | Obtém métricas de funcionários |
| GET | `/api/dashboard/alertas-estoque` | - | `{ingredientesParaRepor, produtosEstoqueMinimo, alertasCriticos}` | Obtém alertas de estoque |

---

## 📝 LEGENDA DOS CAMPOS:

- **idCliente/idProduto/etc.**: Identificador único (Long)
- **nome/nomeProduto/etc.**: Texto (String)
- **email/telefone**: Texto (String)
- **dataCadastro/dataVenda/etc.**: Data e hora (LocalDateTime)
- **precoKg/quantidadeEstoque/salarioBase/total**: Números decimais (Double)
- **ativo**: Booleano (true/false)
- **status**: Texto com valores específicos (ex: "solicitado", "aprovado", etc.)
- **formaPagamento**: "dinheiro", "cartao", "pix", "fiado"
- **statusPagamento**: "pago", "pendente"
- **cargo**: "padeiro", "atendente", "gerente", "auxiliar"
- **turno**: "manha", "tarde", "noite", "integral"
- **diaSemana**: "segunda", "terca", "quarta", "quinta", "sexta", "sabado", "domingo"
