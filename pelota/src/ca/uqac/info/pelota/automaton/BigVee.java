//As a single successor, creates a disjonctive next state for every value in the message that
//matches the given label
package ca.uqac.info.pelota.automaton;

public class BigVee extends Node {

	protected String m_variable;
	private String m_path;
	protected  Node m_outgoing;
	
	public BigVee()
	{
		name=AutomatonLTLFO.stateCounter++;
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
	
	void print()
	{
		System.out.println("BigVeeState name : " + name + " "  + m_variable);
		System.out.println(name +" -> "+ m_outgoing.name);
		//outgoing.print(name);
	}



}
