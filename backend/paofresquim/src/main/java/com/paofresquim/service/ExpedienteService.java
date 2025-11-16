package com.paofresquim.service;

import com.paofresquim.dto.ExpedienteRequestDTO;
import com.paofresquim.dto.ExpedienteResponseDTO;
import com.paofresquim.entity.ExpedienteFuncionario;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.exception.EntidadeNaoEncontradaException;
import com.paofresquim.exception.ValidacaoException;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.repository.ExpedienteRepository;
import com.paofresquim.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpedienteService extends BaseService<ExpedienteFuncionario, Long, ExpedienteRequestDTO, ExpedienteResponseDTO> {

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    protected ExpedienteRepository getRepository() {
        return expedienteRepository;
    }

    @Override
    protected ExpedienteResponseDTO toResponseDTO(ExpedienteFuncionario expediente) {
        return new ExpedienteResponseDTO(
            expediente.getIdExpediente(),
            expediente.getFuncionario().getIdFuncionario(),
            expediente.getFuncionario().getNome(),
            expediente.getFuncionario().getCargo(),
            expediente.getDiaSemana(),
            expediente.getHoraEntrada(),
            expediente.getHoraSaida(),
            expediente.getTurno()
        );
    }

    @Override
    protected ExpedienteFuncionario toEntity(ExpedienteRequestDTO requestDTO) {
        Funcionario funcionario = funcionarioRepository.findById(requestDTO.idFuncionario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário", requestDTO.idFuncionario()));

        validarDadosExpediente(requestDTO);
        verificarConflitoExpediente(null, requestDTO.idFuncionario(), requestDTO.diaSemana());

        ExpedienteFuncionario expediente = new ExpedienteFuncionario();
        expediente.setFuncionario(funcionario);
        expediente.setDiaSemana(requestDTO.diaSemana());
        expediente.setHoraEntrada(requestDTO.horaEntrada());
        expediente.setHoraSaida(requestDTO.horaSaida());
        expediente.setTurno(requestDTO.turno());

        return expediente;
    }

    @Override
    protected void updateEntityFromRequest(ExpedienteFuncionario expediente, ExpedienteRequestDTO requestDTO) {
        Funcionario funcionario = funcionarioRepository.findById(requestDTO.idFuncionario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário", requestDTO.idFuncionario()));

        validarDadosExpediente(requestDTO);
        verificarConflitoExpediente(expediente.getIdExpediente(), requestDTO.idFuncionario(), requestDTO.diaSemana());

        expediente.setFuncionario(funcionario);
        expediente.setDiaSemana(requestDTO.diaSemana());
        expediente.setHoraEntrada(requestDTO.horaEntrada());
        expediente.setHoraSaida(requestDTO.horaSaida());
        expediente.setTurno(requestDTO.turno());
    }

    @Override
    protected Long getIdFromEntity(ExpedienteFuncionario entity) {
        return entity.getIdExpediente();
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorFuncionario(Long idFuncionario) {
        logger.debug("Buscando expedientes por funcionário ID: {}", idFuncionario);
        return expedienteRepository.findByFuncionarioIdFuncionario(idFuncionario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorDiaSemana(String diaSemana) {
        logger.debug("Buscando expedientes por dia da semana: {}", diaSemana);
        return expedienteRepository.findByDiaSemana(diaSemana)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorTurno(String turno) {
        logger.debug("Buscando expedientes por turno: {}", turno);
        return expedienteRepository.findByTurno(turno)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarDadosExpediente(ExpedienteRequestDTO expedienteRequest) {
        if (!isDiaSemanaValido(expedienteRequest.diaSemana())) {
            throw new ValidacaoException("Dia da semana inválido: " + expedienteRequest.diaSemana() + 
                    ". Dias válidos: segunda, terca, quarta, quinta, sexta, sabado, domingo");
        }

        if (!isTurnoValido(expedienteRequest.turno())) {
            throw new ValidacaoException("Turno inválido: " + expedienteRequest.turno() + 
                    ". Turnos válidos: manha, tarde, noite, integral");
        }

        if (!expedienteRequest.horaEntrada().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new ValidacaoException("Formato de hora de entrada inválido. Use formato HH:MM");
        }

        if (!expedienteRequest.horaSaida().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new ValidacaoException("Formato de hora de saída inválido. Use formato HH:MM");
        }

        if (expedienteRequest.horaSaida().compareTo(expedienteRequest.horaEntrada()) <= 0) {
            throw new ValidacaoException("Hora de saída deve ser após a hora de entrada");
        }
    }

    private void verificarConflitoExpediente(Long idExpediente, Long idFuncionario, String diaSemana) {
        List<ExpedienteFuncionario> expedientesExistentes = expedienteRepository
                .findByFuncionarioIdFuncionarioAndDiaSemana(idFuncionario, diaSemana);
        
        expedientesExistentes = expedientesExistentes.stream()
                .filter(e -> !e.getIdExpediente().equals(idExpediente))
                .collect(Collectors.toList());
        
        if (!expedientesExistentes.isEmpty()) {
            throw new ConflitoDadosException("Já existe expediente cadastrado para este funcionário no dia: " + diaSemana);
        }
    }

    private boolean isDiaSemanaValido(String diaSemana) {
        return diaSemana != null && 
               (diaSemana.equals("segunda") || diaSemana.equals("terca") || 
                diaSemana.equals("quarta") || diaSemana.equals("quinta") || 
                diaSemana.equals("sexta") || diaSemana.equals("sabado") || 
                diaSemana.equals("domingo"));
    }

    private boolean isTurnoValido(String turno) {
        return turno != null && 
               (turno.equals("manha") || turno.equals("tarde") || 
                turno.equals("noite") || turno.equals("integral"));
    }
}