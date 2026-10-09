'use strict';

const $ = (sel) => document.querySelector(sel);
const state = { cpf: localStorage.getItem('ppi.cpf') };

/* ------------------------------------------------------------------ */
/* Infra                                                              */
/* ------------------------------------------------------------------ */

async function api(path, options = {}) {
	const resp = await fetch(path, {
		headers: { 'Content-Type': 'application/json' },
		...options,
	});
	if (resp.status === 204) return null;
	const texto = await resp.text();
	const corpo = texto ? JSON.parse(texto) : null;
	if (!resp.ok) {
		const detalhe = corpo && (corpo.detail || corpo.message) ? (corpo.detail || corpo.message) : 'Erro inesperado';
		throw new Error(detalhe);
	}
	return corpo;
}

function esc(v) {
	return String(v == null ? '' : v).replace(/[&<>"']/g, (c) => (
		{ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
	));
}

function toast(msg) {
	const el = $('#toast');
	el.textContent = msg;
	el.classList.remove('hidden');
	clearTimeout(toast._t);
	toast._t = setTimeout(() => el.classList.add('hidden'), 3000);
}

function fmtData(iso) {
	if (!iso) return '—';
	const [a, m, d] = iso.split('-');
	return `${d}/${m}/${a}`;
}

function fmtHora(h) {
	return h ? String(h).slice(0, 5) : '—';
}

function erro(el, e) {
	el.textContent = e.message;
	el.classList.remove('hidden');
}

/* ------------------------------------------------------------------ */
/* Autenticação                                                       */
/* ------------------------------------------------------------------ */

document.querySelectorAll('.tab[data-auth]').forEach((tab) => {
	tab.addEventListener('click', () => {
		document.querySelectorAll('.tab[data-auth]').forEach((t) => t.classList.remove('active'));
		tab.classList.add('active');
		const alvo = tab.dataset.auth;
		$('#form-login').classList.toggle('hidden', alvo !== 'login');
		$('#form-cadastro').classList.toggle('hidden', alvo !== 'cadastro');
		$('#auth-erro').classList.add('hidden');
	});
});

$('#form-login').addEventListener('submit', async (ev) => {
	ev.preventDefault();
	const dados = Object.fromEntries(new FormData(ev.target));
	try {
		const doador = await api('/api/doadores/login', { method: 'POST', body: JSON.stringify(dados) });
		entrar(doador);
	} catch (e) {
		erro($('#auth-erro'), e);
	}
});

$('#form-cadastro').addEventListener('submit', async (ev) => {
	ev.preventDefault();
	const dados = Object.fromEntries(new FormData(ev.target));
	try {
		const doador = await api('/api/doadores', { method: 'POST', body: JSON.stringify(dados) });
		entrar(doador);
	} catch (e) {
		erro($('#auth-erro'), e);
	}
});

function entrar(doador) {
	state.cpf = doador.cpf;
	localStorage.setItem('ppi.cpf', doador.cpf);
	$('#login-view').classList.add('hidden');
	$('#topbar').classList.remove('hidden');
	$('#app-view').classList.remove('hidden');
	$('#user-nome').textContent = doador.nome;
	atualizarBadge();
	navegar('home');
}

$('#btn-sair').addEventListener('click', () => {
	state.cpf = null;
	localStorage.removeItem('ppi.cpf');
	$('#app-view').classList.add('hidden');
	$('#topbar').classList.add('hidden');
	$('#login-view').classList.remove('hidden');
});

async function atualizarBadge() {
	try {
		const { total } = await api(`/api/doadores/${state.cpf}/notificacoes/nao-lidas`);
		const badge = $('#badge-nao-lidas');
		badge.textContent = total;
		badge.classList.toggle('hidden', total === 0);
	} catch (_) { /* silencioso */ }
}

/* ------------------------------------------------------------------ */
/* Navegação                                                          */
/* ------------------------------------------------------------------ */

document.querySelectorAll('.nav[data-page]').forEach((btn) => {
	btn.addEventListener('click', () => navegar(btn.dataset.page));
});

const PAGINAS = {
	home: renderHome,
	agendar: renderAgendar,
	agendamentos: renderAgendamentos,
	historico: renderHistorico,
	estoque: renderEstoque,
	triagem: renderTriagem,
	notificacoes: renderNotificacoes,
	perfil: renderPerfil,
};

async function navegar(pagina) {
	document.querySelectorAll('.nav[data-page]').forEach((b) => {
		b.classList.toggle('active', b.dataset.page === pagina);
	});
	const content = $('#content');
	content.innerHTML = '<p class="muted">Carregando…</p>';
	try {
		await PAGINAS[pagina](content);
	} catch (e) {
		content.innerHTML = `<div class="erro">${esc(e.message)}</div>`;
	}
}

/* ------------------------------------------------------------------ */
/* Páginas                                                            */
/* ------------------------------------------------------------------ */

async function renderHome(content) {
	const perfil = await api(`/api/doadores/${state.cpf}`);
	const bolsas = await api(`/api/bolsas-sangue?cpf=${state.cpf}`);
	const estoqueCritico = await api('/api/estoque/criticos');
	content.innerHTML = `
		<h2>Olá, ${esc(perfil.doador.nome)}</h2>
		<div class="grid cols">
			<div class="item">
				<h3>Nível de doador</h3>
				<p class="muted">${perfil.registros} de ${perfil.limite} registros</p>
				<div class="progress"><span style="width:${perfil.percentual}%"></span></div>
				<p>${perfil.nivelMaximo ? '<span class="pill ok">Nível máximo</span>' : esc(perfil.percentual + '%')}</p>
			</div>
			<div class="item">
				<h3>Tipo sanguíneo</h3>
				<p class="pill">${esc(perfil.doador.tipoCompleto || '—')}</p>
			</div>
			<div class="item">
				<h3>Doações registradas</h3>
				<p class="pill ok">${bolsas.length}</p>
			</div>
			<div class="item">
				<h3>Estoque crítico</h3>
				<p class="pill ${estoqueCritico.length ? 'critico' : 'ok'}">${estoqueCritico.length} tipo(s)</p>
			</div>
		</div>`;
}

async function renderAgendar(content) {
	const hemocentros = await api('/api/hemocentros');
	content.innerHTML = `
		<h2>Novo agendamento</h2>
		<form class="form" id="form-agendar">
			<label>Hemocentro
				<select name="hemocentroId" required>
					<option value="">Selecione</option>
					${hemocentros.map((h) => `<option value="${esc(h.id)}">${esc(h.nome)}</option>`).join('')}
				</select>
			</label>
			<label>Data <input type="date" name="data" required></label>
			<label>Hora <input type="time" name="hora" required></label>
			<button type="submit" class="primary">Agendar</button>
		</form>`;
	$('#form-agendar').addEventListener('submit', async (ev) => {
		ev.preventDefault();
		const dados = Object.fromEntries(new FormData(ev.target));
		try {
			await api(`/api/agendamentos?cpf=${state.cpf}`, { method: 'POST', body: JSON.stringify(dados) });
			toast('Agendamento criado');
			navegar('agendamentos');
		} catch (e) {
			toast(e.message);
		}
	});
}

async function renderAgendamentos(content) {
	const lista = await api(`/api/agendamentos?cpf=${state.cpf}`);
	const elegiveis = await api(`/api/agendamentos/para-registro?cpf=${state.cpf}`);
	const cartao = (a, permiteColeta) => `
		<div class="item">
			<h3>${esc(a.hemocentro.nome)}</h3>
			<div class="row"><span>${fmtData(a.data)} às ${fmtHora(a.hora)}</span>
				<span class="pill ${a.status === 'CANCELADO' ? 'critico' : a.status === 'REALIZADO' ? 'ok' : 'alerta'}">${esc(a.statusDescricao)}</span></div>
			${a.quantidadeBolsas != null ? `<div class="row muted"><span>${a.quantidadeBolsas} bolsa(s) registradas</span></div>` : ''}
			<div class="actions">
				${permiteColeta ? `<button class="primary" data-coleta="${a.id}">Registrar coleta</button>` : ''}
				${(a.status === 'PENDENTE' || a.status === 'CONFIRMADO') ? `<button class="secondary" data-cancelar="${a.id}">Cancelar</button>` : ''}
			</div>
		</div>`;
	content.innerHTML = `
		<h2>Agendamentos</h2>
		${lista.length ? `<div class="grid cols">${lista.map((a) => cartao(a, false)).join('')}</div>` : '<div class="empty">Nenhum agendamento.</div>'}
		<h2>Prontos para registrar coleta</h2>
		${elegiveis.length ? `<div class="grid cols">${elegiveis.map((a) => cartao(a, true)).join('')}</div>` : '<div class="empty">Nenhum agendamento elegível.</div>'}`;

	content.querySelectorAll('[data-cancelar]').forEach((b) => b.addEventListener('click', async () => {
		try {
			await api(`/api/agendamentos/${b.dataset.cancelar}/cancelar?cpf=${state.cpf}`, { method: 'POST' });
			toast('Agendamento cancelado');
			navegar('agendamentos');
		} catch (e) { toast(e.message); }
	}));

	content.querySelectorAll('[data-coleta]').forEach((b) => b.addEventListener('click', async () => {
		const volume = prompt('Volume por bolsa (200–470 ml):', '450');
		if (volume === null) return;
		const quantidade = prompt('Quantidade de bolsas (1–6):', '1');
		if (quantidade === null) return;
		try {
			await api(`/api/bolsas-sangue/agendamentos/${b.dataset.coleta}/coletas?cpf=${state.cpf}`, {
				method: 'POST',
				body: JSON.stringify({ volumeMl: Number(volume), quantidade: Number(quantidade) }),
			});
			toast('Coleta registrada');
			navegar('agendamentos');
		} catch (e) { toast(e.message); }
	}));
}

async function renderHistorico(content) {
	const bolsas = await api(`/api/bolsas-sangue?cpf=${state.cpf}`);
	content.innerHTML = `
		<h2>Histórico de coletas</h2>
		${bolsas.length ? `<div class="grid cols">${bolsas.map((b) => `
			<div class="item">
				<h3>${esc(b.codigo)}</h3>
				<div class="row"><span class="pill">${esc(b.tipoCompleto)}</span><span class="pill ${b.status === 'VENCIDA' ? 'critico' : 'ok'}">${esc(b.statusDescricao)}</span></div>
				<div class="row muted"><span>Coleta: ${fmtData(b.dataColeta)}</span><span>Validade: ${fmtData(b.dataValidade)}</span></div>
				<div class="row muted"><span>${b.quantidade} bolsa(s) · ${b.volumeMl}ml</span><span>${esc(b.hemocentroOrigem.nome)}</span></div>
			</div>`).join('')}</div>` : '<div class="empty">Nenhuma coleta registrada.</div>'}`;
}

async function renderEstoque(content) {
	const [estoque, criticos] = await Promise.all([api('/api/estoque'), api('/api/estoque/criticos')]);
	content.innerHTML = `
		<h2>Estoque de sangue</h2>
		<p class="muted">${criticos.length} combinação(ões) no limite mínimo.</p>
		${estoque.length ? `<div class="grid cols">${estoque.map((e) => `
			<div class="item">
				<h3>${esc(e.tipoCompleto)} · ${esc(e.hemocentro.nome)}</h3>
				<div class="row"><span class="pill ${e.critico ? 'critico' : 'ok'}">${e.quantidadeDisponivel} bolsa(s)</span><span class="muted">mínimo ${e.quantidadeMinima}</span></div>
				<div class="actions">
					<button class="secondary" data-minima="${e.id}" data-atual="${e.quantidadeMinima}">Ajustar mínimo</button>
				</div>
			</div>`).join('')}</div>` : '<div class="empty">Estoque vazio — registre coletas.</div>'}`;

	content.querySelectorAll('[data-minima]').forEach((b) => b.addEventListener('click', async () => {
		const valor = prompt('Novo mínimo:', b.dataset.atual);
		if (valor === null) return;
		try {
			await api(`/api/estoque/${b.dataset.minima}`, {
				method: 'PUT',
				body: JSON.stringify({ quantidadeMinima: Number(valor) }),
			});
			toast('Mínimo atualizado');
			navegar('estoque');
		} catch (e) { toast(e.message); }
	}));
}

async function renderTriagem(content) {
	const triagens = await api(`/api/doadores/${state.cpf}/triagens`);
	content.innerHTML = `
		<h2>Triagem clínica</h2>
		<form class="form" id="form-triagem">
			<label>Data <input type="date" name="data" required></label>
			<label>Peso (kg) <input type="number" step="0.1" name="pesoKg" required></label>
			<label>Pressão arterial <input name="pressaoArterial" placeholder="120/80" required></label>
			<label>Hemoglobina (g/dL) <input type="number" step="0.1" name="hemoglobina" required></label>
			<label>Observações <input name="observacoes"></label>
			<button type="submit" class="primary">Registrar triagem</button>
		</form>
		<h2>Histórico de triagens</h2>
		${triagens.length ? `<div class="grid cols">${triagens.map((t) => `
			<div class="item">
				<h3>${fmtData(t.data)}</h3>
				<div class="row"><span class="pill ${t.apto ? 'ok' : 'critico'}">${t.apto ? 'Apto' : 'Inapto'}</span><span class="muted">${t.pesoKg}kg · Hb ${t.hemoglobina}</span></div>
				<div class="row muted"><span>PA ${esc(t.pressaoArterial)}</span>${t.motivoInaptidao ? `<span>${esc(t.motivoInaptidao)}</span>` : ''}</div>
			</div>`).join('')}</div>` : '<div class="empty">Nenhuma triagem registrada.</div>'}`;

	$('#form-triagem').addEventListener('submit', async (ev) => {
		ev.preventDefault();
		const f = Object.fromEntries(new FormData(ev.target));
		const corpo = {
			data: f.data,
			pesoKg: Number(f.pesoKg),
			pressaoArterial: f.pressaoArterial,
			hemoglobina: Number(f.hemoglobina),
			observacoes: f.observacoes || null,
		};
		try {
			const r = await api(`/api/doadores/${state.cpf}/triagens`, { method: 'POST', body: JSON.stringify(corpo) });
			toast(r.apto ? 'Doador apto' : 'Doador inapto: ' + r.motivoInaptidao);
			navegar('triagem');
		} catch (e) { toast(e.message); }
	});
}

async function renderNotificacoes(content) {
	const lista = await api(`/api/doadores/${state.cpf}/notificacoes`);
	content.innerHTML = `
		<h2>Notificações</h2>
		${lista.length ? `<div class="grid">${lista.map((n) => `
			<div class="item">
				<div class="row"><h3>${esc(n.titulo)}</h3><span class="pill ${n.lida ? '' : 'alerta'}">${n.lida ? 'Lida' : 'Nova'}</span></div>
				<p class="muted">${esc(n.mensagem)}</p>
				${n.lida ? '' : `<button class="secondary" data-ler="${n.id}">Marcar como lida</button>`}
			</div>`).join('')}</div>` : '<div class="empty">Nenhuma notificação.</div>'}`;

	content.querySelectorAll('[data-ler]').forEach((b) => b.addEventListener('click', async () => {
		try {
			await api(`/api/doadores/${state.cpf}/notificacoes/${b.dataset.ler}/ler`, { method: 'POST' });
			atualizarBadge();
			navegar('notificacoes');
		} catch (e) { toast(e.message); }
	}));
}

async function renderPerfil(content) {
	const perfil = await api(`/api/doadores/${state.cpf}`);
	content.innerHTML = `
		<h2>Perfil</h2>
		<form class="form" id="form-perfil">
			<label>Nome <input name="nome" value="${esc(perfil.doador.nome)}" required></label>
			<label>E-mail <input type="email" name="email" value="${esc(perfil.doador.email)}" required></label>
			<label>CPF <input value="${esc(perfil.doador.cpf)}" disabled></label>
			<label>Tipo sanguíneo <input value="${esc(perfil.doador.tipoCompleto || '')}" disabled></label>
			<label>Senha atual <input type="password" name="senhaAtual"></label>
			<label>Nova senha <input type="password" name="novaSenha"></label>
			<button type="submit" class="primary">Salvar</button>
		</form>`;
	$('#form-perfil').addEventListener('submit', async (ev) => {
		ev.preventDefault();
		const f = Object.fromEntries(new FormData(ev.target));
		const corpo = { nome: f.nome, email: f.email, senhaAtual: f.senhaAtual || null, novaSenha: f.novaSenha || null };
		try {
			await api(`/api/doadores/${state.cpf}`, { method: 'PUT', body: JSON.stringify(corpo) });
			toast('Perfil atualizado');
		} catch (e) { toast(e.message); }
	});
}

/* ------------------------------------------------------------------ */
/* Bootstrap                                                          */
/* ------------------------------------------------------------------ */

(async function iniciar() {
	if (!state.cpf) return;
	try {
		const perfil = await api(`/api/doadores/${state.cpf}`);
		entrar(perfil.doador);
	} catch (_) {
		localStorage.removeItem('ppi.cpf');
		state.cpf = null;
	}
})();
