package edu.unifaj.ppi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unifaj.ppi.dto.request.AtualizarPerfilRequest;
import edu.unifaj.ppi.dto.request.CadastroDoadorRequest;
import edu.unifaj.ppi.dto.response.DoadorResponse;
import edu.unifaj.ppi.dto.response.PerfilResponse;
import edu.unifaj.ppi.exception.ConflitoException;
import edu.unifaj.ppi.exception.CredenciaisInvalidasException;
import edu.unifaj.ppi.exception.RecursoNaoEncontradoException;
import edu.unifaj.ppi.exception.RegraDeNegocioException;
import edu.unifaj.ppi.model.Doador;
import edu.unifaj.ppi.model.enums.FatorRh;
import edu.unifaj.ppi.model.enums.TipoSanguineo;
import edu.unifaj.ppi.repository.BolsaSangueRepository;
import edu.unifaj.ppi.repository.DoadorRepository;
import edu.unifaj.ppi.validation.ConversorTipoSanguineo;
import edu.unifaj.ppi.validation.Validador;

@Service
public class DoadorService {

	private static final String MENSAGEM_SENHA_PARCIAL = "Preencha a senha atual e a nova senha";
	private static final String MENSAGEM_SENHA_ATUAL = "Senha atual incorreta";

	private final DoadorRepository doadorRepository;
	private final BolsaSangueRepository bolsaSangueRepository;
	private final PasswordEncoder passwordEncoder;
	private final Validador validador;
	private final ConversorTipoSanguineo conversorTipo;

	public DoadorService(DoadorRepository doadorRepository, BolsaSangueRepository bolsaSangueRepository,
			PasswordEncoder passwordEncoder, Validador validador, ConversorTipoSanguineo conversorTipo) {
		this.doadorRepository = doadorRepository;
		this.bolsaSangueRepository = bolsaSangueRepository;
		this.passwordEncoder = passwordEncoder;
		this.validador = validador;
		this.conversorTipo = conversorTipo;
	}

	/**
	 * Mesma ordem de validacao da tela de cadastro do app, para que a primeira
	 * regra violada seja sempre a mesma.
	 */
	@Transactional
	public DoadorResponse cadastrar(CadastroDoadorRequest request) {
		validador.validarCamposObrigatorios(request.nome(), request.email(), request.senha(), request.cpf(),
				request.tipoSanguineo());
		validador.validarEmail(request.email());
		validador.validarSenha(request.senha());
		validador.validarCpf(request.cpf());
		conversorTipo.validarTipoSanguineo(request.tipoSanguineo());

		String cpf = somenteDigitos(request.cpf());
		String email = request.email().trim().toLowerCase();

		if (doadorRepository.existsByEmailIgnoreCase(email)) {
			throw new ConflitoException("Este email já está cadastrado");
		}
		if (doadorRepository.existsByCpf(cpf)) {
			throw new ConflitoException("Este CPF já está cadastrado");
		}

		TipoSanguineo tipo = conversorTipo.paraTipo(request.tipoSanguineo());
		FatorRh fatorRh = conversorTipo.paraFatorRh(request.tipoSanguineo());

		Doador doador = new Doador(request.nome().trim(), email, passwordEncoder.encode(request.senha()), cpf, tipo,
				fatorRh);
		return DoadorResponse.from(doadorRepository.save(doador));
	}

	/**
	 * A mesma mensagem para email inexistente e senha errada: responder de forma
	 * diferente transformaria o login num oraculo de quais emails existem.
	 */
	@Transactional(readOnly = true)
	public DoadorResponse login(String email, String senha) {
		Doador doador = doadorRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
				.orElseThrow(CredenciaisInvalidasException::new);
		if (!passwordEncoder.matches(senha == null ? "" : senha, doador.getSenha())) {
			throw new CredenciaisInvalidasException();
		}
		return DoadorResponse.from(doador);
	}

	@Transactional(readOnly = true)
	public PerfilResponse buscarPerfil(String cpf) {
		Doador doador = buscarDoador(cpf);
		return PerfilResponse.from(DoadorResponse.from(doador), (int) bolsaSangueRepository.countByAgendamentoDoadorCpf(doador.getCpf()));
	}

	@Transactional
	public PerfilResponse atualizarPerfil(String cpf, AtualizarPerfilRequest request) {
		Doador doador = buscarDoador(cpf);

		doador.setNome(request.nome().trim());
		atualizarEmail(doador, request.email());
		atualizarSenha(doador, request);

		// CPF e tipo nunca sao tocados: o CPF e a chave que liga o doador aos
		// agendamentos, entao edita-lo deixaria os agendamentos antigos orfaos.
		Doador salvo = doadorRepository.save(doador);
		return PerfilResponse.from(DoadorResponse.from(salvo),
				(int) bolsaSangueRepository.countByAgendamentoDoadorCpf(salvo.getCpf()));
	}

	private void atualizarEmail(Doador doador, String email) {
		String normalizado = email.trim().toLowerCase();
		if (doadorRepository.existsByEmailIgnoreCaseAndIdNot(normalizado, doador.getId())) {
			throw new ConflitoException("Este email já está cadastrado");
		}
		doador.setEmail(normalizado);
	}

	/**
	 * Dois campos em branco mantem a senha; apenas um preenchido e erro. A
	 * senha atual e conferida com o BCrypt, nao por igualdade de texto.
	 */
	private void atualizarSenha(Doador doador, AtualizarPerfilRequest request) {
		String senhaAtual = request.senhaAtual();
		String novaSenha = request.novaSenha();

		boolean querTrocar = temConteudo(senhaAtual) || temConteudo(novaSenha);
		if (!querTrocar) {
			return;
		}
		if (!temConteudo(senhaAtual) || !temConteudo(novaSenha)) {
			throw new RegraDeNegocioException(MENSAGEM_SENHA_PARCIAL);
		}
		if (!passwordEncoder.matches(senhaAtual, doador.getSenha())) {
			throw new RegraDeNegocioException(MENSAGEM_SENHA_ATUAL);
		}
		validador.validarSenha(novaSenha);
		doador.setSenha(passwordEncoder.encode(novaSenha));
	}

	private Doador buscarDoador(String cpf) {
		String normalizado = cpf == null ? "" : somenteDigitos(cpf);
		return doadorRepository.findByCpf(normalizado)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado"));
	}

	private boolean temConteudo(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}

	private String somenteDigitos(String cpf) {
		return cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
	}

}