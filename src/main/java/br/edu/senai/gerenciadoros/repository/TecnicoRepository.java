package br.edu.senai.gerenciadoros.repository;

import br.edu.senai.gerenciadoros.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
}
