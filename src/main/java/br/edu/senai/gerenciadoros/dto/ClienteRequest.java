package br.edu.senai.gerenciadoros.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 8, max = 20) String telefone,
        @NotBlank @Email String email
) {
}
