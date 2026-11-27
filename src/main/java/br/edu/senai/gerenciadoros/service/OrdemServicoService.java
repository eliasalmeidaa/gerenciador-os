package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.OrdemServicoRequest;
import br.edu.senai.gerenciadoros.exception.NaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.model.OrdemServico;
import br.edu.senai.gerenciadoros.model.StatusOrdem;
import br.edu.senai.gerenciadoros.model.Tecnico;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import br.edu.senai.gerenciadoros.repository.OrdemServicoRepository;
import br.edu.senai.gerenciadoros.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;

    public List<OrdemServico> listar(StatusOrdem status) {
        if (status == null) {
            return repository.findAll();
        }
        return repository.findByStatus(status);
    }

    public OrdemServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ordem de serviço não encontrada"));
    }

    public OrdemServico abrir(OrdemServicoRequest r) {
        Cliente cliente = clienteRepository.findById(r.clienteId())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));

        OrdemServico os = new OrdemServico();
        os.setEquipamento(r.equipamento());
        os.setDefeito(r.defeito());
        os.setCliente(cliente);
        os.setStatus(StatusOrdem.ABERTA);
        os.setDataAbertura(LocalDateTime.now());
        return repository.save(os);
    }

    public OrdemServico atribuirTecnico(Long id, Long tecnicoId) {
        OrdemServico os = buscarPorId(id);
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new NaoEncontradoException("Técnico não encontrado"));
        os.setTecnico(tecnico);
        return repository.save(os);
    }

    public OrdemServico iniciar(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getTecnico() == null) {
            throw new IllegalArgumentException("Atribua um técnico antes de iniciar a OS");
        }
        os.setStatus(StatusOrdem.EM_ANDAMENTO);
        return repository.save(os);
    }

    public OrdemServico concluir(Long id, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() != StatusOrdem.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A OS precisa estar em andamento para ser concluída");
        }
        os.setValor(valor);
        os.setStatus(StatusOrdem.CONCLUIDA);
        os.setDataConclusao(LocalDateTime.now());
        return repository.save(os);
    }

    public OrdemServico cancelar(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() == StatusOrdem.CONCLUIDA) {
            throw new IllegalArgumentException("Não é possível cancelar uma OS concluída");
        }
        os.setStatus(StatusOrdem.CANCELADA);
        return repository.save(os);
    }
}
