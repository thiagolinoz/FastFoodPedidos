package br.com.fiap.postechfasfood;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostechFastfoodApplicationTest {

    @Test
    @DisplayName("Deve ter método main para inicialização da aplicação")
    void deveTemMetodoMain() {
        // Act & Assert - Verifica se a classe tem o método main
        assertDoesNotThrow(() -> {
            PostechFastfoodApplication.class.getDeclaredMethod("main", String[].class);
        });
    }

    @Test
    @DisplayName("Deve poder criar instância da classe principal")
    void devePodeCriarInstancia() {
        // Act & Assert - Verifica se pode criar uma instância
        assertDoesNotThrow(() -> {
            new PostechFastfoodApplication();
        });
    }
}
