package br.com.valueprojects.mock_spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import br.com.valueprojects.mock_spring.business.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.service.SMSService;
import br.com.valueprojects.mock_spring.repository.JogoDao;
import org.mockito.InOrder;

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

    private List<Jogo> CriaJogos() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(2024, Calendar.OCTOBER, 15);

        Calendar hoje = Calendar.getInstance();

        Participante joao = new Participante(1, "Joao", "1111111");
        Participante ana = new Participante(2, "Ana", "22222222");
        Participante roberto = new Participante(3, "Roberto", "33333333");
        Participante lucas = new Participante(3, "Lucas", "44444444");

        Jogo jogo1 = new CriadorDeJogo().para("Caça moedas").naData(antiga).constroi();
        jogo1.anota(new Resultado(joao, 10));
        jogo1.anota(new Resultado(ana, 115));
        jogo1.anota(new Resultado(roberto, 70));
        jogo1.anota(new Resultado(lucas, 90));
        Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();
        jogo2.anota(new Resultado(joao, 100));
        jogo1.anota(new Resultado(ana, 80));
        jogo2.anota(new Resultado(roberto, 70));
        jogo2.anota(new Resultado(lucas, 90));
        Jogo jogo3 = new CriadorDeJogo().para("Esconde Esconde").naData(hoje).constroi();
        jogo2.anota(new Resultado(joao, 0));
        jogo1.anota(new Resultado(ana, 15));
        jogo2.anota(new Resultado(roberto, 65));
        jogo2.anota(new Resultado(lucas, 10));

        List<Jogo> jogos = Arrays.asList(jogo1, jogo2, jogo3);

        return jogos;
    }

    @Test
    public void deveFinalizarJogosDaSemanaAnterior() {

        List<Jogo> jogos = CriaJogos();
        when(jogoDao.emAndamento()).thenReturn(jogos);
        when(jogoDao.atualiza(any(Jogo.class))).thenReturn(true);

        finalizaJogo.finaliza();

        assertTrue(jogos.get(0).isFinalizado());
        assertTrue(jogos.get(1).isFinalizado());
        assertFalse(jogos.get(2).isFinalizado());
        assertEquals(2, finalizaJogo.getTotalFinalizados());
        assertEquals("Ana", jogos.get(0).getGanhador().getParticipante().getNome());
        assertEquals("Joao", jogos.get(1).getGanhador().getParticipante().getNome());

        verify(jogoDao).atualiza(jogos.get(0));
        verify(jogoDao).atualiza(jogos.get(1));
    }

    @Test
    public void deveVerificarSeMetodoAtualizaFoiInvocado() {

        List<Jogo> jogos = CriaJogos();

        when(jogoDao.emAndamento()).thenReturn(jogos);
        when(jogoDao.atualiza(any(Jogo.class))).thenReturn(true);

        finalizaJogo.finaliza();

        verify(jogoDao).atualiza(jogos.get(0));
        verify(jogoDao).atualiza(jogos.get(1));
    }

    @Test
    void deveSalvarJogosFinalizadosEEnviarSMS() {

        List<Jogo> jogos = CriaJogos();

        when(jogoDao.emAndamento()).thenReturn(jogos);
        when(jogoDao.atualiza(any(Jogo.class))).thenReturn(true);

        finalizaJogo.finaliza();

        verify(jogoDao).atualiza(jogos.get(0));
        verify(jogoDao).atualiza(jogos.get(1));
        verify(smsService, times(2)).enviarSMS(anyString(), anyString());
    }

    @Test
    public void deveGarantirQueSMSSoEhEnviadoAposAtualizacao() {

        List<Jogo> jogos = CriaJogos();

        when(jogoDao.emAndamento()).thenReturn(jogos);
        when(jogoDao.atualiza(any(Jogo.class))).thenReturn(true);

        finalizaJogo.finaliza();

        verify(jogoDao).atualiza(jogos.get(0));
        verify(jogoDao).atualiza(jogos.get(1));

        InOrder inOrder = inOrder(jogoDao, smsService);
        inOrder.verify(jogoDao).atualiza(jogos.get(0));
        inOrder.verify(smsService).enviarSMS(anyString(), anyString());
    }

    @Test
    void naoDeveEnviarSMSSemSalvarJogos() {
        List<Jogo> jogos = CriaJogos();
        when(jogoDao.emAndamento()).thenReturn(jogos);
        when(jogoDao.atualiza(any(Jogo.class))).thenReturn(false);

        finalizaJogo.finaliza();

        verifyNoInteractions(smsService);
    }
}
