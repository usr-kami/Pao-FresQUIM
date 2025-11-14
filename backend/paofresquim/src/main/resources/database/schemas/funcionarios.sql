CREATE TABLE IF NOT EXISTS funcionarios (
    id_funcionario INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    telefone TEXT,
    email TEXT UNIQUE,
    cargo TEXT CHECK(cargo IN ('padeiro', 'atendente', 'gerente', 'auxiliar')) DEFAULT 'atendente',
    salario_base REAL DEFAULT 0,
    data_admissao TEXT DEFAULT CURRENT_TIMESTAMP,
    ativo INTEGER DEFAULT 1
);