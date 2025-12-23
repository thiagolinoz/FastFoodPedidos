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

    // Campos alternativos que a API externa pode estar usando
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

    /**
     * Método helper para verificar se a pessoa está ativa.
     * Trata null como false (inativo) e verifica múltiplos campos.
     *
     * @return true se ativo for Boolean.TRUE, false caso contrário (null ou false)
     */
    public boolean isAtiva() {
        // Verificar primeiro o campo padrão
        if (Boolean.TRUE.equals(ativo)) {
            return true;
        }

        // Verificar campo alternativo snAtivo
        if (Boolean.TRUE.equals(snAtivo)) {
            return true;
        }

        return false;
    }

    /**
     * Getter unificado para CPF que tenta diferentes campos
     */
    public String getCpfUnificado() {
        return cpf != null ? cpf : cdDocPessoa;
    }

    /**
     * Getter unificado para nome que tenta diferentes campos
     */
    public String getNomeUnificado() {
        return nome != null ? nome : nmPessoa;
    }

    /**
     * Getter unificado para email que tenta diferentes campos
     */
    public String getEmailUnificado() {
        return email != null ? email : dsEmail;
    }

    /**
     * Verifica se o objeto está "vazio" (todos os campos null)
     */
    public boolean isEmpty() {
        return cpf == null && nome == null && email == null && ativo == null &&
               cdDocPessoa == null && nmPessoa == null && dsEmail == null && snAtivo == null;
    }
}

