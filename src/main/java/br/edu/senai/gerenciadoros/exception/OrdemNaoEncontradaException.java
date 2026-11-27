package br.edu.senai.gerenciadoros.exception;

public class OrdemNaoEncontradaException extends RuntimeException {

    public OrdemNaoEncontradaException(Long id) {
        super("Ordem de serviço não encontrada: " + id);
    }
}
