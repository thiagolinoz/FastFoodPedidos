package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.exception.ClienteNaoCadastradoException;
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
            throw new ClienteNaoCadastradoException("CPF não informado");
        }

        try {
            log.info("Verificando se CPF existe na API de pessoas: {}", cpf);

            PessoaExternaDTO pessoa = pessoaFeignClient.buscarPessoaPorCpf(cpf);

            if (pessoa == null) {
                log.warn("CPF {} não encontrado na API de pessoas", cpf);
                throw new ClienteNaoCadastradoException(cpf);
            }

            log.info("CPF {} encontrado na API de pessoas", cpf);

        } catch (FeignException.NotFound e) {
            log.warn("CPF não encontrado na API de pessoas: {}", cpf);
            throw new ClienteNaoCadastradoException(cpf);
        } catch (FeignException e) {
            log.error("Erro ao verificar CPF {} na API de pessoas: Status {} - {}", cpf, e.status(), e.getMessage());
            throw new RuntimeException("Erro ao comunicar com serviço de pessoas: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao verificar CPF {}: {}", cpf, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao verificar CPF: " + e.getMessage(), e);
        }
    }
}

