package br.com.fiap.postechfasfood.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProdutoExternoDTO {

    @JsonProperty("cdProduto")
    private String cdProduto;

    @JsonProperty("nmProduto")
    private String nmProduto;

    @JsonProperty("dsDescricao")
    private String dsDescricao;

    @JsonProperty("vlPreco")
    private Double vlPreco;

    @JsonProperty("snAtivo")
    private Boolean snAtivo;

    @JsonProperty("tpCategoria")
    private String tpCategoria;
}
