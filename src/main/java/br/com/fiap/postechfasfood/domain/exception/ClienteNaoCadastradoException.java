package br.com.fiap.postechfasfood.domain.exception;

public class ClienteNaoCadastradoException extends RuntimeException {

    public ClienteNaoCadastradoException(String cpf) {
        super("Cliente não cadastrado para o CPF: " + cpf);
    }

    public ClienteNaoCadastradoException(String cpf, Throwable cause) {
        super("Cliente não cadastrado para o CPF: " + cpf, cause);
    }
}
