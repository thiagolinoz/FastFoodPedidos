package br.com.fiap.postechfasfood.infrastructure.config;

import br.com.fiap.postechfasfood.application.usecases.*;
import br.com.fiap.postechfasfood.domain.ports.input.*;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CadastrarPedidoUseCase cadastrarPedidoUseCase(
            PedidoRepositoryPort pedidoRepository,
            CatalogoServicePort catalogoService) {
        return new CadastrarPedidoUseCaseImpl(pedidoRepository, catalogoService);
    }

    @Bean
    public AtualizarStatusPedidoUseCase atualizarStatusPedidoUseCase(
            PedidoRepositoryPort pedidoRepository) {
        return new AtualizarStatusPedidoUseCaseImpl(pedidoRepository);
    }

    @Bean
    public ListarPedidosUseCase listarPedidosUseCase(
            PedidoRepositoryPort pedidoRepository) {
        return new ListarPedidosUseCaseImpl(pedidoRepository);
    }

    @Bean
    public ConsultarStatusPagamentoUseCase consultarStatusPagamentoUseCase(
            PagamentoRepositoryPort pagamentoRepository,
            PedidoRepositoryPort pedidoRepository) {
        return new ConsultarStatusPagamentoUseCaseImpl(pagamentoRepository, pedidoRepository);
    }

    @Bean
    public ProcessarWebhookPagamentoUseCase processarWebhookPagamentoUseCase(
            PedidoRepositoryPort pedidoRepository,
            PagamentoRepositoryPort pagamentoRepository) {
        return new ProcessarWebhookPagamentoUseCaseImpl(pedidoRepository, pagamentoRepository);
    }
}
