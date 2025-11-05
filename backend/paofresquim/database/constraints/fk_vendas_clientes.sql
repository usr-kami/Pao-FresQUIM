ALTER TABLE vendas 
ADD FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente);