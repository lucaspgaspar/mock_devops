
package br.com.valueprojects.mock_spring.business;


import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Resultado;

public class Juiz {
	
	private double maisPontos = Double.NEGATIVE_INFINITY;
	private Resultado ganhador = null;
	private double menosPontos = Double.POSITIVE_INFINITY;
	




	public void julga(Jogo jogo){
		if(jogo.getResultados().size()==0){
			throw new RuntimeException("Sem resultados não há julgamento!");
			}
		for(Resultado resultado : jogo.getResultados()){
			if(resultado.getMetrica() > maisPontos){
				maisPontos = resultado.getMetrica();
				ganhador = resultado;
			}
			if(resultado.getMetrica() < menosPontos) menosPontos = resultado.getMetrica();

	     }

		jogo.setGanhador(ganhador);
	}
		
	public double getPrimeiroColocado(){
			
			return maisPontos;
		}
		
   public double getUltimoColocado(){
			
			return menosPontos;
		}

}
