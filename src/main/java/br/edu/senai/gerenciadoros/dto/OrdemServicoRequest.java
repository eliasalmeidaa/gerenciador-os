package br.edu.senai.gerenciadoros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrdemServicoRequest(
        @NotBlank String equipamento,
        @NotBlank String defeito,
        @NotNull Long clienteId
) {
}
