package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.TecnicoRequest;
import br.edu.senai.gerenciadoros.exception.NaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Tecnico;
import br.edu.senai.gerenciadoros.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository repository;

    public List<Tecnico> listar() {
        return repository.findAll();
    }

    public Tecnico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Técnico não encontrado"));
    }

    public Tecnico cadastrar(TecnicoRequest r) {
        Tecnico t = new Tecnico();
        t.setNome(r.nome());
        t.setEspecialidade(r.especialidade());
        return repository.save(t);
    }

    public Tecnico atualizar(Long id, TecnicoRequest r) {
        Tecnico t = buscarPorId(id);
        t.setNome(r.nome());
        t.setEspecialidade(r.especialidade());
        return repository.save(t);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
