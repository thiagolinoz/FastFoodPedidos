package br.com.fiap.postechfasfood.infrastructure.adapters.rest.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException e retornar BAD_REQUEST")
    void deveTratarMethodArgumentNotValidException() {
        // Arrange
        Object target = new Object();
        String objectName = "pedidoRequest";
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
        
        bindingResult.addError(new FieldError(objectName, "campo1", "Campo 1 é obrigatório"));
        bindingResult.addError(new FieldError(objectName, "campo2", "Campo 2 deve ser positivo"));

        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals("Erro de validação", body.message());
        assertNotNull(body.timestamp());
        
        Map<String, String> errors = body.errors();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Campo 1 é obrigatório", errors.get("campo1"));
        assertEquals("Campo 2 deve ser positivo", errors.get("campo2"));
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException e retornar BAD_REQUEST")
    void deveTratarIllegalArgumentException() {
        // Arrange
        String mensagemErro = "Argumento inválido fornecido";
        IllegalArgumentException exception = new IllegalArgumentException(mensagemErro);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals(mensagemErro, body.message());
        assertNotNull(body.timestamp());
        assertNull(body.errors());
    }

    @Test
    @DisplayName("Deve tratar IllegalStateException e retornar UNPROCESSABLE_ENTITY")
    void deveTratarIllegalStateException() {
        // Arrange
        String mensagemErro = "Estado inválido do pedido";
        IllegalStateException exception = new IllegalStateException(mensagemErro);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleIllegalStateException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), body.status());
        assertEquals(mensagemErro, body.message());
        assertNotNull(body.timestamp());
        assertNull(body.errors());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica e retornar INTERNAL_SERVER_ERROR")
    void deveTratarExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro inesperado");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.status());
        assertEquals("Erro interno do servidor", body.message());
        assertNotNull(body.timestamp());
        assertNull(body.errors());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com todos os campos")
    void deveCriarErrorResponseComTodosCampos() {
        // Arrange
        Map<String, String> errors = Map.of("campo", "mensagem");

        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = 
            new GlobalExceptionHandler.ErrorResponse(
                java.time.LocalDateTime.now(),
                400,
                "Mensagem de erro",
                errors
            );

        // Assert
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.status());
        assertEquals("Mensagem de erro", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
        assertEquals(errors, errorResponse.errors());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse sem erros detalhados")
    void deveCriarErrorResponseSemErrosDetalhados() {
        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = 
            new GlobalExceptionHandler.ErrorResponse(
                java.time.LocalDateTime.now(),
                500,
                "Erro genérico",
                null
            );

        // Assert
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.status());
        assertEquals("Erro genérico", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
        assertNull(errorResponse.errors());
    }

    @Test
    @DisplayName("Deve tratar NullPointerException através do handler genérico")
    void deveTratarNullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Objeto nulo");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar RuntimeException através do handler genérico")
    void deveTratarRuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Erro em tempo de execução");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve incluir timestamp atual na resposta de erro")
    void deveIncluirTimestampAtualNaResposta() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Teste");
        java.time.LocalDateTime antes = java.time.LocalDateTime.now();

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleIllegalArgumentException(exception);
        
        java.time.LocalDateTime depois = java.time.LocalDateTime.now();

        // Assert
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.timestamp().isAfter(antes.minusSeconds(1)));
        assertTrue(body.timestamp().isBefore(depois.plusSeconds(1)));
    }
}
