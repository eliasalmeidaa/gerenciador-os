package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.TecnicoRequest;
import br.edu.senai.gerenciadoros.dto.TecnicoResponse;
import br.edu.senai.gerenciadoros.exception.TecnicoNaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Tecnico;
import br.edu.senai.gerenciadoros.repository.OrdemServicoRepository;
import br.edu.senai.gerenciadoros.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository repository;
    private final OrdemServicoRepository ordemRepository;

    public List<TecnicoResponse> listar() {
        return repository.findAll().stream().map(TecnicoResponse::de).toList();
    }

    public Tecnico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TecnicoNaoEncontradoException(id));
    }

    @Transactional
    public Tecnico cadastrar(TecnicoRequest r) {
        Tecnico t = new Tecnico();
        t.setNome(r.nome());
        t.setEspecialidade(r.especialidade());
        return repository.save(t);
    }

    @Transactional
    public Tecnico atualizar(Long id, TecnicoRequest r) {
        Tecnico t = buscarPorId(id);
        t.setNome(r.nome());
        t.setEspecialidade(r.especialidade());
        return repository.save(t);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new TecnicoNaoEncontradoException(id);
        }
        if (ordemRepository.existsByTecnicoId(id)) {
            throw new IllegalStateException("Técnico possui ordens de serviço e não pode ser excluído");
        }
        repository.deleteById(id);
    }
}
