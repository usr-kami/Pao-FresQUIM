package com.paofresquim.config;

import com.paofresquim.dto.ErrorResponse;
import com.paofresquim.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleEntidadeNaoEncontrada(
            EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        logger.warn("Entidade não encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Recurso Não Encontrado",
            ex.getMessage(),
            request.getRequestURI(),
            "ENTIDADE_NAO_ENCONTRADA"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErrorResponse> handleValidacaoException(
            ValidacaoException ex, HttpServletRequest request) {
        logger.warn("Erro de validação: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Dados de Entrada Inválidos",
            ex.getMessage(),
            request.getRequestURI(),
            "VALIDACAO_FALHOU"
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConflitoDadosException.class)
    public ResponseEntity<ErrorResponse> handleConflitoDadosException(
            ConflitoDadosException ex, HttpServletRequest request) {
        logger.warn("Conflito de dados: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflito de Dados",
            ex.getMessage(),
            request.getRequestURI(),
            "CONFLITO_DADOS"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraNegocioException(
            RegraNegocioException ex, HttpServletRequest request) {
        logger.warn("Violação de regra de negócio: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Regra de Negócio Violada",
            ex.getMessage(),
            request.getRequestURI(),
            "REGRA_NEGOCIO_VIOLADA"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "Erro de validação nos campos: " + String.join(", ", errors.keySet());
        logger.warn("Validação falhou: {}", errorMessage);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validação Falhou",
            errorMessage,
            request.getRequestURI(),
            "VALIDACAO_CAMPOS"
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        logger.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno do Servidor",
            "Ocorreu um erro interno no servidor",
            request.getRequestURI(),
            "ERRO_INTERNO"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}