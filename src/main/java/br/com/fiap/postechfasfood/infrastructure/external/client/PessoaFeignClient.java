package br.com.fiap.postechfasfood.infrastructure.external.client;

import br.com.fiap.postechfasfood.infrastructure.external.dto.PessoaExternaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pessoa-service",  url = "${app.external.pessoas.url:http://localhost:9000}"
//    url = "${app.external.pessoas.url}"
)
public interface PessoaFeignClient {

    @GetMapping("/api/v1/pessoa/{cdDocPessoa}")
    PessoaExternaDTO buscarPessoaPorCpf(@PathVariable("cdDocPessoa") String cdDocPessoa);
}

