CREATE TABLE IF NOT EXISTS produtos (
    id_produto INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_produto TEXT NOT NULL,
    preco_kg REAL NOT NULL
);