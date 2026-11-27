package br.edu.senai.gerenciadoros.dto;

import br.edu.senai.gerenciadoros.model.OrdemServico;
import br.edu.senai.gerenciadoros.model.StatusOrdem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoResponse(
        Long id,
        String equipamento,
        String defeito,
        StatusOrdem status,
        BigDecimal valor,
        LocalDateTime dataAbertura,
        LocalDateTime dataConclusao,
        String clienteNome,
        String tecnicoNome
) {

    public static OrdemServicoResponse de(OrdemServico os) {
        return new OrdemServicoResponse(
                os.getId(),
                os.getEquipamento(),
                os.getDefeito(),
                os.getStatus(),
                os.getValor(),
                os.getDataAbertura(),
                os.getDataConclusao(),
                os.getCliente().getNome(),
                os.getTecnico() != null ? os.getTecnico().getNome() : null
        );
    }
}
