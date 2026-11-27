package br.edu.senai.gerenciadoros.repository;

import br.edu.senai.gerenciadoros.model.OrdemServico;
import br.edu.senai.gerenciadoros.model.StatusOrdem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByStatus(StatusOrdem status);

    boolean existsByClienteId(Long clienteId);

    boolean existsByTecnicoId(Long tecnicoId);
}
