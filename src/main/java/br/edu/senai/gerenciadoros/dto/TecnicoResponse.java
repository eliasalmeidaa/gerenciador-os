package br.edu.senai.gerenciadoros.dto;

import br.edu.senai.gerenciadoros.model.Tecnico;

public record TecnicoResponse(Long id, String nome, String especialidade) {

    public static TecnicoResponse de(Tecnico t) {
        return new TecnicoResponse(t.getId(), t.getNome(), t.getEspecialidade());
    }
}
