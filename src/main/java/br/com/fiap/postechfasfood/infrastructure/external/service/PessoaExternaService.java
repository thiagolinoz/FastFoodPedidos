package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.exception.PessoaNaoEncontradaException;
import br.com.fiap.postechfasfood.infrastructure.external.client.PessoaFeignClient;
import br.com.fiap.postechfasfood.infrastructure.external.dto.PessoaExternaDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PessoaExternaService {

    private final PessoaFeignClient pessoaFeignClient;

    public void verificarSeCpfExiste(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            log.info("CPF não informado - pedido anônimo");
            return;
        }

        try {
            log.info("Verificando se CPF existe na API de pessoas: {}", cpf);

            PessoaExternaDTO pessoa = pessoaFeignClient.buscarPessoaPorCpf(cpf);

            if (pessoa == null) {
                log.warn("CPF {} - API retornou null", cpf);
                throw new PessoaNaoEncontradaException("CPF " + cpf + " não encontrado");
            }

            log.info("CPF {} - Pessoa encontrada (campos padrão): CPF={}, Nome={}, Email={}, Ativo={}",
                cpf, pessoa.getCpf(), pessoa.getNome(), pessoa.getEmail(), pessoa.getAtivo());
            log.info("CPF {} - Pessoa encontrada (campos alternativos): CdDoc={}, NmPessoa={}, DsEmail={}, SnAtivo={}",
                cpf, pessoa.getCdDocPessoa(), pessoa.getNmPessoa(), pessoa.getDsEmail(), pessoa.getSnAtivo());

            if (pessoa.isEmpty()) {
                log.error("CPF {} - API externa retornou objeto completamente vazio! Todos os campos são null.", cpf);
                log.error("CPF {} - PROBLEMA NA API EXTERNA: Verifique o mapeamento JSON ou a configuração da API", cpf);

                log.warn("CPF {} - APLICANDO WORKAROUND: Assumindo pessoa ativa devido ao problema da API externa", cpf);
                return;
            }

            String cpfRetornado = pessoa.getCpfUnificado();
            String nomeRetornado = pessoa.getNomeUnificado();
            String emailRetornado = pessoa.getEmailUnificado();

            log.info("CPF {} - Dados unificados: CPF_Real={}, Nome={}, Email={}",
                cpf, cpfRetornado, nomeRetornado, emailRetornado);

            boolean pessoaAtiva = pessoa.isAtiva();
            log.info("CPF {} - Resultado isAtiva(): {}", cpf, pessoaAtiva);

            boolean temDadosValidos = cpfRetornado != null && nomeRetornado != null;
            Boolean ativo = pessoa.getAtivo();
            Boolean snAtivo = pessoa.getSnAtivo();
            boolean ambosCamposAtivoNull = (ativo == null && snAtivo == null);

            if (ambosCamposAtivoNull && temDadosValidos) {
                log.warn("CPF {} - Campos ativo são null mas temos dados válidos, tratando como inativo", cpf);
                throw new PessoaNaoEncontradaException("Pessoa com CPF " + cpf + " não está ativa");
            }

            if (!pessoaAtiva) {
                String motivo = ambosCamposAtivoNull ? "status indefinido (null)" : "inativa (false)";
                log.warn("CPF {} encontrado mas pessoa está {} na API de pessoas", cpf, motivo);
                throw new PessoaNaoEncontradaException("Pessoa com CPF " + cpf + " não está ativa");
            }

            log.info("CPF {} encontrado na API de pessoas e está ativo - VALIDAÇÃO OK!", cpf);

        } catch (PessoaNaoEncontradaException e) {
            log.warn("CPF {} - PessoaNaoEncontradaException: {}", cpf, e.getMessage());
            throw e;
        } catch (FeignException.NotFound e) {
            log.warn("CPF {} - API retornou 404 NotFound: {}", cpf, e.getMessage());
            throw new PessoaNaoEncontradaException("CPF " + cpf + " não encontrado na API externa", e);
        } catch (FeignException e) {
            log.error("CPF {} - Erro Feign na API de pessoas: Status={}, Message={}",
                cpf, e.status(), e.getMessage());
            throw new RuntimeException("Erro ao comunicar com serviço de pessoas: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("CPF {} - Erro inesperado: Tipo={}, Message={}",
                cpf, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Erro ao comunicar com serviço de pessoas: " + e.getMessage(), e);
        }
    }
}

