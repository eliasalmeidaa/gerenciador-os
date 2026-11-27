package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.ClienteRequest;
import br.edu.senai.gerenciadoros.exception.ClienteNaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import br.edu.senai.gerenciadoros.repository.OrdemServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository repository;

    @Mock
    OrdemServicoRepository ordemRepository;

    @InjectMocks
    ClienteService service;

    @Test
    void deveBuscarClientePorId() {
        var cliente = new Cliente(1L, "Maria", "11999990000", "maria@email.com"); // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        var resultado = service.buscarPorId(1L); // Act
        assertEquals("Maria", resultado.getNome()); // Assert
        verify(repository).findById(1L);
    }

    @Test
    void deveRetornarErroQuandoClienteNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveCadastrarClienteComDadosValidos() {
        var request = new ClienteRequest("Maria", "11999990000", "maria@email.com");
        when(repository.existsByEmail("maria@email.com")).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        service.cadastrar(request);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository, times(1)).save(captor.capture());
        assertEquals("Maria", captor.getValue().getNome());
    }

    @Test
    void naoDeveCadastrarClienteComEmailDuplicado() {
        var request = new ClienteRequest("Maria", "11999990000", "maria@email.com");
        when(repository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveExcluirClienteComOrdensDeServico() {
        when(repository.existsById(1L)).thenReturn(true);
        when(ordemRepository.existsByClienteId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.excluir(1L));
        verify(repository, never()).deleteById(any());
    }
}
