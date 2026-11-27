package br.edu.senai.gerenciadoros.dto;

import jakarta.validation.constraints.NotBlank;

public record TecnicoRequest(
        @NotBlank String nome,
        @NotBlank String especialidade
) {
}
