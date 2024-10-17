package br.com.valueprojects.mock_spring.model;

import infra.JogoDao;
import br.com.valueprojects.mock_spring.service.SMSService;

import java.util.Calendar;
import java.util.List;

public class FinalizaJogo {

	private final JogoDao dao;
	private final SMSService smsService;
	private int total = 0;

	public FinalizaJogo(JogoDao dao, SMSService smsService) {
		this.dao = dao;
		this.smsService = smsService;
	}

	public void finaliza() {
		List<Jogo> todosJogosEmAndamento = dao.emAndamento();

		for (Jogo jogo : todosJogosEmAndamento) {
			if (iniciouSemanaAnterior(jogo)) {
				jogo.finaliza();
				total++;
				dao.atualiza(jogo);
				smsService.enviarSMS(jogo.getGanhador().getParticipante().getCelular(), "Parabéns, você venceu no jogo: " + jogo.getDescricao());
			}
		}
	}

	private boolean iniciouSemanaAnterior(Jogo jogo) {
		return diasEntre(jogo.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}
		return diasNoIntervalo;
	}

	public int getTotalFinalizados() {
		return total;
	}
}