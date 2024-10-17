package br.com.valueprojects.mock_spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import br.com.valueprojects.mock_spring.model.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.service.SMSService;
import infra.JogoDao;

public class FinalizaJogoTest {

	private JogoDao jogoDao;
	private SMSService smsService;
	private FinalizaJogo finalizaJogo;

	@BeforeEach
	void setup() {
		jogoDao = mock(JogoDao.class);  // Mock do DAO
		smsService = mock(SMSService.class);  // Mock do serviço de SMS
		finalizaJogo = new FinalizaJogo(jogoDao, smsService);  // Injeção de dependências
	}

	@Test
	public void deveFinalizarJogosDaSemanaAnterior() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Jogo jogo1 = new CriadorDeJogo().para("Caça moedas").naData(antiga).constroi();
		Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();

		List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

		when(jogoDao.emAndamento()).thenReturn(jogosAnteriores);

		finalizaJogo.finaliza();

		assertTrue(jogo1.isFinalizado());
		assertTrue(jogo2.isFinalizado());
		assertEquals(2, finalizaJogo.getTotalFinalizados());
	}

	@Test
	public void deveVerificarSeMetodoAtualizaFoiInvocado() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Jogo jogo1 = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
		Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();

		List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

		when(jogoDao.emAndamento()).thenReturn(jogosAnteriores);

		finalizaJogo.finaliza();

		verify(jogoDao).atualiza(jogo1);
	}

	@Test
	void deveSalvarJogosFinalizadosEEnviarSMS() {
		Calendar dataAnterior = Calendar.getInstance();
		dataAnterior.add(Calendar.DAY_OF_MONTH, -8);

		Jogo jogo1 = new CriadorDeJogo().para("Jogo Teste 1").naData(dataAnterior).constroi();
		Jogo jogo2 = new CriadorDeJogo().para("Jogo Teste 2").naData(dataAnterior).constroi();

		List<Jogo> jogosEmAndamento = Arrays.asList(jogo1, jogo2);

		when(jogoDao.emAndamento()).thenReturn(jogosEmAndamento);

		finalizaJogo.finaliza();

		verify(jogoDao).atualiza(jogo1);
		verify(jogoDao).atualiza(jogo2);
		verify(smsService, times(2)).enviarSMS(anyString(), anyString());
	}

	@Test
	void naoDeveEnviarSMSSemSalvarJogos() {
		when(jogoDao.emAndamento()).thenReturn(new ArrayList<>());

		finalizaJogo.finaliza();

		verifyNoInteractions(smsService);
	}
}
