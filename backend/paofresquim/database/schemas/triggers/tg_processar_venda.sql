CREATE TRIGGER IF NOT EXISTS tg_processar_venda
BEFORE INSERT ON vendas
FOR EACH ROW
BEGIN
    SELECT CASE 
        WHEN (SELECT id_produto FROM produtos WHERE id_produto = NEW.id_produto) IS NULL 
        THEN RAISE(ABORT, 'Produto não encontrado')
    END;
    
    UPDATE vendas 
    SET preco_kg = COALESCE(NEW.preco_kg, (SELECT preco_kg FROM produtos WHERE id_produto = NEW.id_produto))
    WHERE id_venda = NEW.id_venda;
    
    UPDATE vendas 
    SET total = NEW.peso_vendido * COALESCE(NEW.preco_kg, (SELECT preco_kg FROM produtos WHERE id_produto = NEW.id_produto))
    WHERE id_venda = NEW.id_venda;
END;