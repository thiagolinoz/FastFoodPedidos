package br.com.fiap.postechfasfood.infrastructure.adapters.rest;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.exception.PessoaNaoEncontradaException;
import br.com.fiap.postechfasfood.domain.ports.input.AtualizarStatusPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidoPorNumeroUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidosPorStatusUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.ConsultarStatusPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.ListarPedidosUseCase;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.CheckoutPedidoRequest;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.PedidoResponse;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.StatusPagamentoResponse;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.mapper.PedidoMapper;
import br.com.fiap.postechfasfood.infrastructure.external.service.PessoaExternaService;
import br.com.fiap.postechfasfood.infrastructure.external.service.ProdutoExternoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "API de Gestão de Pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final CadastrarPedidoUseCase cadastrarPedidoUseCase;
    private final AtualizarStatusPedidoUseCase atualizarStatusPedidoUseCase;
    private final ListarPedidosUseCase listarPedidosUseCase;
    private final ConsultarStatusPagamentoUseCase consultarStatusPagamentoUseCase;
    private final BuscarPedidoPorNumeroUseCase buscarPedidoPorNumeroUseCase;
    private final BuscarPedidosPorStatusUseCase buscarPedidosPorStatusUseCase;
    private final ProdutoExternoService produtoExternoService;
    private final PessoaExternaService pessoaExternaService;

    @PostMapping("/checkout")
    @Operation(summary = "Realizar checkout", 
               description = "Cria um novo pedido usando o ID do produto (consumindo API externa) e valida CPF no MS de pessoas")
    public ResponseEntity<PedidoResponse> checkout(@Valid @RequestBody CheckoutPedidoRequest request) {
        CadastrarPedidoUseCase.CadastrarPedidoRequest useCaseRequest = 
            PedidoMapper.toUseCaseRequest(request);

        Pedido pedido = cadastrarPedidoUseCase.executar(useCaseRequest);

        PedidoResponse response = PedidoMapper.toResponse(pedido);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{cdPedido}/status/{txStatus}")
    @Operation(summary = "Atualizar status do pedido", 
               description = "Atualiza o status de um pedido no ciclo de vida")
    public ResponseEntity<PedidoResponse> atualizarStatus(
            @PathVariable String cdPedido,
            @PathVariable String txStatus) {

        StatusPedido novoStatus = StatusPedido.fromString(txStatus);

        Pedido pedido = atualizarStatusPedidoUseCase.executar(cdPedido, novoStatus);

        PedidoResponse response = PedidoMapper.toResponse(pedido);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", 
               description = "Lista todos os pedidos ordenados por status (Pronto > Em Preparação > Recebido) e por data de criação")
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {

        List<Pedido> pedidos = listarPedidosUseCase.executar();

        List<PedidoResponse> response = PedidoMapper.toResponseList(pedidos);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{nrPedido}/pagamento/status")
    @Operation(summary = "Consultar status de pagamento", 
               description = "Verifica o status atual do pagamento no serviço externo")
    public ResponseEntity<StatusPagamentoResponse> consultarStatusPagamento(
            @PathVariable Integer nrPedido) {

        StatusPagamento status = consultarStatusPagamentoUseCase.executar(nrPedido);

        StatusPagamentoResponse response = PedidoMapper.toStatusPagamentoResponse(nrPedido, status);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/produtos/{idProduto}")
    @Operation(summary = "Buscar produto por ID",
               description = "Busca um produto específico na API externa pelo ID")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable String idProduto) {
        try {
            Produto produto = produtoExternoService.buscarProdutoPorId(idProduto);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Buscar pedido por número",
               description = "Busca um pedido específico pelo seu número")
    public ResponseEntity<PedidoResponse> buscarPorNumeroPedido(@PathVariable Integer numeroPedido) {
        try {
            Pedido pedido = buscarPedidoPorNumeroUseCase.executar(numeroPedido);
            PedidoResponse response = PedidoMapper.toResponse(pedido);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar pedidos por status",
               description = "Lista todos os pedidos com um status específico")
    public ResponseEntity<List<PedidoResponse>> buscarPorStatusPedido(@PathVariable String status) {
        try {
            StatusPedido statusPedido = StatusPedido.fromString(status);
            List<Pedido> pedidos = buscarPedidosPorStatusUseCase.executar(statusPedido);
            List<PedidoResponse> response = PedidoMapper.toResponseList(pedidos);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
