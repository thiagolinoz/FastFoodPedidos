package br.com.fiap.postechfasfood.infrastructure.adapters.rest;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.exception.PessoaNaoEncontradaException;
import br.com.fiap.postechfasfood.domain.ports.input.AtualizarStatusPedidoUseCase;
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
               description = "Lista todos os pedidos ordenados (Pronto > Em Preparação > Recebido)")
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

    @GetMapping("/debug/pessoa/{cpf}")
    @Operation(summary = "Debug - Testar API de pessoas",
               description = "Endpoint para debug da integração com API de pessoas")
    public ResponseEntity<?> debugPessoa(@PathVariable String cpf) {
        try {
            log.info("DEBUG - Testando CPF: {}", cpf);

            // Chamar diretamente o service para debug
            pessoaExternaService.verificarSeCpfExiste(cpf);

            return ResponseEntity.ok(Map.of(
                "cpf", cpf,
                "status", "ENCONTRADO_E_ATIVO",
                "message", "CPF encontrado e pessoa está ativa"
            ));

        } catch (PessoaNaoEncontradaException e) {
            log.warn("DEBUG - CPF {} não encontrado: {}", cpf, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "cpf", cpf,
                "status", "NAO_ENCONTRADO_OU_INATIVO",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("DEBUG - Erro inesperado para CPF {}: {}", cpf, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "cpf", cpf,
                "status", "ERRO",
                "message", e.getMessage(),
                "type", e.getClass().getSimpleName()
            ));
        }
    }
}
