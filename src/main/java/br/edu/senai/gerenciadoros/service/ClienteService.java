package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.ClienteRequest;
import br.edu.senai.gerenciadoros.dto.ClienteResponse;
import br.edu.senai.gerenciadoros.exception.ClienteNaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import br.edu.senai.gerenciadoros.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final OrdemServicoRepository ordemRepository;

    public List<ClienteResponse> listar(String nome) {
        List<Cliente> clientes = (nome == null || nome.isBlank())
                ? repository.findAll()
                : repository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream().map(ClienteResponse::de).toList();
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
    }

    @Transactional
    public Cliente cadastrar(ClienteRequest r) {
        if (repository.existsByEmail(r.email())) {
            throw new IllegalArgumentException("Já existe um cliente com este e-mail");
        }
        Cliente c = new Cliente();
        c.setNome(r.nome());
        c.setTelefone(r.telefone());
        c.setEmail(r.email());
        return repository.save(c);
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest r) {
        Cliente c = buscarPorId(id);
        if (!c.getEmail().equals(r.email()) && repository.existsByEmail(r.email())) {
            throw new IllegalArgumentException("Já existe um cliente com este e-mail");
        }
        c.setNome(r.nome());
        c.setTelefone(r.telefone());
        c.setEmail(r.email());
        return repository.save(c);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ClienteNaoEncontradoException(id);
        }
        if (ordemRepository.existsByClienteId(id)) {
            throw new IllegalStateException("Cliente possui ordens de serviço e não pode ser excluído");
        }
        repository.deleteById(id);
    }
}
