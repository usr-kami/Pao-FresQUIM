package com.paofresquim.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseService<T, ID, REQ, RES> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected abstract JpaRepository<T, ID> getRepository();
    protected abstract RES toResponseDTO(T entity);
    protected abstract T toEntity(REQ requestDTO);
    protected abstract void updateEntityFromRequest(T entity, REQ requestDTO);
    
    @Transactional(readOnly = true)
    public List<RES> listarTodos() {
        logger.debug("Listando todos os registros de {}", getEntityName());
        return getRepository().findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<RES> buscarPorId(ID id) {
        logger.debug("Buscando {} por ID: {}", getEntityName(), id);
        return getRepository().findById(id)
                .map(this::toResponseDTO);
    }
    
    @Transactional
    public RES criar(REQ requestDTO) {
        logger.info("Criando novo {}", getEntityName());
        T entity = toEntity(requestDTO);
        T savedEntity = getRepository().save(entity);
        logger.info("{} criado com ID: {}", getEntityName(), getIdFromEntity(savedEntity));
        return toResponseDTO(savedEntity);
    }
    
    @Transactional
    public Optional<RES> atualizar(ID id, REQ requestDTO) {
        logger.info("Atualizando {} com ID: {}", getEntityName(), id);
        return getRepository().findById(id)
                .map(entity -> {
                    updateEntityFromRequest(entity, requestDTO);
                    T updatedEntity = getRepository().save(entity);
                    logger.info("{} atualizado com ID: {}", getEntityName(), id);
                    return toResponseDTO(updatedEntity);
                });
    }
    
    @Transactional
    public boolean deletar(ID id) {
        logger.info("Deletando {} com ID: {}", getEntityName(), id);
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
            logger.info("{} deletado com ID: {}", getEntityName(), id);
            return true;
        }
        logger.warn("Tentativa de deletar {} não encontrado com ID: {}", getEntityName(), id);
        return false;
    }
    
    protected String getEntityName() {
        return getClass().getSimpleName().replace("Service", "");
    }
    
    protected abstract ID getIdFromEntity(T entity);
}