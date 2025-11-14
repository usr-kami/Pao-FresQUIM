CREATE TABLE IF NOT EXISTS ferias_funcionarios (
    id_ferias INTEGER PRIMARY KEY AUTOINCREMENT,
    id_funcionario INTEGER NOT NULL,
    data_inicio TEXT NOT NULL,
    data_fim TEXT NOT NULL,
    dias_solicitados INTEGER NOT NULL,
    status TEXT CHECK(status IN ('solicitado', 'aprovado', 'em_andamento', 'concluido', 'cancelado')) DEFAULT 'solicitado',
    data_solicitacao TEXT DEFAULT CURRENT_TIMESTAMP,
    observacoes TEXT,
    
    FOREIGN KEY (id_funcionario) REFERENCES funcionarios(id_funcionario)
);