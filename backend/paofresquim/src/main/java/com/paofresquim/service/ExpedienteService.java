package com.paofresquim.service;

import com.paofresquim.dto.ExpedienteRequestDTO;
import com.paofresquim.dto.ExpedienteResponseDTO;
import com.paofresquim.entity.ExpedienteFuncionario;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.repository.ExpedienteRepository;
import com.paofresquim.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpedienteService {

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> listarTodos() {
        return expedienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ExpedienteResponseDTO> buscarPorId(Long id) {
        return expedienteRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorFuncionario(Long idFuncionario) {
        return expedienteRepository.findByFuncionarioIdFuncionario(idFuncionario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorDiaSemana(String diaSemana) {
        return expedienteRepository.findByDiaSemana(diaSemana)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponseDTO> buscarPorTurno(String turno) {
        return expedienteRepository.findByTurno(turno)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpedienteResponseDTO criarExpediente(ExpedienteRequestDTO expedienteRequest) {
        
        Funcionario funcionario = funcionarioRepository.findById(expedienteRequest.getIdFuncionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + expedienteRequest.getIdFuncionario()));

        validarDadosExpediente(expedienteRequest);

        List<ExpedienteFuncionario> expedientesExistentes = expedienteRepository
                .findByFuncionarioIdFuncionarioAndDiaSemana(expedienteRequest.getIdFuncionario(), expedienteRequest.getDiaSemana());
        
        if (!expedientesExistentes.isEmpty()) {
            throw new RuntimeException("Já existe expediente cadastrado para este funcionário no dia: " + expedienteRequest.getDiaSemana());
        }

        ExpedienteFuncionario expediente = new ExpedienteFuncionario();
        expediente.setFuncionario(funcionario);
        expediente.setDiaSemana(expedienteRequest.getDiaSemana());
        expediente.setHoraEntrada(expedienteRequest.getHoraEntrada());
        expediente.setHoraSaida(expedienteRequest.getHoraSaida());
        expediente.setTurno(expedienteRequest.getTurno());

        ExpedienteFuncionario expedienteSalvo = expedienteRepository.save(expediente);
        return toResponseDTO(expedienteSalvo);
    }

    @Transactional
    public Optional<ExpedienteResponseDTO> atualizarExpediente(Long id, ExpedienteRequestDTO expedienteRequest) {
        return expedienteRepository.findById(id)
                .map(expediente -> {
                    
                    Funcionario funcionario = funcionarioRepository.findById(expedienteRequest.getIdFuncionario())
                            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + expedienteRequest.getIdFuncionario()));

                    validarDadosExpediente(expedienteRequest);
                    
                    List<ExpedienteFuncionario> expedientesExistentes = expedienteRepository
                            .findByFuncionarioIdFuncionarioAndDiaSemana(expedienteRequest.getIdFuncionario(), expedienteRequest.getDiaSemana());
                    
                    expedientesExistentes = expedientesExistentes.stream()
                            .filter(e -> !e.getIdExpediente().equals(id))
                            .collect(Collectors.toList());
                    
                    if (!expedientesExistentes.isEmpty()) {
                        throw new RuntimeException("Já existe expediente cadastrado para este funcionário no dia: " + expedienteRequest.getDiaSemana());
                    }

                    expediente.setFuncionario(funcionario);
                    expediente.setDiaSemana(expedienteRequest.getDiaSemana());
                    expediente.setHoraEntrada(expedienteRequest.getHoraEntrada());
                    expediente.setHoraSaida(expedienteRequest.getHoraSaida());
                    expediente.setTurno(expedienteRequest.getTurno());

                    ExpedienteFuncionario expedienteAtualizado = expedienteRepository.save(expediente);
                    return toResponseDTO(expedienteAtualizado);
                });
    }

    @Transactional
    public boolean deletarExpediente(Long id) {
        if (expedienteRepository.existsById(id)) {
            expedienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarDadosExpediente(ExpedienteRequestDTO expedienteRequest) {
        
        if (!isDiaSemanaValido(expedienteRequest.getDiaSemana())) {
            throw new RuntimeException("Dia da semana inválido: " + expedienteRequest.getDiaSemana() + 
                    ". Dias válidos: segunda, terca, quarta, quinta, sexta, sabado, domingo");
        }

        if (!isTurnoValido(expedienteRequest.getTurno())) {
            throw new RuntimeException("Turno inválido: " + expedienteRequest.getTurno() + 
                    ". Turnos válidos: manha, tarde, noite, integral");
        }

        if (!expedienteRequest.getHoraEntrada().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new RuntimeException("Formato de hora de entrada inválido. Use formato HH:MM");
        }

        if (!expedienteRequest.getHoraSaida().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new RuntimeException("Formato de hora de saída inválido. Use formato HH:MM");
        }

        if (expedienteRequest.getHoraSaida().compareTo(expedienteRequest.getHoraEntrada()) <= 0) {
            throw new RuntimeException("Hora de saída deve ser após a hora de entrada");
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

    private ExpedienteResponseDTO toResponseDTO(ExpedienteFuncionario expediente) {
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
}