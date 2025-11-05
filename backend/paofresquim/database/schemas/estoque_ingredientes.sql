CREATE TABLE IF NOT EXISTS estoque_ingredientes (
    id_ingrediente INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_ingrediente TEXT NOT NULL,
    quantidade_estoque REAL NOT NULL,
    unidade_medida TEXT CHECK(unidade_medida IN ('kg', 'g', 'un', 'lt', 'ml')) DEFAULT 'kg',
    estoque_minimo REAL DEFAULT 0,
    custo_medio REAL DEFAULT 0,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP
);