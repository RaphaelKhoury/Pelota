//This abstract class is the top-level class for the hierarchy containing all the types of nodes that occur in an
//automaton. 
//It generalizes states (formulas, bot and top), conditionals and quantifiers
//It also generalizes transition because transitions can be strung together
package ca.uqac.info.pelota.automaton;


public abstract class Node {

	
	protected int name;

	abstract void print();

	public int getName(){
		return name;
	}

}
