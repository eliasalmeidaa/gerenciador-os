package br.edu.senai.gerenciadoros.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConcluirOrdemRequest(
        @NotNull @DecimalMin("0.01") BigDecimal valor
) {
}
