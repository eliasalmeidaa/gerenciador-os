package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.ClienteRequest;
import br.edu.senai.gerenciadoros.exception.NaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public List<Cliente> listar(String nome) {
        if (nome == null) {
            return repository.findAll();
        }
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
    }

    public Cliente cadastrar(ClienteRequest r) {
        Cliente c = new Cliente();
        c.setNome(r.nome());
        c.setTelefone(r.telefone());
        c.setEmail(r.email());
        return repository.save(c);
    }

    public Cliente atualizar(Long id, ClienteRequest r) {
        Cliente c = buscarPorId(id);
        c.setNome(r.nome());
        c.setTelefone(r.telefone());
        c.setEmail(r.email());
        return repository.save(c);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
