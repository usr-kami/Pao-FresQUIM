const API_BASE_URL = 'http://localhost:8080/api';
let currentSection = 'dashboard';

document.addEventListener('DOMContentLoaded', function() {
    atualizarDataAtual();
    configurarNavegacao();
    carregarDashboard();
});

function getTierIcon(tier) {
    switch(tier) {
        case 1: return '🥇';
        case 2: return '🥈';
        case 3: return '🥉';
        case 4: return '⭐';
        case 5: return '⭐';
        default: return '📊';
    }
}

function configurarNavegacao() {
    document.querySelectorAll('.menu-item').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const section = this.getAttribute('data-section');
            mostrarSecao(section);
        });
    });
}

function mostrarSecao(section) {
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
    });
    document.querySelector(`[data-section="${section}"]`).classList.add('active');

    document.querySelectorAll('.section').forEach(sec => {
        sec.classList.remove('active');
    });
    document.getElementById(section).classList.add('active');

    document.getElementById('page-title').textContent = 
        document.querySelector(`[data-section="${section}"]`).textContent;

    currentSection = section;
    
    switch(section) {
        case 'dashboard':
            carregarDashboard();
            break;
        case 'clientes':
            carregarClientes();
            break;
        case 'produtos':
            carregarProdutos();
            break;
        case 'estoque':
            carregarEstoque();
            break;
        case 'vendas':
            carregarVendas();
            break;
        case 'funcionarios':
            carregarFuncionarios();
            break;
        case 'cftv':
            carregarCFTV();
            break;
    }
}

async function apiRequest(endpoint, options = {}) {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
        const config = {
            method: options.method || 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            signal: controller.signal,
            ...options
        };

        if (config.body && typeof config.body === 'object') {
            config.body = JSON.stringify(config.body);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        clearTimeout(timeoutId);

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        if (response.status === 204) {
            return { success: true, message: 'Operação realizada com sucesso' };
        }

        const responseText = await response.text();
        if (!responseText.trim()) {
            return { success: true, message: 'Operação realizada com sucesso' };
        }

        return JSON.parse(responseText);
        
    } catch (error) {
        clearTimeout(timeoutId);
        
        if (error.name === 'AbortError') {
            mostrarErro('Tempo limite excedido. O servidor está demorando para responder.');
        } else if (error.message.includes('Failed to fetch')) {
            mostrarErro('Não foi possível conectar ao servidor.');
        } else {
            mostrarErro(`Erro: ${error.message}`);
        }
        
        return null;
    }
}

function mostrarErro(mensagem) {
    alert(`Erro: ${mensagem}`);
}

function mostrarSucesso(mensagem) {
    alert(`Sucesso: ${mensagem}`);
}

async function carregarDashboard() {
    try {
        const [vendas, clientes, estoque] = await Promise.all([
            apiRequest('/vendas'),
            apiRequest('/clientes'),
            apiRequest('/estoque-ingredientes')
        ]);

        const vendasHoje = calcularVendasHoje(vendas);
        const vendasMes = calcularVendasMes(vendas);
        
        document.getElementById('vendas-hoje').textContent = `R$ ${vendasHoje.toFixed(2)}`;
        document.getElementById('vendas-mes').textContent = `R$ ${vendasMes.toFixed(2)}`;
        document.getElementById('total-clientes').textContent = clientes ? clientes.length : 0;
        
        const alertasEstoque = estoque ? estoque.filter(e => 
            e.precisaRepor || (e.quantidadeEstoque <= e.estoqueMinimo)
        ).length : 0;
        document.getElementById('alertas-estoque').textContent = alertasEstoque;

        await carregarProdutosMaisVendidos(vendas);
        await carregarTopClientesInadimplentes(vendas);

    } catch (error) {
        mostrarErro('Erro ao carregar dashboard: ' + error.message);
    }
}

function calcularVendasHoje(vendas) {
    if (!vendas || vendas.length === 0) return 0;

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    
    const vendasHoje = vendas.filter(venda => {
        const dataVenda = normalizarData(venda.dataVenda);
        if (!dataVenda) return false;
        dataVenda.setHours(0, 0, 0, 0);
        return dataVenda.getTime() === hoje.getTime();
    });

    return vendasHoje.reduce((total, venda) => total + (venda.total || 0), 0);
}

function calcularVendasMes(vendas) {
    if (!vendas || vendas.length === 0) return 0;

    const hoje = new Date();
    const mesAtual = hoje.getMonth();
    const anoAtual = hoje.getFullYear();
    
    const vendasMes = vendas.filter(venda => {
        const dataVenda = normalizarData(venda.dataVenda);
        if (!dataVenda) return false;
        return dataVenda.getMonth() === mesAtual && dataVenda.getFullYear() === anoAtual;
    });

    return vendasMes.reduce((total, venda) => total + (venda.total || 0), 0);
}

function normalizarData(dataString) {
    if (!dataString) return null;
    
    try {
        let data = new Date(dataString);
        if (!isNaN(data.getTime())) return data;
        
        data = new Date(dataString.replace(' ', 'T'));
        if (!isNaN(data.getTime())) return data;
        
        if (dataString.includes('/')) {
            const partes = dataString.split(' ')[0].split('/');
            if (partes.length === 3) {
                data = new Date(partes[2], partes[1] - 1, partes[0]);
                if (!isNaN(data.getTime())) return data;
            }
        }
        
        return null;
    } catch (error) {
        return null;
    }
}

