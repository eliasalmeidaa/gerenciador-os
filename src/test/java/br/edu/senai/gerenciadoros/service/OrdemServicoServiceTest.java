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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    OrdemServicoRepository repository;

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    TecnicoRepository tecnicoRepository;

    @InjectMocks
    OrdemServicoService service;

    Cliente cliente;
    Tecnico tecnico;
    OrdemServico ordem;

    @BeforeEach
    void preparar() {
        cliente = new Cliente(1L, "Maria", "11999990000", "maria@email.com");
        tecnico = new Tecnico(1L, "Carlos", "Notebooks");
        ordem = new OrdemServico();
        ordem.setId(10L);
        ordem.setEquipamento("Notebook Dell");
        ordem.setDefeito("Não liga");
        ordem.setCliente(cliente);
        ordem.setStatus(StatusOrdem.ABERTA);
    }

    @Test
    void deveAbrirOrdemComStatusAberta() {
        OrdemServicoRequest request = new OrdemServicoRequest("Notebook Dell", "Não liga", 1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(any(OrdemServico.class))).thenReturn(ordem);

        OrdemServico resultado = service.abrir(request);

        assertEquals(StatusOrdem.ABERTA, resultado.getStatus());
        verify(repository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    void naoDeveAbrirOrdemParaClienteInexistente() {
        OrdemServicoRequest request = new OrdemServicoRequest("Notebook Dell", "Não liga", 99L);
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.abrir(request));
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveIniciarOrdemSemTecnico() {
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThrows(IllegalArgumentException.class, () -> service.iniciar(10L));
        verify(repository, never()).save(any());
    }

    @Test
    void deveIniciarOrdemComTecnico() {
        ordem.setTecnico(tecnico);
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));
        when(repository.save(ordem)).thenReturn(ordem);

        OrdemServico resultado = service.iniciar(10L);

        assertEquals(StatusOrdem.EM_ANDAMENTO, resultado.getStatus());
    }

    @Test
    void deveConcluirOrdemEmAndamento() {
        ordem.setTecnico(tecnico);
        ordem.setStatus(StatusOrdem.EM_ANDAMENTO);
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));
        when(repository.save(ordem)).thenReturn(ordem);

        OrdemServico resultado = service.concluir(10L, new BigDecimal("150.00"));

        assertEquals(StatusOrdem.CONCLUIDA, resultado.getStatus());
        assertEquals(new BigDecimal("150.00"), resultado.getValor());
        verify(repository, times(1)).save(ordem);
    }

    @Test
    void naoDeveConcluirOrdemQueNaoEstaEmAndamento() {
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThrows(IllegalArgumentException.class,
                () -> service.concluir(10L, new BigDecimal("150.00")));
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 0})
    void naoDeveConcluirComValorZeroOuNegativo(int valor) {
        assertThrows(IllegalArgumentException.class,
                () -> service.concluir(10L, BigDecimal.valueOf(valor)));
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveCancelarOrdemConcluida() {
        ordem.setStatus(StatusOrdem.CONCLUIDA);
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThrows(IllegalArgumentException.class, () -> service.cancelar(10L));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRetornarErroQuandoOrdemNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.buscarPorId(99L));
    }
}
