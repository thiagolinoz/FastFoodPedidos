package br.com.fiap.postechfasfood.infrastructure.external.client;

import br.com.fiap.postechfasfood.infrastructure.external.dto.ProdutoExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "produto-service",
    url = "${app.external.produtos.url:http://localhost:7777}"
)
public interface ProdutoFeignClient {

    @GetMapping("/api/v1/produtos/{cdProduto}")
    ProdutoExternoDTO buscarProdutoPorId(@PathVariable("cdProduto") String cdProduto);
}
