CREATE TABLE IF NOT EXISTS vendas (
    id_venda INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER,
    id_produto INTEGER NOT NULL,
    peso_vendido REAL NOT NULL,
    preco_kg REAL NOT NULL,
    total REAL NOT NULL,
    forma_pagamento TEXT CHECK(forma_pagamento IN ('dinheiro', 'cartao', 'pix', 'fiado')) DEFAULT 'dinheiro',
    status_pagamento TEXT CHECK(status_pagamento IN ('pago', 'pendente')) DEFAULT 'pago',
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_vencimento DATE
);