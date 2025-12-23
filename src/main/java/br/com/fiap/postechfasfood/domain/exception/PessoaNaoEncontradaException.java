package br.com.fiap.postechfasfood.domain.exception;

public class PessoaNaoEncontradaException extends RuntimeException {
    public PessoaNaoEncontradaException(String message) {
        super(message);
    }

    public PessoaNaoEncontradaException(String cpf, Throwable cause) {
        super("Pessoa com CPF " + cpf + " não encontrada", cause);
    }
}
