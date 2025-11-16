package com.paofresquim.service;

import com.paofresquim.dto.FeriasRequestDTO;
import com.paofresquim.dto.FeriasResponseDTO;
import com.paofresquim.entity.FeriasFuncionario;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.exception.EntidadeNaoEncontradaException;
import com.paofresquim.exception.ValidacaoException;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.exception.RegraNegocioException;
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
public class FeriasService extends BaseService<FeriasFuncionario, Long, FeriasRequestDTO, FeriasResponseDTO> {

    @Autowired
    private FeriasRepository feriasRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    protected FeriasRepository getRepository() {
        return feriasRepository;
    }

    @Override
    protected FeriasResponseDTO toResponseDTO(FeriasFuncionario ferias) {
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

    @Override
    protected FeriasFuncionario toEntity(FeriasRequestDTO requestDTO) {
        Funcionario funcionario = funcionarioRepository.findById(requestDTO.idFuncionario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário", requestDTO.idFuncionario()));

        validarDatasFerias(requestDTO.dataInicio(), requestDTO.dataFim());
        verificarConflitoFerias(null, requestDTO.idFuncionario(), requestDTO.dataInicio(), requestDTO.dataFim());

        FeriasFuncionario ferias = new FeriasFuncionario();
        ferias.setFuncionario(funcionario);
        ferias.setDataInicio(requestDTO.dataInicio());
        ferias.setDataFim(requestDTO.dataFim());
        ferias.setObservacoes(requestDTO.observacoes());
        ferias.setStatus("solicitado");

        return ferias;
    }

    @Override
    protected void updateEntityFromRequest(FeriasFuncionario ferias, FeriasRequestDTO requestDTO) {
        if (!"solicitado".equals(ferias.getStatus())) {
            throw new RegraNegocioException("Só é possível atualizar férias com status 'solicitado'");
        }

        Funcionario funcionario = funcionarioRepository.findById(requestDTO.idFuncionario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário", requestDTO.idFuncionario()));

        validarDatasFerias(requestDTO.dataInicio(), requestDTO.dataFim());
        verificarConflitoFerias(ferias.getIdFerias(), requestDTO.idFuncionario(), requestDTO.dataInicio(), requestDTO.dataFim());

        ferias.setFuncionario(funcionario);
        ferias.setDataInicio(requestDTO.dataInicio());
        ferias.setDataFim(requestDTO.dataFim());
        ferias.setObservacoes(requestDTO.observacoes());
    }

    @Override
    protected Long getIdFromEntity(FeriasFuncionario entity) {
        return entity.getIdFerias();
    }

    @Transactional(readOnly = true)
    public List<FeriasResponseDTO> buscarPorFuncionario(Long idFuncionario) {
        logger.debug("Buscando férias por funcionário ID: {}", idFuncionario);
        return feriasRepository.findByFuncionarioIdFuncionario(idFuncionario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeriasResponseDTO> buscarPorStatus(String status) {
        logger.debug("Buscando férias por status: {}", status);
        return feriasRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<FeriasResponseDTO> atualizarStatus(Long id, String novoStatus) {
        logger.info("Atualizando status das férias ID: {} para {}", id, novoStatus);
        return feriasRepository.findById(id)
                .map(ferias -> {
                    if (!isStatusValido(novoStatus)) {
                        throw new ValidacaoException("Status inválido: " + novoStatus + 
                                ". Status válidos: solicitado, aprovado, em_andamento, concluido, cancelado");
                    }

                    if ("aprovado".equals(novoStatus) && !"solicitado".equals(ferias.getStatus())) {
                        throw new RegraNegocioException("Só é possível aprovar férias com status 'solicitado'");
                    }

                    if ("em_andamento".equals(novoStatus) && !"aprovado".equals(ferias.getStatus())) {
                        throw new RegraNegocioException("Só é possível iniciar férias com status 'aprovado'");
                    }

                    ferias.setStatus(novoStatus);
                    FeriasFuncionario feriasAtualizada = feriasRepository.save(ferias);
                    logger.info("Status das férias ID: {} atualizado para: {}", id, novoStatus);
                    return toResponseDTO(feriasAtualizada);
                });
    }

    @Override
    @Transactional
    public boolean deletar(Long id) {
        logger.info("Tentando deletar férias ID: {}", id);
        if (feriasRepository.existsById(id)) {
            FeriasFuncionario ferias = feriasRepository.findById(id).orElseThrow();
            if (!"solicitado".equals(ferias.getStatus())) {
                throw new RegraNegocioException("Só é possível deletar férias com status 'solicitado'");
            }
            feriasRepository.deleteById(id);
            logger.info("Férias deletadas com ID: {}", id);
            return true;
        }
        logger.warn("Férias não encontradas para deleção ID: {}", id);
        return false;
    }

    private void validarDatasFerias(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isBefore(LocalDate.now())) {
            throw new ValidacaoException("Data de início não pode ser no passado");
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new ValidacaoException("Data de fim não pode ser anterior à data de início");
        }

        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        if (dias < 5) {
            throw new ValidacaoException("Período mínimo de férias é 5 dias");
        }

        if (dias > 30) {
            throw new ValidacaoException("Período máximo de férias é 30 dias");
        }
    }

    private void verificarConflitoFerias(Long idFerias, Long idFuncionario, LocalDate dataInicio, LocalDate dataFim) {
        List<FeriasFuncionario> conflitos = feriasRepository
                .findByFuncionarioIdFuncionarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        idFuncionario, dataFim, dataInicio);
        
        conflitos = conflitos.stream()
                .filter(f -> !f.getIdFerias().equals(idFerias))
                .collect(Collectors.toList());
        
        if (!conflitos.isEmpty()) {
            throw new ConflitoDadosException("Conflito de datas com outras férias já cadastradas");
        }
    }

    private boolean isStatusValido(String status) {
        return status != null && 
               (status.equals("solicitado") || status.equals("aprovado") || 
                status.equals("em_andamento") || status.equals("concluido") || 
                status.equals("cancelado"));
    }
}