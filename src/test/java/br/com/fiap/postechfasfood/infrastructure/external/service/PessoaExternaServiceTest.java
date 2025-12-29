package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.exception.PessoaNaoEncontradaException;
import br.com.fiap.postechfasfood.infrastructure.external.client.PessoaFeignClient;
import br.com.fiap.postechfasfood.infrastructure.external.dto.PessoaExternaDTO;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PessoaExternaService")
class PessoaExternaServiceTest {

    @Mock
    private PessoaFeignClient pessoaFeignClient;

    private PessoaExternaService pessoaExternaService;

    @BeforeEach
    void setUp() {
        pessoaExternaService = new PessoaExternaService(pessoaFeignClient);
    }

    @Test
    @DisplayName("Deve permitir pedido anônimo quando CPF é null")
    void devePermitirPedidoAnonimoQuandoCpfNulo() {
        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(null));

        // Não deve chamar API externa
        verify(pessoaFeignClient, never()).buscarPessoaPorCpf(any());
    }

    @Test
    @DisplayName("Deve permitir pedido anônimo quando CPF é vazio")
    void devePermitirPedidoAnonimoQuandoCpfVazio() {
        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(""));
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste("   "));

        // Não deve chamar API externa
        verify(pessoaFeignClient, never()).buscarPessoaPorCpf(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF não encontrado na API externa")
    void deveLancarExcecaoQuandoCpfNaoEncontrado() {
        // Arrange
        String cpf = "99999999999";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf))
            .thenThrow(FeignException.NotFound.class);

        // Act & Assert - A exceção será capturada pelo GlobalExceptionHandler que retornará 204
        PessoaNaoEncontradaException exception = assertThrows(
            PessoaNaoEncontradaException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
        assertTrue(exception.getMessage().contains(cpf));
    }

    @Test
    @DisplayName("Deve lançar exceção quando API retorna pessoa null")
    void deveLancarExcecaoQuandoPessoaNull() {
        // Arrange
        String cpf = "12345678901";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(null);

        // Act & Assert - A exceção será capturada pelo GlobalExceptionHandler que retornará 204
        PessoaNaoEncontradaException exception = assertThrows(
            PessoaNaoEncontradaException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa está inativa")
    void deveLancarExcecaoQuandoPessoaInativa() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaInativa = new PessoaExternaDTO();
        pessoaInativa.setCpf(cpf);
        pessoaInativa.setNome("João Silva");
        pessoaInativa.setEmail("joao@teste.com");
        pessoaInativa.setAtivo(false); // PESSOA INATIVA

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaInativa);

        // Act & Assert - A exceção será capturada pelo GlobalExceptionHandler que retornará 204
        PessoaNaoEncontradaException exception = assertThrows(
            PessoaNaoEncontradaException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("não está ativa"));
    }

    @Test
    @DisplayName("Deve validar com sucesso quando pessoa ativa existe")
    void deveValidarComSucessoQuandoPessoaAtivaExiste() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaAtiva = new PessoaExternaDTO();
        pessoaAtiva.setCpf(cpf);
        pessoaAtiva.setNome("João Silva");
        pessoaAtiva.setEmail("joao@teste.com");
        pessoaAtiva.setAtivo(true); // PESSOA ATIVA

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaAtiva);

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção runtime quando erro na API externa")
    void deveLancarExcecaoRuntimeQuandoErroApiExterna() {
        // Arrange
        String cpf = "12345678901";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf))
            .thenThrow(FeignException.InternalServerError.class);

        // Act & Assert
        PessoaNaoEncontradaException exception = assertThrows(
            PessoaNaoEncontradaException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve assumir pessoa ativa quando campo ativo é null mas há dados válidos")
    void deveAssumirPessoaAtivaQuandoCampoAtivoNull() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaComAtivoNull = new PessoaExternaDTO();
        pessoaComAtivoNull.setCpf(cpf);
        pessoaComAtivoNull.setNome("João Silva");
        pessoaComAtivoNull.setEmail("joao@teste.com");
        pessoaComAtivoNull.setAtivo(null); // CAMPO ATIVO NULL

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaComAtivoNull);

        // Act & Assert - Deve assumir pessoa ativa quando há dados válidos
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));
    }
}
