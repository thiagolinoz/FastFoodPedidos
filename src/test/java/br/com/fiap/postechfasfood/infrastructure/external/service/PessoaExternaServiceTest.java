package br.com.fiap.postechfasfood.infrastructure.external.service;

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
    @DisplayName("Deve tratar como pedido anônimo quando CPF não encontrado na API externa")
    void deveTratarComoPedidoAnonimoQuandoCpfNaoEncontrado() {
        // Arrange
        String cpf = "99999999999";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf))
            .thenThrow(FeignException.NotFound.class);

        // Act & Assert - não deve lançar exceção (trata como anônimo)
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve tratar como pedido anônimo quando API retorna pessoa null")
    void deveTratarComoPedidoAnonimoQuandoPessoaNull() {
        // Arrange
        String cpf = "12345678901";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(null);

        // Act & Assert - não deve lançar exceção (trata como anônimo)
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve tratar como pedido anônimo quando pessoa tem nome vazio")
    void deveTratarComoPedidoAnonimoQuandoNomeVazio() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaSemNome = new PessoaExternaDTO();
        pessoaSemNome.setCdDocPessoa(cpf);
        pessoaSemNome.setNmPessoa(""); // Nome vazio
        pessoaSemNome.setDsEmail("joao@teste.com");
        pessoaSemNome.setTpPessoa("CLIENTE");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaSemNome);

        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve tratar como pedido anônimo quando pessoa tem email vazio")
    void deveTratarComoPedidoAnonimoQuandoEmailVazio() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaSemEmail = new PessoaExternaDTO();
        pessoaSemEmail.setCdDocPessoa(cpf);
        pessoaSemEmail.setNmPessoa("João Silva");
        pessoaSemEmail.setDsEmail(null);
        pessoaSemEmail.setTpPessoa("CLIENTE");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaSemEmail);

        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve tratar como pedido anônimo quando pessoa tem tipo vazio")
    void deveTratarComoPedidoAnonimoQuandoTipoVazio() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaSemTipo = new PessoaExternaDTO();
        pessoaSemTipo.setCdDocPessoa(cpf);
        pessoaSemTipo.setNmPessoa("João Silva");
        pessoaSemTipo.setDsEmail("joao@teste.com");
        pessoaSemTipo.setTpPessoa("   ");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaSemTipo);

        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve validar com sucesso quando pessoa válida existe")
    void deveValidarComSucessoQuandoPessoaValidaExiste() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaValida = new PessoaExternaDTO();
        pessoaValida.setCdDocPessoa(cpf);
        pessoaValida.setNmPessoa("João Silva");
        pessoaValida.setDsEmail("joao@teste.com");
        pessoaValida.setTpPessoa("CLIENTE");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaValida);

        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve validar com sucesso quando cdDocPessoa é null mas outros campos estão preenchidos")
    void deveValidarComSucessoQuandoCdDocPessoaNullMasOutrosCamposPreenchidos() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaSemCdDoc = new PessoaExternaDTO();
        pessoaSemCdDoc.setCdDocPessoa(null);
        pessoaSemCdDoc.setNmPessoa("João Silva");
        pessoaSemCdDoc.setDsEmail("joao@teste.com");
        pessoaSemCdDoc.setTpPessoa("CLIENTE");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaSemCdDoc);

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve tratar como pedido anônimo quando API retorna todos os campos null")
    void deveTratarComoPedidoAnonimoQuandoTodosCamposNull() {
        // Arrange
        String cpf = "12345678901";
        PessoaExternaDTO pessoaVazia = new PessoaExternaDTO();
        pessoaVazia.setCdDocPessoa(null);
        pessoaVazia.setNmPessoa(null);
        pessoaVazia.setDsEmail(null);
        pessoaVazia.setTpPessoa(null);

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenReturn(pessoaVazia);

        assertDoesNotThrow(() -> pessoaExternaService.verificarSeCpfExiste(cpf));

        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando há erro de comunicação com a API")
    void deveLancarRuntimeExceptionQuandoErroFeignGenerico() {
        // Arrange
        String cpf = "12345678901";
        FeignException.InternalServerError feignError = mock(FeignException.InternalServerError.class);
        when(feignError.status()).thenReturn(500);
        when(feignError.getMessage()).thenReturn("Internal Server Error");

        when(pessoaFeignClient.buscarPessoaPorCpf(cpf)).thenThrow(feignError);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("Erro ao comunicar com serviço de pessoas"));
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando há erro inesperado")
    void deveLancarRuntimeExceptionQuandoErroInesperado() {
        // Arrange
        String cpf = "12345678901";
        when(pessoaFeignClient.buscarPessoaPorCpf(cpf))
            .thenThrow(new IllegalStateException("Erro inesperado"));

        // Act & Assert - deve lançar RuntimeException para erros inesperados
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> pessoaExternaService.verificarSeCpfExiste(cpf)
        );

        assertTrue(exception.getMessage().contains("Erro ao comunicar com serviço de pessoas"));
        verify(pessoaFeignClient, times(1)).buscarPessoaPorCpf(cpf);
    }



}
