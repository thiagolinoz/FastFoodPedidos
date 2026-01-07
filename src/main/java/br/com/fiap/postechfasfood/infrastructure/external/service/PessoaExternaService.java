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
                log.info("CPF {} - API retornou null, tratando como pedido anônimo", cpf);
                return;
            }

            log.info("CPF {} - Pessoa encontrada: CPF={}, Nome={}, Email={}, Tipo={}",
                cpf, pessoa.getCdDocPessoa(), pessoa.getNmPessoa(), pessoa.getDsEmail(), pessoa.getTpPessoa());

            // Valida campos obrigatórios (apenas cdDocPessoa é opcional)

            // Nome é obrigatório - não pode ser null nem vazio
            if (pessoa.getNmPessoa() == null || pessoa.getNmPessoa().trim().isEmpty()) {
                log.info("CPF {} - Nome é obrigatório mas está null/vazio, tratando como pedido anônimo", cpf);
                return;
            }

            // Tipo de pessoa é obrigatório - não pode ser null nem vazio
            if (pessoa.getTpPessoa() == null || pessoa.getTpPessoa().trim().isEmpty()) {
                log.info("CPF {} - Tipo de pessoa é obrigatório mas está null/vazio, tratando como pedido anônimo", cpf);
                return;
            }

            // Email é obrigatório - não pode ser null nem vazio
            if (pessoa.getDsEmail() == null || pessoa.getDsEmail().trim().isEmpty()) {
                log.info("CPF {} - Email é obrigatório mas está null/vazio, tratando como pedido anônimo", cpf);
                return;
            }

            // Apenas cdDocPessoa pode ser null (isso é permitido)
            if (pessoa.getCdDocPessoa() == null) {
                log.info("CPF {} - cdDocPessoa é null (permitido), pessoa válida com Nome: {}, Email: {}, Tipo: {}",
                    cpf, pessoa.getNmPessoa(), pessoa.getDsEmail(), pessoa.getTpPessoa());
            }

            log.info("CPF {} encontrado na API de pessoas - VALIDAÇÃO OK!", cpf);

        } catch (PessoaNaoEncontradaException e) {
            log.warn("CPF {} - PessoaNaoEncontradaException: {}", cpf, e.getMessage());
            throw e;
        } catch (FeignException.NotFound e) {
            log.info("CPF {} - API retornou 404 NotFound, tratando como pedido anônimo: {}", cpf, e.getMessage());
            return;
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

