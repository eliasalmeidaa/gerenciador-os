package br.edu.senai.gerenciadoros.service;

import br.edu.senai.gerenciadoros.dto.ClienteRequest;
import br.edu.senai.gerenciadoros.exception.NaoEncontradoException;
import br.edu.senai.gerenciadoros.model.Cliente;
import br.edu.senai.gerenciadoros.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    ClienteService service;

    @Test
    void deveBuscarClientePorId() {
        Cliente cliente = new Cliente(1L, "Maria", "11999990000", "maria@email.com");
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorId(1L);

        assertEquals("Maria", resultado.getNome());
        verify(repository).findById(1L);
    }

    @Test
    void deveRetornarErroQuandoClienteNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveCadastrarCliente() {
        ClienteRequest request = new ClienteRequest("Maria", "11999990000", "maria@email.com");
        when(repository.save(any(Cliente.class))).thenReturn(new Cliente(1L, "Maria", "11999990000", "maria@email.com"));

        Cliente resultado = service.cadastrar(request);

        assertEquals(1L, resultado.getId());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void naoDeveExcluirClienteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.excluir(99L));
        verify(repository, never()).deleteById(any());
    }
}
