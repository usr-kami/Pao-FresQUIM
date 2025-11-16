package com.paofresquim.service;

import com.paofresquim.dto.VendaRequestDTO;
import com.paofresquim.dto.VendaResponseDTO;
import com.paofresquim.entity.Cliente;
import com.paofresquim.entity.Produto;
import com.paofresquim.entity.Venda;
import com.paofresquim.exception.EntidadeNaoEncontradaException;
import com.paofresquim.exception.ValidacaoException;
import com.paofresquim.repository.ClienteRepository;
import com.paofresquim.repository.ProdutoRepository;
import com.paofresquim.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendaService extends BaseService<Venda, Long, VendaRequestDTO, VendaResponseDTO> {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected VendaRepository getRepository() {
        return vendaRepository;
    }

    @Override
    protected VendaResponseDTO toResponseDTO(Venda venda) {
        return new VendaResponseDTO(
            venda.getIdVenda(),
            venda.getCliente() != null ? venda.getCliente().getIdCliente() : null,
            venda.getCliente() != null ? venda.getCliente().getNome() : "Cliente não informado",
            venda.getProduto().getIdProduto(),
            venda.getProduto().getNomeProduto(),
            venda.getPesoVendido(),
            venda.getPrecoKg(),
            venda.getTotal(),
            venda.getFormaPagamento(),
            venda.getStatusPagamento(),
            venda.getDataVenda(),
            venda.getDataVencimento()
        );
    }

    @Override
    protected Venda toEntity(VendaRequestDTO requestDTO) {
        Produto produto = produtoRepository.findById(requestDTO.idProduto())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado: " + requestDTO.idProduto()));

        Cliente cliente = null;
        if (requestDTO.idCliente() != null) {
            cliente = clienteRepository.findById(requestDTO.idCliente())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado: " + requestDTO.idCliente()));
        }

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setCliente(cliente);
        venda.setPesoVendido(requestDTO.pesoVendido());
        
        if (requestDTO.precoKg() != null) {
            venda.setPrecoKg(requestDTO.precoKg());
        } else {
            venda.setPrecoKg(produto.getPrecoKg());
        }
        
        venda.setFormaPagamento(requestDTO.formaPagamento());
        venda.setStatusPagamento(requestDTO.statusPagamento());
        
        if ("fiado".equals(requestDTO.formaPagamento()) && "pendente".equals(requestDTO.statusPagamento())) {
            venda.setDataVencimento(LocalDateTime.now().plusDays(7));
        }

        venda.calcularTotal();
        return venda;
    }

    @Override
    protected void updateEntityFromRequest(Venda venda, VendaRequestDTO requestDTO) {
        Produto produto = produtoRepository.findById(requestDTO.idProduto())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado: " + requestDTO.idProduto()));

        Cliente cliente = null;
        if (requestDTO.idCliente() != null) {
            cliente = clienteRepository.findById(requestDTO.idCliente())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado: " + requestDTO.idCliente()));
        }

        venda.setProduto(produto);
        venda.setCliente(cliente);
        venda.setPesoVendido(requestDTO.pesoVendido());
        
        if (requestDTO.precoKg() != null) {
            venda.setPrecoKg(requestDTO.precoKg());
        } else {
            venda.setPrecoKg(produto.getPrecoKg());
        }
        
        venda.setFormaPagamento(requestDTO.formaPagamento());
        venda.setStatusPagamento(requestDTO.statusPagamento());
        
        if ("fiado".equals(requestDTO.formaPagamento()) && "pendente".equals(requestDTO.statusPagamento())) {
            venda.setDataVencimento(LocalDateTime.now().plusDays(7));
        } else if ("pago".equals(requestDTO.statusPagamento()) || !"fiado".equals(requestDTO.formaPagamento())) {
            venda.setDataVencimento(null);
        }
        
        venda.calcularTotal();
    }

    @Override
    protected Long getIdFromEntity(Venda entity) {
        return entity.getIdVenda();
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorCliente(Long idCliente) {
        logger.debug("Buscando vendas por cliente ID: {}", idCliente);
        return vendaRepository.findByClienteIdCliente(idCliente)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorProduto(Long idProduto) {
        logger.debug("Buscando vendas por produto ID: {}", idProduto);
        return vendaRepository.findByProdutoIdProduto(idProduto)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        logger.debug("Buscando vendas por período: {} - {}", inicio, fim);
        return vendaRepository.findByDataVendaBetween(inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorStatusPagamento(String statusPagamento) {
        logger.debug("Buscando vendas por status de pagamento: {}", statusPagamento);
        return vendaRepository.findByStatusPagamento(statusPagamento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento) {
        logger.debug("Buscando vendas por forma de pagamento: {}", formaPagamento);
        return vendaRepository.findByFormaPagamento(formaPagamento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<VendaResponseDTO> atualizarStatusPagamento(Long id, String novoStatus) {
        logger.info("Atualizando status de pagamento da venda ID: {} para {}", id, novoStatus);
        return vendaRepository.findById(id)
                .map(venda -> {
                    if (!"pago".equals(novoStatus) && !"pendente".equals(novoStatus)) {
                        throw new ValidacaoException("Status de pagamento inválido: " + novoStatus);
                    }
                    
                    venda.setStatusPagamento(novoStatus);
                    
                    if ("pago".equals(novoStatus) && "fiado".equals(venda.getFormaPagamento())) {
                        venda.setDataVencimento(null);
                    }
                    
                    Venda vendaAtualizada = vendaRepository.save(venda);
                    logger.info("Status de pagamento atualizado para venda ID: {}", id);
                    return toResponseDTO(vendaAtualizada);
                });
    }
}