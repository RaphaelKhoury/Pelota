package ca.uqac.info.pelota.automaton;

/**A string that occurs in the formula 
 * Can be a varialbe or a Xpath*/

public class StringPredicate extends Predicate {
	
	private final String value;
	private final boolean isPath;
	
	StringPredicate(String v){
		
		value=v;
		if (v.contains(("/")))
			isPath=true;
		else 
			isPath=false;
	}

	public String getValue() {
		return value;
	}

	public boolean isPath() {
		return isPath;
	}


	
	

}
