package br.com.valueprojects.mock_spring.model;

import br.com.valueprojects.mock_spring.infra.JogoDao;
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
        Calendar hoje = Calendar.getInstance();

        for (Jogo jogo : todosJogosEmAndamento) {
            if (iniciouSemanaAnterior(jogo, hoje)) {
                jogo.finaliza();
                total++;
                boolean updated = dao.atualiza(jogo);
                if (updated)
                    smsService.enviarSMS(jogo.getGanhador().getParticipante().getCelular(), "Parabéns, você venceu no jogo: " + jogo.getDescricao());
                else
                    System.err.println("Falha ao atualizar o jogo: " + jogo.getDescricao());
            }
        }
    }

    private boolean iniciouSemanaAnterior(Jogo jogo, Calendar hoje) {

        int jogoAno = jogo.getData().get(Calendar.YEAR);
        int jogoSemana = jogo.getData().get(Calendar.WEEK_OF_YEAR);

        int hojeAno = hoje.get(Calendar.YEAR);
        int hojeSemana = hoje.get(Calendar.WEEK_OF_YEAR);

        return hojeAno > jogoAno || (hojeAno == jogoAno && hojeSemana > jogoSemana);

    }

    public int getTotalFinalizados() {
        return total;
    }
}