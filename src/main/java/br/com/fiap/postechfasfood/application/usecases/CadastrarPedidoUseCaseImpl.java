package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CadastrarPedidoUseCaseImpl implements CadastrarPedidoUseCase {
    
    private final PedidoRepositoryPort pedidoRepository;
    private final CatalogoServicePort catalogoService;
    
    public CadastrarPedidoUseCaseImpl(
            PedidoRepositoryPort pedidoRepository,
            CatalogoServicePort catalogoService) {
        this.pedidoRepository = pedidoRepository;
        this.catalogoService = catalogoService;
    }
    
    @Override
    public Pedido executar(CadastrarPedidoRequest request) {
        if (request == null || request.itens() == null || request.itens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter pelo menos um item");
        }

        List<ItemPedido> itensPedido = new ArrayList<>();
        for (var itemRequest : request.itens()) {
            Produto produto = catalogoService.buscarProdutoPorNome(itemRequest.nomeProduto())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Produto não encontrado: " + itemRequest.nomeProduto()));
            
            if (!produto.isAtivo()) {
                throw new IllegalArgumentException(
                    "Produto não está disponível: " + itemRequest.nomeProduto());
            }
            
            ItemPedido item = new ItemPedido(
                produto.getNome(),
                produto.getId(),
                itemRequest.quantidade(),
                produto.getPreco()
            );
            itensPedido.add(item);
        }

        Integer numeroPedido = pedidoRepository.gerarProximoNumeroPedido();

        Pedido pedido = new Pedido.Builder()
            .id(UUID.randomUUID().toString())
            .documentoCliente(request.documentoCliente())
            .status(StatusPedido.AGUARDANDO_PAGAMENTO)
            .numeroPedido(numeroPedido)
            .dataCriacao(LocalDateTime.now())
            .dataUltimaAtualizacao(LocalDateTime.now())
            .itens(itensPedido)
            .build();

        return pedidoRepository.salvar(pedido);
    }
}
