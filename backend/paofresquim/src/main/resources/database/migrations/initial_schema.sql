.read schemas/clientes.sql
.read schemas/produtos.sql
.read schemas/estoque_ingredientes.sql
.read schemas/vendas.sql
.read constraints/fk_vendas_clientes.sql
.read constraints/fk_vendas_produtos.sql
.read schemas/funcionarios.sql
.read schemas/ferias_funcionarios.sql
.read schemas/expediente_funcionario.sql
.read constraints/fk_ferias_funcionarios.sql
.read constraints/fk_expediente_funcionario.sql

.mode csv
.import seeding-csv/clientes.csv clientes
.import seeding-csv/produtos.csv produtos
.import seeding-csv/estoque_ingredientes.csv estoque_ingredientes
.import seeding-csv/vendas.csv vendas
