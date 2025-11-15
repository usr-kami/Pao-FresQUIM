package com.paofresquim.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INICIANDO CARGA DE DADOS INICIAIS ===");
        
        if (isTableEmpty("clientes")) {
            loadCsvData("clientes", "database/seeding-csv/clientes.csv");
        }
        
        if (isTableEmpty("produtos")) {
            loadCsvData("produtos", "database/seeding-csv/produtos.csv");
        }
        
        if (isTableEmpty("estoque_ingredientes")) {
            loadCsvData("estoque_ingredientes", "database/seeding-csv/estoque_ingredientes.csv");
        }
        
        if (isTableEmpty("funcionarios")) {
            loadCsvData("funcionarios", "database/seeding-csv/funcionarios.csv");
        }
        
        if (isTableEmpty("vendas")) {
            loadCsvData("vendas", "database/seeding-csv/vendas.csv");
        }
        
        if (isTableEmpty("expediente_funcionario")) {
            loadCsvData("expediente_funcionario", "database/seeding-csv/expediente_funcionario.csv");
        }
        
        if (isTableEmpty("ferias_funcionarios")) {
            loadCsvData("ferias_funcionarios", "database/seeding-csv/ferias_funcionarios.csv");
        }
        
        logger.info("=== CARGA DE DADOS CONCLUÍDA ===");
    }

    private boolean isTableEmpty(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + tableName, Integer.class);
            logger.info("Tabela {}: {} registros", tableName, count);
            return count == 0;
        } catch (Exception e) {
            logger.warn("Tabela {} não encontrada ou vazia: {}", tableName, e.getMessage());
            return true;
        }
    }

    private void loadCsvData(String tableName, String csvPath) {
        try {
            ClassPathResource resource = new ClassPathResource(csvPath);
            if (!resource.exists()) {
                logger.error("Arquivo CSV não encontrado: {}", csvPath);
                return;
            }

            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                String csvContent = FileCopyUtils.copyToString(reader);
                String[] lines = csvContent.split("\n");
                
                if (lines.length <= 1) {
                    logger.warn("Arquivo CSV vazio ou apenas cabeçalho: {}", csvPath);
                    return;
                }

                int loadedCount = 0;
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (!line.isEmpty()) {
                        String[] values = parseCsvLine(line);
                        insertCsvRow(tableName, values);
                        loadedCount++;
                    }
                }
                
                logger.info("✅ {}: {} registros carregados", tableName, loadedCount);
            }
        } catch (Exception e) {
            logger.error("❌ Erro ao carregar CSV {}: {}", csvPath, e.getMessage());
        }
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private void insertCsvRow(String tableName, String[] values) {
        try {
            switch (tableName) {
                case "clientes":
                    jdbcTemplate.update("INSERT INTO clientes (id_cliente, nome, email, telefone, data_cadastro) VALUES (?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), values[1], values[2], values[3], values[4]);
                    break;
                case "produtos":
                    jdbcTemplate.update("INSERT INTO produtos (id_produto, nome_produto, preco_kg) VALUES (?, ?, ?)",
                        Long.parseLong(values[0]), values[1], Double.parseDouble(values[2]));
                    break;
                case "estoque_ingredientes":
                    jdbcTemplate.update("INSERT INTO estoque_ingredientes (id_ingrediente, nome_ingrediente, quantidade_estoque, unidade_medida, estoque_minimo, custo_medio, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), values[1], Double.parseDouble(values[2]), values[3], 
                        Double.parseDouble(values[4]), Double.parseDouble(values[5]), values[6]);
                    break;
                case "funcionarios":
                    jdbcTemplate.update("INSERT INTO funcionarios (id_funcionario, nome, telefone, email, cargo, salario_base, data_admissao, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), values[1], values[2], values[3], values[4],
                        Double.parseDouble(values[5]), values[6], Integer.parseInt(values[7]));
                    break;
                case "vendas":
                    jdbcTemplate.update("INSERT INTO vendas (id_venda, id_cliente, id_produto, peso_vendido, preco_kg, total, forma_pagamento, status_pagamento, data_venda, data_vencimento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), 
                        values[1].isEmpty() ? null : Long.parseLong(values[1]),
                        Long.parseLong(values[2]), Double.parseDouble(values[3]), 
                        Double.parseDouble(values[4]), Double.parseDouble(values[5]),
                        values[6], values[7], values[8],
                        values.length > 9 && !values[9].isEmpty() ? values[9] : null);
                    break;
                case "expediente_funcionario":
                    jdbcTemplate.update("INSERT INTO expediente_funcionario (id_expediente, id_funcionario, dia_semana, hora_entrada, hora_saida, turno) VALUES (?, ?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), Long.parseLong(values[1]), values[2], values[3], values[4], values[5]);
                    break;
                case "ferias_funcionarios":
                    jdbcTemplate.update("INSERT INTO ferias_funcionarios (id_ferias, id_funcionario, data_inicio, data_fim, dias_solicitados, status, data_solicitacao, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Long.parseLong(values[0]), Long.parseLong(values[1]), values[2], values[3],
                        Integer.parseInt(values[4]), values[5], values[6],
                        values.length > 7 ? values[7] : null);
                    break;
                default:
                    logger.warn("Tabela não mapeada: {}", tableName);
            }
        } catch (Exception e) {
            logger.error("Erro ao inserir registro na tabela {}: {}", tableName, e.getMessage());
        }
    }
}