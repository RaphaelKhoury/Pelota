package ca.uqac.info.pelota.automaton;

public final class FormulaState extends State {

	private String m_label="";  //The formula contained in this label.
	
	private Transition outgoing;
	
	public FormulaState(String l)
	{	m_label=l;
		name=AutomatonLTLFO.stateCounter++;
	}

	public Transition getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(Transition outgoing) {
		this.outgoing = outgoing;
	}

	
	void print() {
		System.out.println("Form. " + m_label + " name : " +  getName());
		//outgoing.print(name);
	}
	
}
