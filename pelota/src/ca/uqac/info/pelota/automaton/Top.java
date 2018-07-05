package ca.uqac.info.pelota.automaton;

public final class Top extends State {
	
	Top()
	{
		name=AutomatonLTLFO.stateCounter++;
	}

	void print()
	{
		System.out.println("TOP "+ "name : " +  getName());
	}
	

}
