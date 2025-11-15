package com.paofresquim.service;

import com.paofresquim.dto.FeriasRequestDTO;
import com.paofresquim.dto.FeriasResponseDTO;
import com.paofresquim.entity.FeriasFuncionario;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.repository.FeriasRepository;
import com.paofresquim.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeriasService {

    @Autowired
    private FeriasRepository feriasRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Transactional(readOnly = true)
    public List<FeriasResponseDTO> listarTodas() {
        return feriasRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FeriasResponseDTO> buscarPorId(Long id) {
        return feriasRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<FeriasResponseDTO> buscarPorFuncionario(Long idFuncionario) {
        return feriasRepository.findByFuncionarioIdFuncionario(idFuncionario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeriasResponseDTO> buscarPorStatus(String status) {
        return feriasRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeriasResponseDTO solicitarFerias(FeriasRequestDTO feriasRequest) {
        
        Funcionario funcionario = funcionarioRepository.findById(feriasRequest.getIdFuncionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + feriasRequest.getIdFuncionario()));

        validarDatasFerias(feriasRequest.getDataInicio(), feriasRequest.getDataFim());

        verificarConflitoFerias(funcionario.getIdFuncionario(), feriasRequest.getDataInicio(), feriasRequest.getDataFim());

        FeriasFuncionario ferias = new FeriasFuncionario();
        ferias.setFuncionario(funcionario);
        ferias.setDataInicio(feriasRequest.getDataInicio());
        ferias.setDataFim(feriasRequest.getDataFim());
        ferias.setObservacoes(feriasRequest.getObservacoes());
        ferias.setStatus("solicitado");

        FeriasFuncionario feriasSalva = feriasRepository.save(ferias);
        return toResponseDTO(feriasSalva);
    }

    @Transactional
    public Optional<FeriasResponseDTO> atualizarStatus(Long id, String novoStatus) {
        return feriasRepository.findById(id)
                .map(ferias -> {
                    if (!isStatusValido(novoStatus)) {
                        throw new RuntimeException("Status inválido: " + novoStatus + 
                                ". Status válidos: solicitado, aprovado, em_andamento, concluido, cancelado");
                    }

                    if ("aprovado".equals(novoStatus) && !"solicitado".equals(ferias.getStatus())) {
                        throw new RuntimeException("Só é possível aprovar férias com status 'solicitado'");
                    }

                    if ("em_andamento".equals(novoStatus) && !"aprovado".equals(ferias.getStatus())) {
                        throw new RuntimeException("Só é possível iniciar férias com status 'aprovado'");
                    }

                    ferias.setStatus(novoStatus);
                    FeriasFuncionario feriasAtualizada = feriasRepository.save(ferias);
                    return toResponseDTO(feriasAtualizada);
                });
    }

    @Transactional
    public Optional<FeriasResponseDTO> atualizarFerias(Long id, FeriasRequestDTO feriasRequest) {
        return feriasRepository.findById(id)
                .map(ferias -> {
                    if (!"solicitado".equals(ferias.getStatus())) {
                        throw new RuntimeException("Só é possível atualizar férias com status 'solicitado'");
                    }

                    Funcionario funcionario = funcionarioRepository.findById(feriasRequest.getIdFuncionario())
                            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + feriasRequest.getIdFuncionario()));

                    validarDatasFerias(feriasRequest.getDataInicio(), feriasRequest.getDataFim());
                    
                    List<FeriasFuncionario> conflitos = feriasRepository
                            .findByFuncionarioIdFuncionarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                                    funcionario.getIdFuncionario(), feriasRequest.getDataFim(), feriasRequest.getDataInicio());
                    
                    conflitos = conflitos.stream()
                            .filter(f -> !f.getIdFerias().equals(id))
                            .collect(Collectors.toList());
                    
                    if (!conflitos.isEmpty()) {
                        throw new RuntimeException("Conflito de datas com outras férias já cadastradas");
                    }

                    ferias.setFuncionario(funcionario);
                    ferias.setDataInicio(feriasRequest.getDataInicio());
                    ferias.setDataFim(feriasRequest.getDataFim());
                    ferias.setObservacoes(feriasRequest.getObservacoes());

                    FeriasFuncionario feriasAtualizada = feriasRepository.save(ferias);
                    return toResponseDTO(feriasAtualizada);
                });
    }

    @Transactional
    public boolean deletarFerias(Long id) {
        if (feriasRepository.existsById(id)) {
            FeriasFuncionario ferias = feriasRepository.findById(id).orElseThrow();
            if (!"solicitado".equals(ferias.getStatus())) {
                throw new RuntimeException("Só é possível deletar férias com status 'solicitado'");
            }
            feriasRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarDatasFerias(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isBefore(LocalDate.now())) {
            throw new RuntimeException("Data de início não pode ser no passado");
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new RuntimeException("Data de fim não pode ser anterior à data de início");
        }

        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        if (dias < 5) {
            throw new RuntimeException("Período mínimo de férias é 5 dias");
        }

        if (dias > 30) {
            throw new RuntimeException("Período máximo de férias é 30 dias");
        }
    }

    private void verificarConflitoFerias(Long idFuncionario, LocalDate dataInicio, LocalDate dataFim) {
        List<FeriasFuncionario> conflitos = feriasRepository
                .findByFuncionarioIdFuncionarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        idFuncionario, dataFim, dataInicio);
        
        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Conflito de datas com outras férias já cadastradas");
        }
    }

    private boolean isStatusValido(String status) {
        return status != null && 
               (status.equals("solicitado") || status.equals("aprovado") || 
                status.equals("em_andamento") || status.equals("concluido") || 
                status.equals("cancelado"));
    }

    private FeriasResponseDTO toResponseDTO(FeriasFuncionario ferias) {
        return new FeriasResponseDTO(
            ferias.getIdFerias(),
            ferias.getFuncionario().getIdFuncionario(),
            ferias.getFuncionario().getNome(),
            ferias.getFuncionario().getCargo(),
            ferias.getDataInicio(),
            ferias.getDataFim(),
            ferias.getDiasSolicitados(),
            ferias.getStatus(),
            ferias.getDataSolicitacao(),
            ferias.getObservacoes()
        );
    }
}