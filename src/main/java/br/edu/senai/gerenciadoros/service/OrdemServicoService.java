package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.OrdemServicoRequest;
import br.edu.senai.gerenciadoros.exception.ClienteNaoEncontradoException;
import br.edu.senai.gerenciadoros.exception.OrdemNaoEncontradaException;
import br.edu.senai.gerenciadoros.exception.TecnicoNaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.model.OrdemServico;
import br.edu.senai.gerenciadoros.model.StatusOrdem;
import br.edu.senai.gerenciadoros.model.Tecnico;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import br.edu.senai.gerenciadoros.repository.OrdemServicoRepository;
import br.edu.senai.gerenciadoros.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return status == null ? repository.findAll() : repository.findByStatus(status);
    }

    public OrdemServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrdemNaoEncontradaException(id));
    }

    @Transactional
    public OrdemServico abrir(OrdemServicoRequest r) {
        Cliente cliente = clienteRepository.findById(r.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(r.clienteId()));

        OrdemServico os = new OrdemServico();
        os.setEquipamento(r.equipamento());
        os.setDefeito(r.defeito());
        os.setCliente(cliente);
        os.setStatus(StatusOrdem.ABERTA);
        os.setDataAbertura(LocalDateTime.now());
        return repository.save(os);
    }

    @Transactional
    public OrdemServico atribuirTecnico(Long id, Long tecnicoId) {
        OrdemServico os = buscarPorId(id);
        validarNaoFinalizada(os);
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new TecnicoNaoEncontradoException(tecnicoId));
        os.setTecnico(tecnico);
        return repository.save(os);
    }

    @Transactional
    public OrdemServico iniciar(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() != StatusOrdem.ABERTA) {
            throw new IllegalStateException("Só é possível iniciar uma OS com status ABERTA");
        }
        if (os.getTecnico() == null) {
            throw new IllegalStateException("Atribua um técnico antes de iniciar a OS");
        }
        os.setStatus(StatusOrdem.EM_ANDAMENTO);
        return repository.save(os);
    }

    public void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor do serviço deve ser maior que zero");
        }
    }

    @Transactional
    public OrdemServico concluir(Long id, BigDecimal valor) {
        validarValor(valor);
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() != StatusOrdem.EM_ANDAMENTO) {
            throw new IllegalStateException("Só é possível concluir uma OS em andamento");
        }
        os.setValor(valor);
        os.setStatus(StatusOrdem.CONCLUIDA);
        os.setDataConclusao(LocalDateTime.now());
        return repository.save(os);
    }

    @Transactional
    public OrdemServico cancelar(Long id) {
        OrdemServico os = buscarPorId(id);
        validarNaoFinalizada(os);
        os.setStatus(StatusOrdem.CANCELADA);
        return repository.save(os);
    }

    private void validarNaoFinalizada(OrdemServico os) {
        if (os.getStatus() == StatusOrdem.CONCLUIDA || os.getStatus() == StatusOrdem.CANCELADA) {
            throw new IllegalStateException("OS finalizada não pode ser alterada");
        }
    }
}
