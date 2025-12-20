package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.exception.ClienteNaoCadastradoException;
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
    @DisplayName("Deve verificar CPF existente com sucesso")
    void deveVerificarCpfExistenteComSucesso() {
        // Arrange
        String cpf = "12345678900";
        PessoaExternaDTO pessoaDto = new PessoaExternaDTO();
        pessoaDto.setCpf(cpf);
        pessoaDto.setNome("João Silva");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaDto);

        // Act & Assert
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for nulo")
    void deveLancarExcecaoQuandoCpfForNulo() {
        // Act & Assert
        ClienteNaoCadastradoException exception = assertThrows(
            ClienteNaoCadastradoException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(null)
        );

        assertEquals("Cliente não cadastrado para o CPF: CPF não informado", exception.getMessage());
        verify(pessoaFeignClient, never()).buscarPessoaPorCpf(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for vazio")
    void deveLancarExcecaoQuandoCpfForVazio() {
        // Act & Assert
        ClienteNaoCadastradoException exception = assertThrows(
            ClienteNaoCadastradoException.class,
            () -> pessoaExternaService.verificarSeCpfExiste("")
        );

        assertEquals("Cliente não cadastrado para o CPF: CPF não informado", exception.getMessage());
        verify(pessoaFeignClient, never()).buscarPessoaPorCpf(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for apenas espaços")
    void deveLancarExcecaoQuandoCpfForApenasEspacos() {
        // Act & Assert
        ClienteNaoCadastradoException exception = assertThrows(
            ClienteNaoCadastradoException.class,
            () -> pessoaExternaService.verificarSeCpfExiste("   ")
        );

        assertEquals("Cliente não cadastrado para o CPF: CPF não informado", exception.getMessage());
        verify(pessoaFeignClient, never()).buscarPessoaPorCpf(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando API retornar null")
    void deveLancarExcecaoQuandoApiRetornarNull() {
        // Arrange
        String cpf = "12345678900";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao verificar CPF"));
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando API retornar 404 Not Found")
    void deveLancarExcecaoQuandoApiRetornar404NotFound() {
        // Arrange
        String cpf = "99999999999";
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenThrow(notFoundException);

        // Act & Assert
        ClienteNaoCadastradoException exception = assertThrows(
            ClienteNaoCadastradoException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains(cpf));
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para outros erros Feign")
    void deveLancarRuntimeExceptionParaOutrosErrosFeign() {
        // Arrange
        String cpf = "12345678900";
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(500);
        when(feignException.getMessage()).thenReturn("Internal Server Error");
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenThrow(feignException);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("Erro ao comunicar com serviço de pessoas"));
        assertTrue(exception.getCause() instanceof FeignException);
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para exceções gerais")
    void deveLancarRuntimeExceptionParaExcecoesGerais() {
        // Arrange
        String cpf = "12345678900";
        RuntimeException runtimeException = new RuntimeException("Erro inesperado");
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenThrow(runtimeException);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao verificar CPF"));
        assertEquals(runtimeException, exception.getCause());
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve aceitar CPF com espaços ao redor mas não vazio")
    void deveAceitarCpfComEspacosAoRedorMasNaoVazio() {
        // Arrange
        String cpfComEspacos = "  12345678900  ";
        PessoaExternaDTO pessoaDto = new PessoaExternaDTO();
        pessoaDto.setCpf("12345678900");
        pessoaDto.setNome("João Silva");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpfComEspacos)).thenReturn(pessoaDto);

        // Act & Assert
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpfComEspacos));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpfComEspacos);
    }

    @Test
    @DisplayName("Deve verificar diferentes formatos de CPF")
    void deveVerificarDiferentesFormatosDeCpf() {
        // Arrange
        String[] cpfs = {
            "12345678900",
            "123.456.789-00",
            "000.000.000-00"
        };

        for (String cpf : cpfs) {
            PessoaExternaDTO pessoaDto = new PessoaExternaDTO();
            pessoaDto.setCpf(cpf);
            when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaDto);

            // Act & Assert
            assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));
        }

        verify(pessoaFeignClient, times(cpfs.length)).buscarPessoaPorCpf(any());
    }
}
