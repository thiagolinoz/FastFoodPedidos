package br.com.fiap.postechfasfood.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PessoaExternaDTO {

    @JsonProperty("cpf")
    private String cpf;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("email")
    private String email;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("cdDocPessoa")
    private String cdDocPessoa;

    @JsonProperty("nmPessoa")
    private String nmPessoa;

    @JsonProperty("dsEmail")
    private String dsEmail;

    @JsonProperty("snAtivo")
    private Boolean snAtivo;

    @JsonProperty("tpPessoa")
    private String tpPessoa;


    public boolean isAtiva() {
        if (Boolean.TRUE.equals(ativo)) {
            return true;
        }

        if (Boolean.TRUE.equals(snAtivo)) {
            return true;
        }

        return false;
    }

    public String getCpfUnificado() {
        return cpf != null ? cpf : cdDocPessoa;
    }

    public String getNomeUnificado() {
        return nome != null ? nome : nmPessoa;
    }

    public String getEmailUnificado() {
        return email != null ? email : dsEmail;
    }

    public boolean isEmpty() {
        return cpf == null && nome == null && email == null && ativo == null &&
               cdDocPessoa == null && nmPessoa == null && dsEmail == null && snAtivo == null;
    }
}

