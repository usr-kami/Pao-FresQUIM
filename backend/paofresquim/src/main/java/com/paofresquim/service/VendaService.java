package com.paofresquim.service;

import com.paofresquim.dto.VendaRequestDTO;
import com.paofresquim.dto.VendaResponseDTO;
import com.paofresquim.entity.Cliente;
import com.paofresquim.entity.Produto;
import com.paofresquim.entity.Venda;
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
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<VendaResponseDTO> buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorCliente(Long idCliente) {
        return vendaRepository.findByClienteIdCliente(idCliente)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorProduto(Long idProduto) {
        return vendaRepository.findByProdutoIdProduto(idProduto)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByDataVendaBetween(inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorStatusPagamento(String statusPagamento) {
        return vendaRepository.findByStatusPagamento(statusPagamento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento) {
        return vendaRepository.findByFormaPagamento(formaPagamento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VendaResponseDTO criarVenda(VendaRequestDTO vendaRequest) {
        
        Produto produto = produtoRepository.findById(vendaRequest.getIdProduto())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + vendaRequest.getIdProduto()));

        Cliente cliente = null;
        if (vendaRequest.getIdCliente() != null) {
            cliente = clienteRepository.findById(vendaRequest.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + vendaRequest.getIdCliente()));
        }

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setCliente(cliente);
        venda.setPesoVendido(vendaRequest.getPesoVendido());
        
        if (vendaRequest.getPrecoKg() != null) {
            venda.setPrecoKg(vendaRequest.getPrecoKg());
        } else {
            venda.setPrecoKg(produto.getPrecoKg());
        }
        
        venda.setFormaPagamento(vendaRequest.getFormaPagamento());
        venda.setStatusPagamento(vendaRequest.getStatusPagamento());
        
        if ("fiado".equals(vendaRequest.getFormaPagamento()) && "pendente".equals(vendaRequest.getStatusPagamento())) {
            venda.setDataVencimento(LocalDateTime.now().plusDays(7));
        }

        venda.calcularTotal();

        Venda vendaSalva = vendaRepository.save(venda);
        return toResponseDTO(vendaSalva);
    }

    @Transactional
    public Optional<VendaResponseDTO> atualizarStatusPagamento(Long id, String novoStatus) {
        return vendaRepository.findById(id)
                .map(venda -> {
                    if (!"pago".equals(novoStatus) && !"pendente".equals(novoStatus)) {
                        throw new RuntimeException("Status de pagamento inválido: " + novoStatus);
                    }
                    
                    venda.setStatusPagamento(novoStatus);
                    
                    if ("pago".equals(novoStatus) && "fiado".equals(venda.getFormaPagamento())) {
                        venda.setDataVencimento(null);
                    }
                    
                    Venda vendaAtualizada = vendaRepository.save(venda);
                    return toResponseDTO(vendaAtualizada);
                });
    }

    @Transactional
    public Optional<VendaResponseDTO> atualizarVenda(Long id, VendaRequestDTO vendaRequest) {
        return vendaRepository.findById(id)
                .map(venda -> {
                    
                    Produto produto = produtoRepository.findById(vendaRequest.getIdProduto())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + vendaRequest.getIdProduto()));

                    Cliente cliente = null;
                    if (vendaRequest.getIdCliente() != null) {
                        cliente = clienteRepository.findById(vendaRequest.getIdCliente())
                                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + vendaRequest.getIdCliente()));
                    }

                    venda.setProduto(produto);
                    venda.setCliente(cliente);
                    venda.setPesoVendido(vendaRequest.getPesoVendido());
                    
                    if (vendaRequest.getPrecoKg() != null) {
                        venda.setPrecoKg(vendaRequest.getPrecoKg());
                    } else {
                        venda.setPrecoKg(produto.getPrecoKg());
                    }
                    
                    venda.setFormaPagamento(vendaRequest.getFormaPagamento());
                    venda.setStatusPagamento(vendaRequest.getStatusPagamento());
                    
                    
                    venda.calcularTotal();

                    Venda vendaAtualizada = vendaRepository.save(venda);
                    return toResponseDTO(vendaAtualizada);
                });
    }

    @Transactional
    public boolean deletarVenda(Long id) {
        if (vendaRepository.existsById(id)) {
            vendaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private VendaResponseDTO toResponseDTO(Venda venda) {
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
}