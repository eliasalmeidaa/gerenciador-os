package br.edu.senai.gerenciadoros.exception;

public class TecnicoNaoEncontradoException extends RuntimeException {

    public TecnicoNaoEncontradoException(Long id) {
        super("Técnico não encontrado: " + id);
    }
}
