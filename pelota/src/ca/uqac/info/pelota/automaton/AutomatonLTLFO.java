package ca.uqac.info.pelota.automaton;

import java.util.ArrayList;

import ca.uqac.info.pelota.ltl.Operator;
import ca.uqac.info.pelota.ltl.UnaryOperator;

public class AutomatonLTLFO {
	
	
	public Node startState=null;
	ArrayList<Node>  nodes;
	ArrayList<String>  variables;
	static int stateCounter=0; 
	private String m_formula;

	
	//it is useful to have pointers that point to the bot and top states. 
	private Node top;
	private Node bot;
	
	

	
	//This is method Built automata from Lebrun et al.
	public AutomatonLTLFO(ca.uqac.info.pelota.ltl.Operator formula) throws UnhandledOperator
	{
		nodes=new ArrayList<Node>();
		variables = new ArrayList<String>();
		m_formula=formula.toString();
		
		top = new Top();
		nodes.add(top);
		bot = new Bot();
		nodes.add(bot);
			
		buildTransition(formula);  //the init state will always be a State (a formulaState in fact)
	
	}
	
	
	public Node buildTransition(ca.uqac.info.pelota.ltl.Operator phi) throws UnhandledOperator
	{
		
			
		if (phi instanceof ca.uqac.info.pelota.ltl.OperatorTrue) {
			if(phi.toString().equals(m_formula))
				startState=this.getTop();	
			return this.getTop();
		}
		else if ( phi instanceof ca.uqac.info.pelota.ltl.OperatorFalse ) {
			if(phi.toString().equals(m_formula))
				startState=this.getBot();
			return this.getBot();
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorEquals){
			
			String l = ((ca.uqac.info.pelota.ltl.OperatorEquals) phi).getLeft().toString();
			String r =((ca.uqac.info.pelota.ltl.OperatorEquals) phi).getRight().toString();
			EvaluationState n = new EvaluationState(l,"=",r);
			n.setIfCase(this.getTop());
			n.setElseCase(this.getBot());
			
			nodes.add(n);
			if(phi.toString().equals(m_formula))
				startState=n;
			
			return n;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorNotEquals){
			
			String l = ((ca.uqac.info.pelota.ltl.OperatorNotEquals) phi).getLeft().toString();
			String r =((ca.uqac.info.pelota.ltl.OperatorNotEquals) phi).getRight().toString();
			EvaluationState n = new EvaluationState(l,"!=",r);
			n.setIfCase(this.getBot());
			n.setElseCase(this.getTop());
			
			nodes.add(n);
			if(phi.toString().equals(m_formula))
				startState=n;
			
			return n;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorGreaterThan){
			
			String l = ((ca.uqac.info.pelota.ltl.OperatorGreaterThan) phi).getLeft().toString();
			String r =((ca.uqac.info.pelota.ltl.OperatorGreaterThan) phi).getRight().toString();
			EvaluationState n = new EvaluationState(l,">",r);
			n.setIfCase(this.getTop());
			n.setElseCase(this.getBot());
			
			nodes.add(n);
			if(phi.toString().equals(m_formula))
				startState=n;
			return n;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorOr){
			
			Operator left = ((ca.uqac.info.pelota.ltl.OperatorOr) phi).getLeft();
			Operator right = ((ca.uqac.info.pelota.ltl.OperatorOr) phi).getRight();
			BinaryOrTrans t = new BinaryOrTrans(buildTransition(left),buildTransition(right));
			
			nodes.add(t);
			t.setOrigin(t.name); 
			if(phi.toString().equals(m_formula))
				startState=t;
			
			return t;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorAnd){
			
			Operator left = ((ca.uqac.info.pelota.ltl.OperatorAnd) phi).getLeft();
			Operator right = ((ca.uqac.info.pelota.ltl.OperatorAnd) phi).getRight();
			BinaryAndTrans t = new BinaryAndTrans(buildTransition(left),buildTransition(right));
			
			t.setOrigin(t.name); 
			nodes.add(t);
			if(phi.toString().equals(m_formula))
				startState=t;
			
			return t;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.Exists){
			
			Conditional n = new Conditional(((ca.uqac.info.pelota.ltl.Exists) phi).getVariable().toString()); 
			n.setIfNotPresent(this.getBot());
			
			BigVee n2 = new BigVee();
			n2.setOutgoing(buildTransition(((UnaryOperator) phi).getOperand()));
			n.setIfPresent(n2);
			
			n.setVariable( ((ca.uqac.info.pelota.ltl.Exists) phi).getVariable().toString());
			n.setPath(  ((ca.uqac.info.pelota.ltl.Exists) phi).getPath().toString());
			
			n2.setVariable( ((ca.uqac.info.pelota.ltl.Exists) phi).getVariable().toString());
			n2.setPath(  ((ca.uqac.info.pelota.ltl.Exists) phi).getPath().toString());	
			this.variables.add(((ca.uqac.info.pelota.ltl.Exists) phi).getVariable().toString());

			
			nodes.add(n);
			nodes.add(n2);
			
			if(phi.toString().equals(m_formula))
				startState=n;
			
			return n;			
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.ForAll){
			
			Conditional n = new Conditional(((ca.uqac.info.pelota.ltl.ForAll) phi).getVariable().toString()); 
			n.setIfNotPresent(this.getTop());
			
			BigWedge n2 = new BigWedge();
			n2.setOutgoing(buildTransition(((UnaryOperator) phi).getOperand()));
			n.setIfPresent(n2);
			
			n.setVariable( ((ca.uqac.info.pelota.ltl.ForAll) phi).getVariable().toString());
			n.setPath(  ((ca.uqac.info.pelota.ltl.ForAll) phi).getPath().toString());		
			
			n2.setVariable( ((ca.uqac.info.pelota.ltl.ForAll) phi).getVariable().toString());
			n2.setPath(  ((ca.uqac.info.pelota.ltl.ForAll) phi).getPath().toString());		
			this.variables.add(((ca.uqac.info.pelota.ltl.ForAll) phi).getVariable().toString());
			
			nodes.add(n);
			nodes.add(n2);
			
			if(phi.toString().equals(m_formula))
				startState=n;
			return n;			
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorX){
			
			Node n = buildTransition( ((ca.uqac.info.pelota.ltl.OperatorX) phi).getOperand());		
			
			nodes.add(n);
			if(phi.toString().equals(m_formula))
				startState=n;
			return n;			
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorF)
		{
			FormulaState n = new FormulaState(phi.toString());
			BinaryOrTrans t = new BinaryOrTrans(n.getName()); 
			
			t.setDestination1(buildTransition(((ca.uqac.info.pelota.ltl.OperatorF) phi).getOperand()));
			t.setDestination2(n);
			n.setOutgoing(t);
						
			nodes.add(n);
			nodes.add(t);
			if(phi.toString().equals(m_formula))
				startState=n;
			
			return t;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorG)
		{
			FormulaState n = new FormulaState(phi.toString());
			BinaryAndTrans t = new BinaryAndTrans(n.getName()); 
			
			t.setDestination1(buildTransition(((ca.uqac.info.pelota.ltl.OperatorG) phi).getOperand()));
			t.setDestination2(n);
			n.setOutgoing(t);
			
			nodes.add(n);
			nodes.add(t);
			if(phi.toString().equals(m_formula))
				startState=n;
			return t;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorU)
		{
			FormulaState n = new FormulaState(phi.toString());
			
			BinaryOrTrans tOr = new BinaryOrTrans(n.getName());
			BinaryAndTrans tAnd = new BinaryAndTrans(tOr.getName());
			Node mu = buildTransition(((ca.uqac.info.pelota.ltl.OperatorU) phi).getLeft());
			Node nu = buildTransition(((ca.uqac.info.pelota.ltl.OperatorU) phi).getRight());
			
			tOr.setDestination1(nu);
			tOr.setDestination2(tAnd);
			tAnd.setDestination1(mu);
			tAnd.setDestination2(n);
			
			n.setOutgoing(tOr);
			nodes.add(n);
			nodes.add(tOr);
			nodes.add(tAnd);
			if(phi.toString().equals(m_formula))
				startState=n;
			
			return tOr;
		}
		else if (phi instanceof ca.uqac.info.pelota.ltl.OperatorR)
		{
			FormulaState n = new FormulaState(phi.toString());
			
			BinaryAndTrans tAnd= new BinaryAndTrans(  n.getName() );
			BinaryOrTrans tOr = new BinaryOrTrans(tAnd.getName());
			Node mu = buildTransition(((ca.uqac.info.pelota.ltl.OperatorR) phi).getLeft());
			Node nu = buildTransition(((ca.uqac.info.pelota.ltl.OperatorR) phi).getRight());
			
			/*System.out.println("Operator R");
			System.out.println("mu :" );
			mu.print();
			System.out.println("nu :" );
			nu.print();*/

			
			tOr.setDestination1(mu);
			tOr.setDestination2(n);
			tAnd.setDestination1(nu);
			tAnd.setDestination2(tOr);

			
			n.setOutgoing(tAnd);
			nodes.add(n);
			nodes.add(tAnd);
			nodes.add(tOr);
			
			if(phi.toString().equals(m_formula))
				startState=n;
			return tAnd;
		}


		throw new UnhandledOperator(phi);
		//throw an exception with the name of the case
		
	}
	

	public void addNode(Node n)
	{
		nodes.add(n);
	}
	
	public void print()
	{
		//System.out.println("Quatifyed Variables: ");
		//for(int i=0;i<variables.size();i++)
		//	System.out.print(variables.get(i) + " ");
		System.out.println(getNumberOfVariables() + " variables");
		for(int i=0;i<nodes.size();i++)
			nodes.get(i).print();
	}
	
	public void printInOrder()
	{	
		System.out.println("Variables: ");
		for(int i=0;i<variables.size();i++)
			System.out.print(variables.get(i) + " ");
		System.out.println("");
		for(int i=0;i<nodes.size();i++)
		{
			for(int j=0;j<nodes.size();j++)
			{
				if(nodes.get(j).name==i)
					nodes.get(j).print();
			}
		}	
	}

	private Node getTop() {
		return top;
	}


	private Node getBot() {
		return bot;
	}
	
	
	public int getNumberOfVariables()
	{
		return variables.size();
	}

	/*private  void setBot(Node bot) {
		this.bot = bot;
	}*/
	
	/*private void setTop(Node top) {
	this.top = top;
	}*/

}


