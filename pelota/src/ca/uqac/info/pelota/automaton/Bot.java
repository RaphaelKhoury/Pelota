package ca.uqac.info.pelota.automaton;

public final class Bot extends State  {

	Bot()
	{
		name=AutomatonLTLFO.stateCounter++;
	}
	void print()
	{
		System.out.println("BOT "+ "name : " +  getName());
	}
	
}
