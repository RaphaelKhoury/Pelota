package ca.uqac.info.pelota.automaton;

//When converting an LTL-FO+ formula to an automaton, an operator type was present in the formula, 
//for which no translation to automaton is available. The formula can be restated using identities.
public class UnhandledOperator extends Exception {

	private static final long serialVersionUID = 0L;   
	
	public UnhandledOperator(ca.uqac.info.pelota.ltl.Operator o){
		  System.out.println("Operator: " + o.getClass().getName() + " " + o.toString() );
	   }
}
