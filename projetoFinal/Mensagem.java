package projetoFinal;

public class Mensagem {
	
	private String metodo;
	private String requestResponsePayload;
	
	public Mensagem(String metodo, String requestResponsePayload) {
		super();
		this.metodo = metodo;
		this.requestResponsePayload = requestResponsePayload;
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	public String getRequestResponsePayload() {
		return requestResponsePayload;
	}

	public void setRequestResponsePayload(String requestResponsePayload) {
		this.requestResponsePayload = requestResponsePayload;
	}
	
}
