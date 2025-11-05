ALTER TABLE vendas 
ADD FOREIGN KEY (id_produto) REFERENCES produtos(id_produto);