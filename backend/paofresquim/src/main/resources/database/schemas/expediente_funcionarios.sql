CREATE TABLE IF NOT EXISTS expediente_funcionarios (
    id_expediente INTEGER PRIMARY KEY AUTOINCREMENT,
    id_funcionario INTEGER NOT NULL,
    dia_semana TEXT CHECK(dia_semana IN ('segunda', 'terca', 'quarta', 'quinta', 'sexta', 'sabado', 'domingo')),
    hora_entrada TEXT NOT NULL, -- formato '08:00'
    hora_saida TEXT NOT NULL,   -- formato '17:00'
    turno TEXT CHECK(turno IN ('manha', 'tarde', 'noite', 'integral')) DEFAULT 'manha',
    
    FOREIGN KEY (id_funcionario) REFERENCES funcionarios(id_funcionario)
);