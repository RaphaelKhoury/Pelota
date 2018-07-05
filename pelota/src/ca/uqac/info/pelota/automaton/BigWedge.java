package ca.uqac.info.pelota.automaton;

public class BigWedge extends Node {


	protected String m_variable;
	private String m_path;
	protected  Node m_outgoing;
	
	BigWedge()
	{
		name=AutomatonLTLFO.stateCounter++;
	}


	
	void print()
	{
		System.out.println("BigWedgeState : " + name + " "  + m_variable);
		System.out.println(name +" -> "+ m_outgoing.name);
		//outgoing.print(name);
	}




	public String getPath() {
		return m_path;
	}

	public void setPath(String path) {
		this.m_path = path;
	}

	public String getVariable() {
		return m_variable;
	}

	public void setVariable(String variable) {
		this.m_variable = variable;
	}

	
	public Node getOutgoing() {
		return m_outgoing;
	}

	public void setOutgoing(Node outgoing) {
		this.m_outgoing = outgoing;
	}
		
	
	
}
