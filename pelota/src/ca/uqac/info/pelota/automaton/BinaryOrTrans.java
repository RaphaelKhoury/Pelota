package ca.uqac.info.pelota.automaton;

public class BinaryOrTrans  extends  Transition{


	private Node m_destination1;
	private Node m_destination2;
	private int  m_origin;
	
	public BinaryOrTrans(int o){
		
		name=AutomatonLTLFO.stateCounter++;
		m_origin =o;
	}
	
	public BinaryOrTrans(Node left, Node right) {
		m_destination1=left;
		m_destination2=right;
	
		name=AutomatonLTLFO.stateCounter++;
	}
	public Node getDestination1() {
		return m_destination1;
	}
	public void setDestination1(Node d) {
		m_destination1 = d;
	}
	public Node getDestination2() {
		return m_destination2;
	}
	public void setDestination2(Node d) {
		m_destination2 = d;
	}

	protected void print()
	{
		System.out.println( m_origin +  "-> " + m_destination1.getName() + " or " + "("+ name + ")" );
		System.out.println( m_origin +  "-> " + m_destination2.getName());
	}

	public void setOrigin(int n) {
		m_origin =n;
	}



}
