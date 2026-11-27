package br.edu.senai.gerenciadoros.dto;

import br.edu.senai.gerenciadoros.model.Cliente;

public record ClienteResponse(Long id, String nome, String telefone, String email) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getTelefone(), c.getEmail());
    }
}
