package br.com.valueprojects.mock_spring.service;

public class SMSService {

    // Método para enviar SMS
    public void enviarSMS(String celular, String mensagem) {
        System.out.println("Enviando SMS pra: " + celular + " / Mensagem: " + mensagem);
        // Aqui poderia estar a lógica real de envio de SMS
    }
}