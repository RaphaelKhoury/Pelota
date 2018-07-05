package ca.uqac.info.pelota.automaton;

public class BinaryAndTrans  extends  Transition{


	private Node m_destination1;
	private Node m_destination2;
	private int  m_origin;
	
	
	public BinaryAndTrans(int o){
		
		name=AutomatonLTLFO.stateCounter++;
		setOrigin(o);
	}
	
	public BinaryAndTrans(Node left, Node right) {
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
		System.out.println( getOrigin() +  "-> " + m_destination1.name + " and ");
		System.out.println( getOrigin() +  "-> " + m_destination2.name);
	}

	public int getOrigin() {
		return m_origin;
	}

	public void setOrigin(int o) {
		this.m_origin = o;
	}



}
