package telegrambot.cineranza.classes;

public class Film {
	
	private String name;
	private boolean estrenoNacional;
	private int edadMinima;
	private boolean cine;		// true - Invierno  false - Verano

	public Film() {}

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
	
}
