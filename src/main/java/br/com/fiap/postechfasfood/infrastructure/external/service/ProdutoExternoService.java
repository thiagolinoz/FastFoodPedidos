package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import br.com.fiap.postechfasfood.infrastructure.external.client.ProdutoFeignClient;
import br.com.fiap.postechfasfood.infrastructure.external.dto.ProdutoExternoDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoExternoService {

    private final ProdutoFeignClient produtoFeignClient;

    public Produto buscarProdutoPorId(String cdProduto) {
        try {
            log.info("Buscando produto por ID na API externa: {}", cdProduto);

            ProdutoExternoDTO produtoDTO = produtoFeignClient.buscarProdutoPorId(cdProduto);

            if (produtoDTO == null) {
                log.error("API externa retornou null para produto: {}", cdProduto);
                throw new IllegalArgumentException("Produto não encontrado: " + cdProduto);
            }

            if (!produtoDTO.getSnAtivo()) {
                log.warn("Produto {} está inativo na API externa", cdProduto);
                throw new IllegalArgumentException("Produto inativo: " + cdProduto);
            }

            log.info("Produto encontrado na API externa: {} - {}", produtoDTO.getCdProduto(), produtoDTO.getNmProduto());

            return new Produto(
                produtoDTO.getCdProduto(),
                produtoDTO.getNmProduto(),
                produtoDTO.getDsDescricao(),
                produtoDTO.getVlPreco(),
                produtoDTO.getSnAtivo(),
                CategoriaProduto.fromString(produtoDTO.getTpCategoria())
            );

        } catch (FeignException.NotFound e) {
            log.error("Produto {} não encontrado na API externa (404)", cdProduto);
            throw new IllegalArgumentException("Produto não encontrado: " + cdProduto);
        } catch (FeignException.InternalServerError e) {
            log.error("API externa retornou erro 500 para produto {}: {}", cdProduto, e.getMessage());
            throw new RuntimeException("API de produtos temporariamente indisponível. Verifique se o serviço está funcionando corretamente.", e);
        } catch (FeignException e) {
            log.error("Erro de comunicação com API externa para produto {}: Status {} - {}",
                     cdProduto, e.status(), e.getMessage());
            throw new RuntimeException("Erro ao comunicar com serviço de produtos: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar produto {}: {}", cdProduto, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao processar produto: " + e.getMessage(), e);
        }
    }
}
