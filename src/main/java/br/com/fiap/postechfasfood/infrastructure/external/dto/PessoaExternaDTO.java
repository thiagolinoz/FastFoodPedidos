package br.com.fiap.postechfasfood.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PessoaExternaDTO {

    @JsonProperty("cdDocPessoa")
    private String cdDocPessoa;

    @JsonProperty("nmPessoa")
    private String nmPessoa;

    @JsonProperty("dsEmail")
    private String dsEmail;

    @JsonProperty("tpPessoa")
    private String tpPessoa;

    public boolean isEmpty() {
        return cdDocPessoa == null && nmPessoa == null && dsEmail == null && tpPessoa == null;
    }
}

