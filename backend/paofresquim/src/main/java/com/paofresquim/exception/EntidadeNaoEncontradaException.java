package com.paofresquim.exception;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }
    
    public EntidadeNaoEncontradaException(String entidade, Long id) {
        super(String.format("%s não encontrado com ID: %d", entidade, id));
    }
}