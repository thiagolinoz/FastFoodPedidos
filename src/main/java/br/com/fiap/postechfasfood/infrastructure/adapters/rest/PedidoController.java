package br.com.fiap.postechfasfood.infrastructure.adapters.rest;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "API de Gestão de Pedidos")
public class PedidoController {
    
    private final CadastrarPedidoUseCase cadastrarPedidoUseCase;
    private final AtualizarStatusPedidoUseCase atualizarStatusPedidoUseCase;
    private final ListarPedidosUseCase listarPedidosUseCase;
    private final ConsultarStatusPagamentoUseCase consultarStatusPagamentoUseCase;
    
    public PedidoController(
            CadastrarPedidoUseCase cadastrarPedidoUseCase,
            AtualizarStatusPedidoUseCase atualizarStatusPedidoUseCase,
            ListarPedidosUseCase listarPedidosUseCase,
            ConsultarStatusPagamentoUseCase consultarStatusPagamentoUseCase) {
        this.cadastrarPedidoUseCase = cadastrarPedidoUseCase;
        this.atualizarStatusPedidoUseCase = atualizarStatusPedidoUseCase;
        this.listarPedidosUseCase = listarPedidosUseCase;
        this.consultarStatusPagamentoUseCase = consultarStatusPagamentoUseCase;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Realizar checkout", 
               description = "Cria um novo pedido usando o nome do produto (não FK)")
    public ResponseEntity<PedidoResponse> checkout(@Valid @RequestBody CheckoutPedidoRequest request) {
        // Converter DTO para objeto de domínio
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
}
