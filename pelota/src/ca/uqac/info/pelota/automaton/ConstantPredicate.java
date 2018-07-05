package ca.uqac.info.pelota.automaton;
//If an integer constant occurs in the formula

public class ConstantPredicate extends Predicate{
	
	
	private final int value;

	
	ConstantPredicate(int v){
		
		value=v;
	}


	public int getValue() {
		return value;
	}



}
