//if a given token is not in the message then  left else rigth
//First of the two nodes generates when a quatifyer is encontered
package ca.uqac.info.pelota.automaton;

public class Conditional extends Node {

	private String m_token;
	
	//Should be a binaryTransition
	private Node ifNotPresent;
	private Node ifPresent;
	
	private String m_variable;
	private String m_path;
	
	public Conditional(String t)
	{
		m_token=t;
		name=AutomatonLTLFO.stateCounter++;
	}

	public Node getIfNotPresent() {
		return ifNotPresent;
	}

	public void setIfNotPresent(Node ifNotPresent) {
		this.ifNotPresent = ifNotPresent;
	}

	public Node getIfPresent() {
		return ifPresent;
	}

	public void setIfPresent(Node ifPresent) {
		this.ifPresent = ifPresent;
	}

	
	void print() {
		System.out.println("Cond. "  + m_token  + " name : " +  getName());
		System.out.println("if Not Present " + getName() + "-> " +  ifNotPresent.getName() );
		System.out.println("if Present " + getName() + "-> " +  ifPresent.getName() );
		
		
	}

	public String getVariable() {
		return m_variable;
	}

	public void setVariable(String variable) {
		this.m_variable = variable;
	}

	public String getPath() {
		return m_path;
	}

	public void setPath(String path) {
		this.m_path = path;
	}
	
	
}
