package telegrambot.cineranza.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Film {
	
	private String name;
	private boolean estrenoNacional;
	private int edadMinima;
	private boolean cine;		// true - Invierno  false - Verano
	private List<Date> horario;

	public Film() {
		this.horario = new ArrayList<Date>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEstrenoNacional() {
		return estrenoNacional;
	}

	public void setEstrenoNacional(boolean estrenoNacional) {
		this.estrenoNacional = estrenoNacional;
	}

	public int getEdadMinima() {
		return edadMinima;
	}

	public void setEdadMinima(int edadMinima) {
		this.edadMinima = edadMinima;
	}

	public boolean getCine() {
		return cine;
	}

	public void setCine(boolean cine) {
		this.cine = cine;
	}

	public List<Date> getHorario() {
		return horario;
	}

	public void setHorario(List<Date> horario) {
		this.horario = horario;
	}
	
}