async function carregarProdutosMaisVendidos(vendas) {
    const produtosContainer = document.getElementById('produtos-mais-vendidos');
    
    try {
        if (vendas && vendas.length > 0) {
            const produtosVendidos = {};
            
            vendas.forEach(venda => {
                const produtoNome = venda.nomeProduto || 'Produto não informado';
                if (!produtosVendidos[produtoNome]) {
                    produtosVendidos[produtoNome] = 0;
                }
                produtosVendidos[produtoNome] += 1;
            });
            
            const produtosOrdenados = Object.entries(produtosVendidos)
                .sort(([,a], [,b]) => b - a)
                .slice(0, 3);
            
            const produtosHtml = `
                <div class="top-tier-list">
                    ${produtosOrdenados.map(([nome, quantidade], index) => {
                        const tierClass = `tier-${index + 1}`;
                        const tierIcon = getTierIcon(index + 1);
                        
                        return `
                            <div class="tier-item ${tierClass}">
                                <div class="tier-rank">
                                    <span class="tier-icon">${tierIcon}</span>
                                    <span class="tier-number">${index + 1}</span>
                                </div>
                                <div class="tier-product">
                                    <span class="product-name">${nome}</span>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            `;
            produtosContainer.innerHTML = produtosHtml;
        } else {
            produtosContainer.innerHTML = '<p style="color: #666; text-align: center;">Nenhum produto vendido ainda</p>';
        }
    } catch (error) {
        produtosContainer.innerHTML = '<p style="color: #666; text-align: center;">Erro ao carregar dados</p>';
    }
}

async function carregarTopClientesInadimplentes(vendas) {
    const clientesContainer = document.getElementById('top-clientes-inadimplentes');
    
    try {
        if (vendas && vendas.length > 0) {
            const vendasPendentes = vendas.filter(v => v.statusPagamento === 'pendente' || v.statusPagamento === 'fiado');
            const totalPendencias = vendasPendentes.reduce((total, v) => total + (v.total || 0), 0);
            
            const clientesInadimplentesMap = {};
            vendasPendentes.forEach(venda => {
                const clienteId = venda.idCliente;
                const clienteNome = venda.nomeCliente || 'Cliente não identificado';
                
                if (!clientesInadimplentesMap[clienteId]) {
                    clientesInadimplentesMap[clienteId] = {
                        nome: clienteNome,
                        total: 0,
                        quantidade: 0
                    };
                }
                
                clientesInadimplentesMap[clienteId].total += venda.total || 0;
                clientesInadimplentesMap[clienteId].quantidade += 1;
            });
            
            const clientesInadimplentes = Object.values(clientesInadimplentesMap)
                .sort((a, b) => b.total - a.total)
                .slice(0, 4);
            
            const clientesHtml = `
                <div class="inadimplencia-stats">
                    <div class="stat-item">
                        <span class="stat-label">Total em Pendências:</span>
                        <span class="stat-value inadimplente">R$ ${totalPendencias.toFixed(2)}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Vendas Pendentes:</span>
                        <span class="stat-value">${vendasPendentes.length}</span>
                    </div>
                </div>
                <div class="clientes-inadimplentes">
                    ${clientesInadimplentes.map(cliente => `
                        <div class="cliente-inadimplente">
                            <div class="cliente-info">
                                <span class="cliente-nome">${cliente.nome}</span>
                                <span class="cliente-valor inadimplente">R$ ${cliente.total.toFixed(2)}</span>
                            </div>
                            <span class="cliente-status inadimplente">${cliente.quantidade} pendência${cliente.quantidade > 1 ? 's' : ''}</span>
                        </div>
                    `).join('')}
                    ${clientesInadimplentes.length === 0 ? '<p style="color: #666; text-align: center; padding: 1rem;">Nenhum cliente inadimplente</p>' : ''}
                </div>
            `;
            
            clientesContainer.innerHTML = clientesHtml;
        } else {
            clientesContainer.innerHTML = `
                <div class="inadimplencia-stats">
                    <div class="stat-item">
                        <span class="stat-label">Total em Pendências:</span>
                        <span class="stat-value inadimplente">R$ 0,00</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Vendas Pendentes:</span>
                        <span class="stat-value">0 vendas</span>
                    </div>
                </div>
                <p style="color: #666; text-align: center; padding: 1rem;">Nenhum cliente inadimplente</p>
            `;
        }
    } catch (error) {
        clientesContainer.innerHTML = '<p style="color: #666; text-align: center;">Erro ao carregar dados de inadimplência</p>';
    }
}

async function carregarClientes() {
    const clientes = await apiRequest('/clientes');
    const tbody = document.getElementById('clientes-body');
    
    if (!clientes) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Erro ao carregar clientes</td></tr>';
        return;
    }

    tbody.innerHTML = clientes.map(cliente => `
        <tr>
            <td>${cliente.idCliente}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.email || '-'}</td>
            <td>${cliente.telefone || '-'}</td>
            <td>${formatarData(cliente.dataCadastro)}</td>
            <td>
                <button class="btn-secondary" onclick="editarCliente(${cliente.idCliente})">Editar</button>
                <button class="btn-danger" onclick="deletarCliente(${cliente.idCliente})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function abrirModalCliente(cliente = null) {
    const titulo = cliente ? 'Editar Cliente' : 'Novo Cliente';
    const form = `
        <div class="form-group">
            <label for="cliente-nome">Nome *</label>
            <input type="text" id="cliente-nome" value="${cliente ? cliente.nome : ''}" required>
        </div>
        <div class="form-group">
            <label for="cliente-email">Email</label>
            <input type="email" id="cliente-email" value="${cliente ? cliente.email : ''}">
        </div>
        <div class="form-group">
            <label for="cliente-telefone">Telefone</label>
            <input type="tel" id="cliente-telefone" value="${cliente ? cliente.telefone : ''}">
        </div>
        <div class="form-actions">
            <button type="button" class="btn-secondary" onclick="fecharModal()">Cancelar</button>
            <button type="button" class="btn-primary" onclick="salvarCliente(${cliente ? cliente.idCliente : null})">Salvar</button>
        </div>
    `;

    abrirModal(titulo, form);
}

async function salvarCliente(id = null) {
    const nome = document.getElementById('cliente-nome').value.trim();
    const email = document.getElementById('cliente-email').value.trim();
    const telefone = document.getElementById('cliente-telefone').value.trim();

    if (!nome) {
        mostrarErro('Nome é obrigatório');
        return;
    }

    const dados = { 
        nome, 
        email: email || null, 
        telefone: telefone || null 
    };

    const endpoint = id ? `/clientes/${id}` : '/clientes';
    const method = id ? 'PUT' : 'POST';

    const resultado = await apiRequest(endpoint, { method, body: dados });

    if (resultado) {
        mostrarSucesso(id ? 'Cliente atualizado com sucesso!' : 'Cliente criado com sucesso!');
        fecharModal();
        carregarClientes();
        if (currentSection === 'dashboard') carregarDashboard();
    }
}

async function editarCliente(id) {
    const cliente = await apiRequest(`/clientes/${id}`);
    if (cliente) {
        abrirModalCliente(cliente);
    }
}

async function deletarCliente(id) {
    if (confirm('Tem certeza que deseja excluir este cliente?')) {
        const sucesso = await apiRequest(`/clientes/${id}`, { method: 'DELETE' });
        if (sucesso !== null) {
            mostrarSucesso('Cliente excluído com sucesso!');
            carregarClientes();
            if (currentSection === 'dashboard') carregarDashboard();
        }
    }
}

async function carregarProdutos() {
    const produtos = await apiRequest('/produtos');
    const tbody = document.getElementById('produtos-body');
    
    if (!produtos) {
        tbody.innerHTML = '<tr><td colspan="4" class="loading">Erro ao carregar produtos</td></tr>';
        return;
    }

    tbody.innerHTML = produtos.map(produto => `
        <tr>
            <td>${produto.idProduto}</td>
            <td>${produto.nomeProduto || produto.nome}</td>
            <td>R$ ${(produto.precoKg || produto.preco || 0).toFixed(2)}</td>
            <td>
                <button class="btn-secondary" onclick="editarProduto(${produto.idProduto})">Editar</button>
                <button class="btn-danger" onclick="deletarProduto(${produto.idProduto})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function abrirModalProduto(produto = null) {
    const titulo = produto ? 'Editar Produto' : 'Novo Produto';
    const preco = produto ? (produto.precoKg || produto.preco) : '';
    
    const form = `
        <div class="form-group">
            <label for="produto-nome">Nome do Produto *</label>
            <input type="text" id="produto-nome" value="${produto ? (produto.nomeProduto || produto.nome) : ''}" required>
        </div>
        <div class="form-group">
            <label for="produto-preco">Preço por KG *</label>
            <input type="number" id="produto-preco" step="0.01" min="0" value="${preco}" required>
        </div>
        <div class="form-actions">
            <button type="button" class="btn-secondary" onclick="fecharModal()">Cancelar</button>
            <button type="button" class="btn-primary" onclick="salvarProduto(${produto ? produto.idProduto : null})">Salvar</button>
        </div>
    `;

    abrirModal(titulo, form);
}

async function salvarProduto(id = null) {
    const nome = document.getElementById('produto-nome').value.trim();
    const precoKg = parseFloat(document.getElementById('produto-preco').value);

    if (!nome || isNaN(precoKg) || precoKg < 0) {
        mostrarErro('Nome e preço válido são obrigatórios');
        return;
    }

    const dados = { 
        nomeProduto: nome, 
        precoKg: precoKg 
    };

    const endpoint = id ? `/produtos/${id}` : '/produtos';
    const method = id ? 'PUT' : 'POST';

    const resultado = await apiRequest(endpoint, { method, body: dados });

    if (resultado) {
        mostrarSucesso(id ? 'Produto atualizado com sucesso!' : 'Produto criado com sucesso!');
        fecharModal();
        carregarProdutos();
        if (currentSection === 'dashboard') carregarDashboard();
    }
}

async function editarProduto(id) {
    const produto = await apiRequest(`/produtos/${id}`);
    if (produto) {
        abrirModalProduto(produto);
    }
}

async function deletarProduto(id) {
    if (confirm('Tem certeza que deseja excluir este produto?')) {
        const sucesso = await apiRequest(`/produtos/${id}`, { method: 'DELETE' });
        if (sucesso !== null) {
            mostrarSucesso('Produto excluído com sucesso!');
            carregarProdutos();
            if (currentSection === 'dashboard') carregarDashboard();
        }
    }
}

async function carregarEstoque() {
    const estoque = await apiRequest('/estoque-ingredientes');
    const tbody = document.getElementById('estoque-body');
    
    if (!estoque) {
        tbody.innerHTML = '<tr><td colspan="8" class="loading">Erro ao carregar estoque</td></tr>';
        return;
    }

    tbody.innerHTML = estoque.map(item => `
        <tr>
            <td>${item.idIngrediente}</td>
            <td>${item.nomeIngrediente || item.nome}</td>
            <td>${item.quantidadeEstoque}</td>
            <td>${item.unidadeMedida}</td>
            <td>${item.estoqueMinimo}</td>
            <td>R$ ${(item.custoMedio || 0).toFixed(2)}</td>
            <td>
                <span class="status-badge ${item.precisaRepor ? 'status-inactive' : 'status-active'}">
                    ${item.precisaRepor ? 'Reposição' : 'OK'}
                </span>
            </td>
            <td>
                <button class="btn-secondary" onclick="editarIngrediente(${item.idIngrediente})">Editar</button>
                <button class="btn-danger" onclick="deletarIngrediente(${item.idIngrediente})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function abrirModalIngrediente(ingrediente = null) {
    const titulo = ingrediente ? 'Editar Ingrediente' : 'Novo Ingrediente';
    const form = `
        <div class="form-group">
            <label for="ingrediente-nome">Nome do Ingrediente *</label>
            <input type="text" id="ingrediente-nome" value="${ingrediente ? (ingrediente.nomeIngrediente || ingrediente.nome) : ''}" required>
        </div>
        <div class="form-group">
            <label for="ingrediente-quantidade">Quantidade em Estoque *</label>
            <input type="number" id="ingrediente-quantidade" step="0.01" min="0" value="${ingrediente ? ingrediente.quantidadeEstoque : ''}" required>
        </div>
        <div class="form-group">
            <label for="ingrediente-unidade">Unidade de Medida *</label>
            <select id="ingrediente-unidade" required>
                <option value="">Selecione a unidade</option>
                <option value="kg" ${ingrediente?.unidadeMedida === 'kg' ? 'selected' : ''}>kg</option>
                <option value="g" ${ingrediente?.unidadeMedida === 'g' ? 'selected' : ''}>g</option>
                <option value="L" ${ingrediente?.unidadeMedida === 'L' ? 'selected' : ''}>L</option>
                <option value="ml" ${ingrediente?.unidadeMedida === 'ml' ? 'selected' : ''}>ml</option>
                <option value="un" ${ingrediente?.unidadeMedida === 'un' ? 'selected' : ''}>unidade</option>
            </select>
        </div>
        <div class="form-group">
            <label for="ingrediente-minimo">Estoque Mínimo *</label>
            <input type="number" id="ingrediente-minimo" step="0.01" min="0" value="${ingrediente ? ingrediente.estoqueMinimo : ''}" required>
        </div>
        <div class="form-group">
            <label for="ingrediente-custo">Custo Médio *</label>
            <input type="number" id="ingrediente-custo" step="0.01" min="0" value="${ingrediente ? ingrediente.custoMedio : ''}" required>
        </div>
        <div class="form-actions">
            <button type="button" class="btn-secondary" onclick="fecharModal()">Cancelar</button>
            <button type="button" class="btn-primary" onclick="salvarIngrediente(${ingrediente ? ingrediente.idIngrediente : null})">Salvar</button>
        </div>
    `;

    abrirModal(titulo, form);
}

async function salvarIngrediente(id = null) {
    const nome = document.getElementById('ingrediente-nome').value.trim();
    const quantidade = parseFloat(document.getElementById('ingrediente-quantidade').value);
    const unidade = document.getElementById('ingrediente-unidade').value;
    const estoqueMinimo = parseFloat(document.getElementById('ingrediente-minimo').value);
    const custoMedio = parseFloat(document.getElementById('ingrediente-custo').value);

    if (!nome || isNaN(quantidade) || quantidade < 0 || !unidade || 
        isNaN(estoqueMinimo) || estoqueMinimo < 0 || isNaN(custoMedio) || custoMedio < 0) {
        mostrarErro('Todos os campos são obrigatórios e devem ser valores válidos');
        return;
    }

    const dados = {
        nomeIngrediente: nome,
        quantidadeEstoque: quantidade,
        unidadeMedida: unidade,
        estoqueMinimo: estoqueMinimo,
        custoMedio: custoMedio
    };

    const endpoint = id ? `/estoque-ingredientes/${id}` : '/estoque-ingredientes';
    const method = id ? 'PUT' : 'POST';

    const resultado = await apiRequest(endpoint, { method, body: dados });

    if (resultado) {
        mostrarSucesso(id ? 'Ingrediente atualizado com sucesso!' : 'Ingrediente criado com sucesso!');
        fecharModal();
        carregarEstoque();
        carregarDashboard();
    }
}

async function editarIngrediente(id) {
    const ingrediente = await apiRequest(`/estoque-ingredientes/${id}`);
    if (ingrediente) {
        abrirModalIngrediente(ingrediente);
    }
}

async function deletarIngrediente(id) {
    if (confirm('Tem certeza que deseja excluir este ingrediente?')) {
        const sucesso = await apiRequest(`/estoque-ingredientes/${id}`, { method: 'DELETE' });
        if (sucesso !== null) {
            mostrarSucesso('Ingrediente excluído com sucesso!');
            carregarEstoque();
            carregarDashboard();
        }
    }
}

async function carregarVendas() {
    const vendas = await apiRequest('/vendas');
    const tbody = document.getElementById('vendas-body');
    
    if (!vendas) {
        tbody.innerHTML = '<tr><td colspan="10" class="loading">Erro ao carregar vendas</td></tr>';
        return;
    }

    tbody.innerHTML = vendas.map(venda => `
        <tr>
            <td>${venda.idVenda}</td>
            <td>${venda.nomeCliente || 'Cliente não informado'}</td>
            <td>${venda.nomeProduto || 'Produto não informado'}</td>
            <td>${venda.pesoVendido} kg</td>
            <td>R$ ${venda.precoKg?.toFixed(2) || '0.00'}</td>
            <td>R$ ${venda.total?.toFixed(2) || '0.00'}</td>
            <td>${venda.formaPagamento}</td>
            <td>
                <span class="status-badge ${venda.statusPagamento === 'pago' ? 'status-active' : 'status-pendente'}">
                    ${venda.statusPagamento}
                </span>
            </td>
            <td>${formatarData(venda.dataVenda)}</td>
            <td>
                <button class="btn-secondary" onclick="editarVenda(${venda.idVenda})">Editar</button>
                <button class="btn-danger" onclick="cancelarVenda(${venda.idVenda})">Cancelar</button>
            </td>
        </tr>
    `).join('');
}

function abrirModalVenda(venda = null) {
    const titulo = venda ? 'Editar Venda' : 'Nova Venda';
    const form = `
        <div class="form-group">
            <label for="venda-cliente">Cliente *</label>
            <select id="venda-cliente" required>
                <option value="">Selecione um cliente</option>
            </select>
        </div>
        <div class="form-group">
            <label for="venda-produto">Produto *</label>
            <select id="venda-produto" required>
                <option value="">Selecione um produto</option>
            </select>
        </div>
        <div class="form-group">
            <label for="venda-peso">Peso Vendido (kg) *</label>
            <input type="number" id="venda-peso" step="0.01" min="0.01" value="${venda ? venda.pesoVendido : ''}" required>
        </div>
        <div class="form-group">
            <label for="venda-preco-kg">Preço por KG *</label>
            <input type="number" id="venda-preco-kg" step="0.01" min="0" value="${venda ? venda.precoKg : ''}" required>
        </div>
        <div class="form-group">
            <label for="venda-forma-pagamento">Forma de Pagamento *</label>
            <select id="venda-forma-pagamento" required>
                <option value="">Selecione</option>
                <option value="dinheiro" ${venda?.formaPagamento === 'dinheiro' ? 'selected' : ''}>Dinheiro</option>
                <option value="cartao" ${venda?.formaPagamento === 'cartao' ? 'selected' : ''}>Cartão</option>
                <option value="pix" ${venda?.formaPagamento === 'pix' ? 'selected' : ''}>PIX</option>
                <option value="fiado" ${venda?.formaPagamento === 'fiado' ? 'selected' : ''}>Fiado</option>
            </select>
        </div>
        <div class="form-group">
            <label for="venda-status-pagamento">Status Pagamento *</label>
            <select id="venda-status-pagamento" required>
                <option value="pendente" ${venda?.statusPagamento === 'pendente' ? 'selected' : ''}>Pendente</option>
                <option value="pago" ${venda?.statusPagamento === 'pago' ? 'selected' : ''}>Pago</option>
            </select>
        </div>
        <div class="form-actions">
            <button type="button" class="btn-secondary" onclick="fecharModal()">Cancelar</button>
            <button type="button" class="btn-primary" onclick="salvarVenda(${venda ? venda.idVenda : null})">Salvar</button>
        </div>
    `;

    abrirModal(titulo, form);
    carregarClientesParaVenda();
    carregarProdutosParaVenda();
    
    if (venda) {
        setTimeout(() => {
            if (venda.idCliente) document.getElementById('venda-cliente').value = venda.idCliente;
            if (venda.idProduto) document.getElementById('venda-produto').value = venda.idProduto;
        }, 100);
    }
}

async function carregarClientesParaVenda() {
    const clientes = await apiRequest('/clientes');
    const select = document.getElementById('venda-cliente');
    if (clientes) {
        select.innerHTML = '<option value="">Selecione um cliente</option>' + 
            clientes.map(cliente => `
                <option value="${cliente.idCliente}">${cliente.nome}</option>
            `).join('');
    }
}

async function carregarProdutosParaVenda() {
    const produtos = await apiRequest('/produtos');
    const select = document.getElementById('venda-produto');
    if (produtos) {
        select.innerHTML = '<option value="">Selecione um produto</option>' + 
            produtos.map(produto => `
                <option value="${produto.idProduto}" data-preco="${produto.precoKg || produto.preco}">
                    ${produto.nomeProduto || produto.nome} - R$ ${(produto.precoKg || produto.preco).toFixed(2)}/kg
                </option>
            `).join('');
        
        select.addEventListener('change', function() {
            const produtoSelecionado = this.options[this.selectedIndex];
            if (produtoSelecionado && produtoSelecionado.dataset.preco) {
                document.getElementById('venda-preco-kg').value = produtoSelecionado.dataset.preco;
            }
        });
    }
}

async function salvarVenda(id = null) {
    const idCliente = document.getElementById('venda-cliente').value;
    const idProduto = document.getElementById('venda-produto').value;
    const pesoVendido = parseFloat(document.getElementById('venda-peso').value);
    const precoKg = parseFloat(document.getElementById('venda-preco-kg').value);
    const formaPagamento = document.getElementById('venda-forma-pagamento').value;
    const statusPagamento = document.getElementById('venda-status-pagamento').value;

    if (!idCliente || !idProduto || isNaN(pesoVendido) || pesoVendido <= 0 || 
        isNaN(precoKg) || precoKg < 0 || !formaPagamento || !statusPagamento) {
        mostrarErro('Todos os campos obrigatórios devem ser preenchidos com valores válidos');
        return;
    }

    const dados = {
        idCliente: parseInt(idCliente),
        idProduto: parseInt(idProduto),
        pesoVendido: pesoVendido,
        precoKg: precoKg,
        formaPagamento: formaPagamento,
        statusPagamento: statusPagamento
    };

    const endpoint = id ? `/vendas/${id}` : '/vendas';
    const method = id ? 'PUT' : 'POST';

    const resultado = await apiRequest(endpoint, { method, body: dados });

    if (resultado) {
        mostrarSucesso(id ? 'Venda atualizada com sucesso!' : 'Venda criada com sucesso!');
        fecharModal();
        carregarVendas();
        carregarDashboard();
    }
}

async function editarVenda(id) {
    const venda = await apiRequest(`/vendas/${id}`);
    if (venda) {
        abrirModalVenda(venda);
    }
}

async function cancelarVenda(id) {
    if (confirm('Tem certeza que deseja cancelar esta venda?')) {
        const sucesso = await apiRequest(`/vendas/${id}`, { method: 'DELETE' });
        if (sucesso !== null) {
            mostrarSucesso('Venda cancelada com sucesso!');
            carregarVendas();
            carregarDashboard();
        }
    }
}

async function carregarFuncionarios() {
    const funcionarios = await apiRequest('/funcionarios');
    const tbody = document.getElementById('funcionarios-body');
    
    if (!funcionarios) {
        tbody.innerHTML = '<tr><td colspan="7" class="loading">Erro ao carregar funcionários</td></tr>';
        return;
    }

    tbody.innerHTML = funcionarios.map(funcionario => `
        <tr>
            <td>${funcionario.idFuncionario}</td>
            <td>${funcionario.nome}</td>
            <td>${funcionario.email || '-'}</td>
            <td>${funcionario.cargo}</td>
            <td>R$ ${(funcionario.salarioBase || funcionario.salario || 0).toFixed(2)}</td>
            <td>
                <span class="status-badge ${funcionario.ativo ? 'status-active' : 'status-inactive'}">
                    ${funcionario.ativo ? 'Ativo' : 'Inativo'}
                </span>
            </td>
            <td>
                <button class="btn-secondary" onclick="editarFuncionario(${funcionario.idFuncionario})">Editar</button>
                <button class="btn-danger" onclick="desativarFuncionario(${funcionario.idFuncionario})">${funcionario.ativo ? 'Desativar' : 'Ativar'}</button>
            </td>
        </tr>
    `).join('');
}

function abrirModalFuncionario(funcionario = null) {
    const titulo = funcionario ? 'Editar Funcionário' : 'Novo Funcionário';
    const salarioBase = funcionario ? (funcionario.salarioBase || funcionario.salario) : '';
    
    const form = `
        <div class="form-group">
            <label for="funcionario-nome">Nome *</label>
            <input type="text" id="funcionario-nome" value="${funcionario ? funcionario.nome : ''}" required>
        </div>
        <div class="form-group">
            <label for="funcionario-email">Email</label>
            <input type="email" id="funcionario-email" value="${funcionario ? funcionario.email : ''}">
        </div>
        <div class="form-group">
            <label for="funcionario-cargo">Cargo *</label>
            <select id="funcionario-cargo" required>
                <option value="">Selecione o cargo</option>
                <option value="padeiro" ${funcionario?.cargo === 'padeiro' ? 'selected' : ''}>Padeiro</option>
                <option value="atendente" ${funcionario?.cargo === 'atendente' ? 'selected' : ''}>Atendente</option>
                <option value="gerente" ${funcionario?.cargo === 'gerente' ? 'selected' : ''}>Gerente</option>
                <option value="auxiliar" ${funcionario?.cargo === 'auxiliar' ? 'selected' : ''}>Auxiliar</option>
            </select>
        </div>
        <div class="form-group">
            <label for="funcionario-salario-base">Salário Base *</label>
            <input type="number" id="funcionario-salario-base" step="0.01" min="0" value="${salarioBase}" required>
        </div>
        <div class="form-group">
            <label for="funcionario-data-admissao">Data de Admissão *</label>
            <input type="date" id="funcionario-data-admissao" value="${funcionario ? funcionario.dataAdmissao?.split('T')[0] : ''}" required>
        </div>
        <div class="form-group">
            <label for="funcionario-ativo">Status</label>
            <select id="funcionario-ativo">
                <option value="true" ${funcionario?.ativo !== false ? 'selected' : ''}>Ativo</option>
                <option value="false" ${funcionario?.ativo === false ? 'selected' : ''}>Inativo</option>
            </select>
        </div>
        <div class="form-actions">
            <button type="button" class="btn-secondary" onclick="fecharModal()">Cancelar</button>
            <button type="button" class="btn-primary" onclick="salvarFuncionario(${funcionario ? funcionario.idFuncionario : null})">Salvar</button>
        </div>
    `;

    abrirModal(titulo, form);
}

async function salvarFuncionario(id = null) {
    const nome = document.getElementById('funcionario-nome').value.trim();
    const email = document.getElementById('funcionario-email').value.trim();
    const cargo = document.getElementById('funcionario-cargo').value;
    const salarioBase = parseFloat(document.getElementById('funcionario-salario-base').value);
    const dataAdmissao = document.getElementById('funcionario-data-admissao').value;
    const ativo = document.getElementById('funcionario-ativo').value === 'true';

    if (!nome || !cargo || isNaN(salarioBase) || salarioBase < 0 || !dataAdmissao) {
        mostrarErro('Todos os campos obrigatórios devem ser preenchidos com valores válidos');
        return;
    }

    const dados = {
        nome,
        email: email || null,
        cargo,
        salarioBase: salarioBase,
        dataAdmissao: dataAdmissao,
        ativo
    };

    const endpoint = id ? `/funcionarios/${id}` : '/funcionarios';
    const method = id ? 'PUT' : 'POST';

    const resultado = await apiRequest(endpoint, { method, body: dados });

    if (resultado) {
        mostrarSucesso(id ? 'Funcionário atualizado com sucesso!' : 'Funcionário criado com sucesso!');
        fecharModal();
        carregarFuncionarios();
    }
}

async function editarFuncionario(id) {
    const funcionario = await apiRequest(`/funcionarios/${id}`);
    if (funcionario) {
        abrirModalFuncionario(funcionario);
    }
}

async function desativarFuncionario(id) {
    const funcionario = await apiRequest(`/funcionarios/${id}`);
    if (!funcionario) return;

    const acao = funcionario.ativo ? 'desativar' : 'ativar';
    if (confirm(`Tem certeza que deseja ${acao} este funcionário?`)) {
        const dados = { ...funcionario, ativo: !funcionario.ativo };
        const sucesso = await apiRequest(`/funcionarios/${id}`, {
            method: 'PUT',
            body: dados
        });
        
        if (sucesso !== null) {
            mostrarSucesso(`Funcionário ${acao === 'desativar' ? 'desativado' : 'ativado'} com sucesso!`);
            carregarFuncionarios();
        }
    }
}

async function carregarCFTV() {
    const cftvGrid = document.getElementById('cftv-grid');
    const lastUpdateElement = document.getElementById('last-update');
    
    try {
        const cameras = [
            { id: 1, nome: 'Câmera 1 - Entrada', imagem: 'resources/imagens-camera/imagem_1.png' },
            { id: 2, nome: 'Câmera 2 - Balcão', imagem: 'resources/imagens-camera/imagem_2.png' },
            { id: 3, nome: 'Câmera 3 - Forno', imagem: 'resources/imagens-camera/imagem_3.png' },
            { id: 4, nome: 'Câmera 4 - Estoque', imagem: 'resources/imagens-camera/imagem_4.png' }
        ];
        
        const camerasHTML = cameras.map(camera => `
            <div class="camera-card">
                <div class="camera-header">
                    <h3 class="camera-title">${camera.nome}</h3>
                    <span class="camera-status online"></span>
                </div>
                <div class="camera-feed">
                    <img src="${camera.imagem}" alt="${camera.nome}" class="camera-image" 
                         onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIwIiBoZWlnaHQ9IjI0MCIgdmlld0JveD0iMCAwIDMyMCAyNDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMjAiIGhlaWdodD0iMjQwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik0xNjAgMTIwQzE0Mi4zMjYgMTIwIDEyOCAxMDUuNjc0IDEyOCA4OC4wMDAxQzEyOCA3MC4zMjYxIDE0Mi4zMjYgNTYuMDAwMSAxNjAgNTYuMDAwMUMxNzcuNjc0IDU2LjAwMDEgMTkyIDcwLjMyNjEgMTkyIDg4LjAwMDFDMTkyIDEwNS42NzQgMTc3LjY3NCAxMjAgMTYwIDEyMFpNMTYwIDY0LjAwMDFDMTQ3LjgyOCA2NC4wMDAxIDEzOCA3My44MjgxIDEzOCA4Ni4wMDAxQzEzOCA5OC4xNzIxIDE0Ny44MjggMTA4LjAwMSAxNjAgMTA4LjAwMUMxNzIuMTcyIDEwOC4wMDEgMTgyIDk4LjE3MjEgMTgyIDg2LjAwMDFDMTgyIDczLjgyODEgMTcyLjE3MiA2NC4wMDAxIDE2MCA2NC4wMDAxWk0yMjQgMTg0SDEwNFYxNjBIMjI0VjE4NFpNOTYgMTc2VjE2OEgxMDRWMTc2SDk2Wk0yMzIgMTc2SDIyNFYxNjhIMjMyVjE3NlpNMTA0IDE0NEgyMjRWMTI4SDEwNFYxNDRaTTk2IDEzNlYxMjhIMTA0VjEzNkg5NlpNMjMyIDEzNkgyMjRWMTI4SDIzMlYxMzZaTTE2MCAwQzE3Ni44NDggMCAxOTIgMTUuMTUyIDE5MiAzMkMxOTIgNDguODQ4IDE3Ni44NDggNjQgMTYwIDY0QzE0My4xNTIgNjQgMTI4IDQ4Ljg0OCAxMjggMzJDMTI4IDE1LjE1MiAxNDMuMTUyIDAgMTYwIDBaIiBmaWxsPSIjQ0RDRENEIi8+Cjwvc3ZnPgo='">
                </div>
                <div class="camera-info">
                    <span class="camera-time">${new Date().toLocaleTimeString('pt-BR')}</span>
                </div>
            </div>
        `).join('');
        
        cftvGrid.innerHTML = camerasHTML;
        lastUpdateElement.textContent = new Date().toLocaleString('pt-BR');
        
    } catch (error) {
        cftvGrid.innerHTML = '<div class="error">Erro ao carregar as câmeras</div>';
    }
}

function atualizarCameras() {
    carregarCFTV();
    mostrarSucesso('Câmeras atualizadas com sucesso!');
}

function ampliarCamera(cameraId) {
    const cameras = [
        { id: 1, nome: 'Câmera 1 - Entrada', imagem: 'resources/imagens-camera/imagem_1.png' },
        { id: 2, nome: 'Câmera 2 - Balcão', imagem: 'resources/imagens-camera/imagem_2.png' },
        { id: 3, nome: 'Câmera 3 - Forno', imagem: 'resources/imagens-camera/imagem_3.png' },
        { id: 4, nome: 'Câmera 4 - Estoque', imagem: 'resources/imagens-camera/imagem_4.png' }
    ];
    
    const camera = cameras.find(c => c.id === cameraId);
    if (!camera) return;
    
    const modalContent = `
        <div class="camera-modal-content">
            <div class="camera-modal-header">
                <h3>${camera.nome}</h3>
                <span class="camera-status online"></span>
            </div>
            <div class="camera-modal-body">
                <img src="${camera.imagem}" alt="${camera.nome}" class="camera-modal-image"
                     onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjQwIiBoZWlnaHQ9IjQ4MCIgdmlld0JveD0iMCAwIDY0MCA0ODAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSI2NDAiIGhlaWdodD0iNDgwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik0zMjAgMjQwQzI4NC42NTIgMjQwIDI1NiAyMTAuMzQ4IDI1NiAxNzYuMDAxQzI1NiAxNDEuNjUyIDI4NC42NTIgMTEyLjAwMSAzMjAgMTEyLjAwMUMzNTUuMzQ4IDExMi4wMDEgMzg0IDE0MS42NTIgMzg0IDE3Ni4wMDFDMzg0IDIxMC4zNDggMzU1LjM0OCAyNDAgMzIwIDI0MFpNMzIwIDEyOC4wMDFDMjk1LjY1NiAxMjguMDAxIDI3NiAxNDcuNjU3IDI3NiAxNzIuMDAxQzI3NiAxOTYuMzQ1IDI5NS42NTYgMjE2LjAwMSAzMjAgMjE2LjAwMUMzNDQuMzQ0IDIxNi4wMDEgMzY0IDE5Ni4zNDUgMzY0IDE3Mi4wMDFDMzY0IDE0Ny42NTcgMzQ0LjM0NCAxMjguMDAxIDMyMCAxMjguMDAxWk00NDggMzY4SDIwOFYzMjBINDQ4VjM2OFpNMTkyIDM1MlYzMzZIMjA4VjM1MkgxOTJaTTQ2NCAzNTJINDQ4VjMzNkg0NjRWMzUyWk0yMDggMjg4SDQ0OFYyNTZIMjA4VjI4OFpNMTkyIDI3MlYyNTZIMjA4VjI3MkgxOTJaTTQ2NCAyNzJINDQ4VjI1Nkg0NjRWMjcyWk0zMjAgMEMzNTMuNjk2IDAgMzg0IDMwLjMwNCAzODQgNjRDMzg0IDk3LjY5NiAzNTMuNjk2IDEyOCAzMjAgMTI4QzI4Ni4zMDQgMTI4IDI1NiA5Ny42OTYgMjU2IDY0QzI1NiAzMC4zMDQgMjg2LjMwNCAwIDMyMCAwWiIgZmlsbD0iI0NEQ0RDRCIvPgo8L3N2Zz4K'">
            </div>
            <div class="camera-modal-footer">
                <span class="camera-time">${new Date().toLocaleString('pt-BR')}</span>
                <button class="btn-primary" onclick="fecharModal()">Fechar</button>
            </div>
        </div>
    `;
    
    abrirModal(camera.nome, modalContent);
}

function formatarData(dataString) {
    if (!dataString) return '-';
    try {
        return new Date(dataString).toLocaleDateString('pt-BR');
    } catch (error) {
        return dataString;
    }
}

function atualizarDataAtual() {
    const data = new Date();
    document.getElementById('current-date').textContent = 
        data.toLocaleDateString('pt-BR', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
}

function abrirModal(titulo, conteudo) {
    document.getElementById('modal-title').textContent = titulo;
    document.getElementById('modal-body').innerHTML = conteudo;
    document.getElementById('modal').style.display = 'block';
}

function fecharModal() {
    document.getElementById('modal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
        fecharModal();
    }
}

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        fecharModal();
    }
});