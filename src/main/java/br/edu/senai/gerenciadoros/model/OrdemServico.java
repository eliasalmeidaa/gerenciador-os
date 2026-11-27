package br.edu.senai.gerenciadoros.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String equipamento;

    @Column(nullable = false)
    private String defeito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdem status;

    @Column(precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataConclusao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;
}
