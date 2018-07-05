package ca.uqac.info.pelota.verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import ca.uqac.info.pelota.automaton.AutomatonLTLFO;
import ca.uqac.info.pelota.automaton.BigVee;
import ca.uqac.info.pelota.automaton.BigWedge;
import ca.uqac.info.pelota.automaton.BinaryAndTrans;
import ca.uqac.info.pelota.automaton.BinaryOrTrans;
import ca.uqac.info.pelota.automaton.Bot;
import ca.uqac.info.pelota.automaton.Conditional;
import ca.uqac.info.pelota.automaton.ConstantPredicate;
import ca.uqac.info.pelota.automaton.EvaluationState;
import ca.uqac.info.pelota.automaton.FormulaState;
import ca.uqac.info.pelota.automaton.Predicate;
import ca.uqac.info.pelota.automaton.StringPredicate;
import ca.uqac.info.pelota.automaton.Top;
import ca.uqac.info.pelota.core.util;
import ca.uqac.info.pelota.xml.Element;
import ca.uqac.info.pelota.xml.Event;
import ca.uqac.info.pelota.xml.SimpleXPathExpression;
import ca.uqac.info.pelota.xml.Event.EvaluationException;

public class Verification {
	 static protected int nbOfQtfVaribles;
	 
	 //these two local top and bots can be reused accross several parts of the dynamic state formula, reducing memory use. 
	 static private final  TopDynamicState top= new TopDynamicState();
	 static private final BotDynamicState bot= new BotDynamicState(); 
	 
	 //this could be moved to an interface eventually
	 static private Hashtable<String, List<Value>>  ValuesInMessage= new Hashtable<String, List<Value>> ();
	 
	
	public static BasicDynamicState initialiazeVerification(AutomatonLTLFO automaton )
	{
		nbOfQtfVaribles=automaton.getNumberOfVariables();
		return new BasicDynamicState(automaton.startState);
	}
	
	//must reset the hashtable on receipt of each new message
	public static void resetHashTable()
	{
		
		//printHashTable();
		ValuesInMessage.clear();
	}
	
	private static void printHashTable()//useful for testing only
	{
		
		System.out.println("Printing the table before deleting it");
		for (String key : ValuesInMessage.keySet()) {
		    System.out.println(key + ":" + ValuesInMessage.get(key));
		}
		System.out.println("fin de la table");
		
	}
	
	
	
	//FormulaState
	static DynamicState evaluateFormulaState(BasicDynamicState currentState, FormulaState nextState, Event e)
	{
		currentState.setCurrentState(nextState);
		return currentState;
	}

	//BinaryAndTrans 
	static DynamicState evaluateBinaryAndTrans(BasicDynamicState currentState, BinaryAndTrans nextState, Event message)
	{
		BasicDynamicState ds1= new BasicDynamicState(currentState);
		BasicDynamicState ds2= new BasicDynamicState(currentState);
		
		ds1.setCurrentState(nextState.getDestination1());
		ds2.setCurrentState(nextState.getDestination2());
		
		
		DynamicState branch1=(recEvaluate(ds1,message));
		DynamicState branch2=(recEvaluate(ds2,message));
		

		/////Simplify//////////// 
		if(branch1 == bot)
			return branch1;
		if(branch2 == bot)
			return branch2;
		if(branch1 == top)
			return branch2;
		if(branch2 == top)
			return branch1;
		///////////////////////////////
		

		DynamicState bs = SimplifyAnd(branch1,branch2);
		return bs;
	}
	
	//BinaryOrTrans
	static DynamicState evaluateBinaryOrTrans(BasicDynamicState currentState, BinaryOrTrans nextState, Event message)
	{
		BasicDynamicState ds1= new BasicDynamicState(currentState);
		BasicDynamicState ds2= new BasicDynamicState(currentState);
		
		ds1.setCurrentState(nextState.getDestination1());
		ds2.setCurrentState(nextState.getDestination2());
		

		DynamicState branch1=(recEvaluate(ds1,message));
		DynamicState branch2=(recEvaluate(ds2,message));
		
		/////Simplify////////////////////////
		if(branch1 == top)
			return branch1;
		if(branch2 == top )
			return branch2;
		if(branch1 == bot)
			return branch2;
		if(branch2 == bot)
			return branch1;
		///////////////////////////////////
		DynamicState bs = SimplifyOr(branch1,branch2);
		return bs;
	}

	//Conditional
	static DynamicState evaluateConditional(BasicDynamicState currentState, Conditional nextState, Event message)
	{

		SimpleXPathExpression exp = SimpleXPathExpression.parse(nextState.getPath());
		List<Value> v = null;
		List<Element> le = null;
		
		if(ValuesInMessage.containsKey(nextState.getPath()) )
			v=ValuesInMessage.get(nextState.getPath());
		else
		{	
			try {
				le = message.evaluate(exp);
				
			} catch (EvaluationException e) {
				
				e.printStackTrace();
			}
			v=_ElementList2ValueList(le);
			ValuesInMessage.put(nextState.getPath(), v);
			
		}
		
		if(v.isEmpty())
			currentState.setCurrentState(nextState.getIfNotPresent());	
		else 
			currentState.setCurrentState(nextState.getIfPresent());
		
		return recEvaluate(currentState, message, v);
	}
	
	static List<Value> _ElementList2ValueList(List<Element> v)
	{
		  
		List<Value> vList= new  ArrayList<Value>(v.size());
		 for (int i=0;i<v.size();i++)
			 vList.add(_Element2Value(v.get(i))); 
	
		 return vList;
	}
	
	
	static Value _Element2Value(Element e)
	{
		Value v=null; 
		if(util.isInteger(e.toString()))//is an integer	 
			v = new IntegerValue(util.efficientParseInt(e.toString()));
		else
			 v= new StringValue(e.toString());
	
		return v;
	}
	
		
	//BigVee
	//req.: the message must contain the token or this state would not be reached by the conditional. 
	//a bloc of code in this method repeats in evaluateBigWedge. place in helper method.
	static DynamicState evaluateBigVee(BasicDynamicState currentState, BigVee nextState, Event message, List<Value> v)
	{
		Valuation valuation;
		
		String varName= nextState.getVariable();
		ArrayList<BasicDynamicState> dsList= new ArrayList<BasicDynamicState>(v.size());  
		LinkedList<DynamicState> dssList;  //needed only for the BigVee case
		
		currentState.setCurrentState(nextState.getOutgoing());
		
	

			for(int i=0;i<v.size();i++)
			{
				BasicDynamicState ds = new BasicDynamicState(currentState);
				valuation=new Valuation(varName, v.get(i));
				ds.addValuation(valuation);
				dsList.add(ds);
			}

		

		//size 1 not possible because its dealt in evaluateBigVeeSingle
		if(v.size()==2)	
			return new BinaryOrDynamicState(recEvaluate(dsList.get(0),message),recEvaluate(dsList.get(1),message));
		
		else 
		{
			dssList= new LinkedList<DynamicState>(); 
			for (BasicDynamicState s : dsList){
				dssList.add(recEvaluate(s,message)); 
			}
			return new BigVeeDynamicState(dssList);
		}
	
	}
	
	//a special version of the BigWedge  to use when there is only one variable in the message 
	private static DynamicState evaluateBigVeeSingle(BasicDynamicState currentState, BigVee nextState, Event message, Value v)
	{
	
		Valuation valuation;
		String varName= nextState.getVariable();
		currentState.setCurrentState(nextState.getOutgoing());		
		
		BasicDynamicState ds = new BasicDynamicState(currentState);
		
		valuation=new Valuation(varName, v);
		ds.addValuation(valuation);
		
		/*if(v instanceof IntegerValue) //if the value is an integer
		{	 
			valuation=new Valuation(varName, v);
			ds.addValuation(valuation);
		}
		else //assumes a String otherwise
		{
				valuation=new  Valuation(varName, v);
				ds.addValuation(valuation);
		}*/
				 
		return recEvaluate(ds,message);		
	}
	
	
	
	//BigWedge
	static DynamicState evaluateBigWedge(BasicDynamicState currentState, BigWedge nextState, Event message, List<Value> v)
	{
		
		Valuation valuation;
		String varName= nextState.getVariable();
		ArrayList<BasicDynamicState> dsList= new ArrayList<BasicDynamicState>(v.size());
		LinkedList<DynamicState> dssList;//needed only in the BigWedge Case
		
		currentState.setCurrentState(nextState.getOutgoing());		
		

			for(int i=0;i<v.size();i++)
			{
				BasicDynamicState ds = new BasicDynamicState(currentState);
				valuation=new Valuation(varName, v.get(i));
				ds.addValuation(valuation);
				dsList.add(ds);
			}

			

		//size 1 not possible because its dealt in evaluateBigVeeSingle
		if(v.size()==2)
		{		
			return new BinaryAndDynamicState(recEvaluate(dsList.get(0),message),recEvaluate(dsList.get(1),message));
		}
		else 
		{	//TODO: Here we create two objects 
			dssList= new LinkedList<DynamicState>();
			for (BasicDynamicState s : dsList)
			{
				dssList.add(recEvaluate(s,message)); 
			}
			return new BigWedgeDynamicState(dssList);
		}
			
	}
	
	//a special version of the BigWedge  to use when there is only one variable in the message 
	private static DynamicState evaluateBigWedgeSingle(BasicDynamicState currentState, BigWedge nextState, Event message, Value v)
	{
	
		Valuation valuation;
		String varName= nextState.getVariable();
		currentState.setCurrentState(nextState.getOutgoing());		
		
		BasicDynamicState ds = new BasicDynamicState(currentState);

		valuation=new Valuation(varName,v);
		ds.addValuation(valuation);
		return recEvaluate(ds,message);	
			
	}
	
	
	
	//EvaluationState
	static DynamicState evaluateEvaluationState(BasicDynamicState currentState, EvaluationState nextState, Event message)
	{
		
		ArrayList<Value> leftPredicate;
		ArrayList<Value> rightPredicate;
		boolean flag=false;
		 
		leftPredicate = getValues(currentState, message, nextState.getLeftPredicate());
		rightPredicate= getValues(currentState, message, nextState.getRightPredicate());
		
		
		if(leftPredicate.isEmpty() || rightPredicate.isEmpty())
			flag=false;
		else 
		{
			for(int i=0;i<leftPredicate.size();i++)
			{
				for(int j=0;j<rightPredicate.size();j++)
				{
					flag=(flag|| evaluateCond(leftPredicate.get(i),nextState.getOperator(), rightPredicate.get(j)) ); 
				}
			}
			
		}	
		if(flag)
			currentState.setCurrentState(nextState.getIfCase()); 
		else 
			currentState.setCurrentState(nextState.getElseCase());

		
		return recEvaluate(currentState, message);
	}
	
	//In some cases it is possible for a variable containing an integer to be compared with a variable containing a string.
	//This occurs; for instance, when a field contains hex values, which may be interpreted as in int or a string depending 
	//on its actual value, as a result of the isInteger fct.
	//in such a case, the int is converted to a string here. 
	private static boolean evaluateCond(Value leftPredicate, String operator, Value rightPredicate)
	{
 
		if(leftPredicate instanceof IntegerValue  &&  rightPredicate instanceof IntegerValue)
			return evaluateCond(((IntegerValue) leftPredicate).getIntValue(), operator, ((IntegerValue) rightPredicate).getIntValue());
		else if  (leftPredicate instanceof StringValue && rightPredicate  instanceof StringValue)
			return evaluateCond(((StringValue) leftPredicate).getStringValue(), operator, ((StringValue) rightPredicate).getStringValue());	
		else
		{
			if(leftPredicate instanceof IntegerValue)
				return evaluateCond(((IntegerValue) leftPredicate).toString(), operator, ((StringValue) rightPredicate).getStringValue());
			else //(rightPredicate instanceof IntegerValue)
				return evaluateCond(((StringValue) leftPredicate).getStringValue(), operator, ((IntegerValue) rightPredicate).toString());	

		}
	}
	
	//assumes the operator is one of the ones in this method
	private static boolean evaluateCond(int leftPredicate, String operator, int rightPredicate)
	{
		if(operator=="=")
		{
			if(leftPredicate==rightPredicate)
				return true;  
			else 
				return false; 
		}	
		else if(operator=="!")
		{
			if(leftPredicate!=rightPredicate)
				return true;  
			else 
				return false; 
		}
		else// if(operator==">"))	
		{
			if(leftPredicate>rightPredicate)
				return true;  
			else 
				return false; 
		}
	}
	
	//assumes the operator is one of the ones in this method
	private static boolean evaluateCond(String leftPredicate, String operator, String rightPredicate)
	{
		if(operator=="=")
		{
			if(leftPredicate.equals(rightPredicate))
				return true;  
			else 
				return false; 
		}	
		else //if(operator=="!")
		{
			if(leftPredicate.equals(rightPredicate))
				return true;  
			else 
				return false; 
		}
	}
	


	
	public static DynamicState evaluate(DynamicState currentState, ca.uqac.info.pelota.xml.Event e)
	{
		if(currentState instanceof BasicDynamicState)
		{//if its a DynamicState, its either a formula state, a top or a bot (or throw an exception)
			if (((BasicDynamicState) currentState).getCurrentState() instanceof FormulaState)
			{	
				((BasicDynamicState) currentState).setCurrentState(((FormulaState) ((BasicDynamicState) currentState).getCurrentState()).getOutgoing());
				return recEvaluate ((BasicDynamicState) currentState, e);
			}
			if(((BasicDynamicState) currentState).getCurrentState() instanceof Top)//never called
				return currentState;
			
			else // if(((BasicDynamicState) currentState).getCurrentState() instanceof Bot)//never called
				return currentState;
		}
		
		else if(currentState instanceof BinaryAndDynamicState)
		{

			DynamicState dyn1= evaluate(((BinaryAndDynamicState) currentState).getDestination1(), e);
			DynamicState dyn2 =evaluate(((BinaryAndDynamicState) currentState).getDestination2(), e);
			
			/////Simplify/////////////////////////
			if(dyn1 == bot)
				return dyn1;
			if(dyn2 == bot)
				return dyn2;
			if(dyn1 == top)
				return dyn2;
			if(dyn2 == top)
				return dyn1;
			///////////////////////////////////
			
			
			((BinaryAndDynamicState) currentState).setDestination1(dyn1);
			((BinaryAndDynamicState) currentState).setDestination2(dyn2);
			
			return SimplifyAnd(dyn1,dyn2);
			//return currentState;
		}
		else if(currentState instanceof BinaryOrDynamicState)
		{
			
			DynamicState dyn1= evaluate(((BinaryOrDynamicState) currentState).getDestination1(), e);
			DynamicState dyn2 =evaluate(((BinaryOrDynamicState) currentState).getDestination2(), e);
			
			
			if(dyn1 == top)
				return dyn1;
			if(dyn2 == top)
				return dyn2;
			if(dyn1 == bot)
				return dyn2;
			if(dyn2 == bot)
				return dyn1;
			if(dyn1.equals(dyn2))
				return dyn1;
			
			((BinaryOrDynamicState) currentState).setDestination1(dyn1);
			
			return SimplifyOr(dyn1,dyn2);
			//return currentState;
		}
		else if(currentState instanceof BigWedgeDynamicState) 
		{
			
			//BigWedgeDynamicState n= new BigWedgeDynamicState(((BigWedgeDynamicState) currentState).getChildren().parallelStream().map(o -> evaluate(o,e)).collect(Collectors.toCollection(HashSet::new)));
																	
			//boolean  idFalsePresent = n.getChildren().parallelStream().anyMatch(dyn -> dyn instanceof BotDynamicState);
			//if(idFalsePresent)
			//	return new BotDynamicState();
			//else 
			//	return n;  
			
			//iterative version of the code above
			
			//this is an older version that creates a new collection each time it is called. 
			/*BigWedgeDynamicState n= new BigWedgeDynamicState(); 
			for (DynamicState s : ((BigWedgeDynamicState) currentState).getChildren()) 
			{
				DynamicState dyn = evaluate(s,e); 
				if(dyn == bot)
					return dyn;
				else 
					n.add(dyn);
					
			}*/
			int size = ((BigWedgeDynamicState) currentState).getSize();
			for (int i = 0; i < size; i++) {
				DynamicState dyn = evaluate( ((BigWedgeDynamicState) currentState).getChildren().poll(),e); 
				if(dyn == bot)
					return dyn;
				else 
					((BigWedgeDynamicState) currentState).add(dyn);   
			}
		
			return currentState; 
			
			
		}
		else if(currentState instanceof BigVeeDynamicState)
		{

			//BigVeeDynamicState n= new BigVeeDynamicState(((BigVeeDynamicState) currentState).getChildren().parallelStream().map(o -> evaluate(o,e)).collect(Collectors.toCollection(HashSet::new)));
			
			//boolean  idTruePresent = n.getChildren().parallelStream().anyMatch(dyn -> dyn instanceof TopDynamicState);
			//if(idTruePresent)
			//	return new TopDynamicState();
			//else 
			//	return n;  
			
			//iterative version of the code above
			/*BigVeeDynamicState n= new BigVeeDynamicState();
			for (DynamicState s : ((BigVeeDynamicState) currentState).getChildren()) 
			{
				DynamicState dyn = evaluate(s,e); 
				if(dyn == top)
					return dyn;
				else 
					n.add(dyn);
			}				
			return n;*/

			int size = ((BigVeeDynamicState) currentState).getSize();
			for (int i = 0; i < size; i++) {
				DynamicState dyn = evaluate( ((BigVeeDynamicState) currentState).getChildren().poll(),e); 
				if(dyn == top)
					return dyn;
				else 
					((BigVeeDynamicState) currentState).add(dyn);   
			}
			return currentState; 
		
		}
		else //if(currentState instanceof TopDynamicState || currentState instanceof BotDynamicState)
		{

			return currentState;
		}

		
		//every case is covered above.
		//return null; 
	}
	
	
	public static DynamicState recEvaluate(BasicDynamicState currentState, Event message, List<Value> v)
	{
		
		if(currentState.getCurrentState() instanceof BigVee)
		{
			if(v.size()==1)
				return evaluateBigVeeSingle(currentState, (BigVee) currentState.getCurrentState(), message,v.get(0));
			else 
				return evaluateBigVee(currentState, (BigVee) currentState.getCurrentState(), message, v);
		}
			
		else if(currentState.getCurrentState() instanceof BigWedge)
		{
			if(v.size()==1)
				return evaluateBigWedgeSingle(currentState, (BigWedge) currentState.getCurrentState(), message,v.get(0));
			else 
				return evaluateBigWedge(currentState, (BigWedge) currentState.getCurrentState(), message,v);
		}

		return recEvaluate(currentState, message);
		
	}
	
	public static DynamicState recEvaluate(BasicDynamicState currentState, Event e)
	{

		
		if(currentState.getCurrentState() instanceof FormulaState)
			return evaluateFormulaState(currentState, (FormulaState) currentState.getCurrentState(), e);
		
		 if(currentState.getCurrentState() instanceof Top)
			return top;  
		
		 if(currentState.getCurrentState() instanceof Bot)
			return bot; 
			
		 if(currentState.getCurrentState() instanceof BinaryAndTrans)
			return evaluateBinaryAndTrans(currentState, (BinaryAndTrans) currentState.getCurrentState(), e);

		 if(currentState.getCurrentState() instanceof BinaryOrTrans)
			return evaluateBinaryOrTrans(currentState, (BinaryOrTrans) currentState.getCurrentState(), e);

		 if(currentState.getCurrentState() instanceof Conditional)  
			return evaluateConditional(currentState, (Conditional) currentState.getCurrentState(), e);
		
		else //if(currentState.getCurrentState() instanceof EvaluationState)
			return evaluateEvaluationState(currentState, (EvaluationState) currentState.getCurrentState(), e);

	}
	
//takes as input the current dynamic state, the current message and a predicate, which occurs in an
//evaluation state of the automaton. 
//it returns a list of  integer values of the predicate.
//The predicate is either a constant ,  a variable in the environment, or Xpath in the current message
//returns null if its a path not present in the current message.
//this method assumes the predicate captures an integer
static  ArrayList<Value> getValues(BasicDynamicState currentState, Event message, Predicate predicate ) 
{
	ArrayList<Value> Values= new ArrayList<Value>();
	
	
	if(predicate instanceof ConstantPredicate)	//predicate is an integer  constant
		Values.add(new IntegerValue(((ConstantPredicate) predicate).getValue()));
		
	else if(currentState.containsVariable(((StringPredicate) predicate).getValue())) //predicate is a variable in the valuation  (representing an int or Sting)
	{
		Value v = currentState.getVariablesValuation(((StringPredicate) predicate).getValue()).getValue();
		Values.add(v);
	}	
	else if (((StringPredicate) predicate).isPath())   //predicate is a path (may be absent  or present, possibly multiple times)
	{
		SimpleXPathExpression exp = SimpleXPathExpression.parse(((StringPredicate) predicate).getValue());///predicate.getValue
		List<Element> v = null;
		try {
			v = message.evaluate(exp);
		} catch (EvaluationException e) {
			
			e.printStackTrace();
		}
		for(int i=0;i<v.size();i++)
		{	
			if (util.isInteger( v.get(0).toString()  ))
				Values.add(new IntegerValue(util.efficientParseInt(v.get(0).toString()  )));
			else //it's a string
				Values.add(new StringValue(v.get(0).toString() ));
		}	
	}
	else// its a string constant
	{
		Values.add(new StringValue(((StringPredicate) predicate).getValue()));   
	}
	
	return Values;
}

////simplifications

private static DynamicState SimplifyAnd(DynamicState branch1, DynamicState  branch2)
{

	/*this is commented out because its tested in every calling location
	//if either is a True or a False or they are the same (basic)
	if(branch1 instanceof TopDynamicState)
		return branch2;
	if(branch2 instanceof TopDynamicState)
		return branch1;
	if(branch1 instanceof BotDynamicState)
		return branch1;
	if(branch2 instanceof BotDynamicState)
		return branch2;
	if(branch1.equals(branch2))
		return branch1;*/
	
	//else, both sides are ands and/or Bigwedges 
	if(branch1 instanceof BinaryAndDynamicState ||  branch1 instanceof BigWedgeDynamicState ||
			branch2 instanceof BinaryAndDynamicState ||  branch2 instanceof BigWedgeDynamicState)
	{
		//a new merge function that will optimize as it merges. 
		return mergeAnd(branch1,branch2); 
	}

	//in all other case, return a BinaryAnd of both
	return new BinaryAndDynamicState(branch1, branch2);
}

protected static DynamicState SimplifyOr(DynamicState branch1, DynamicState  branch2)
{
		
	//if either is a True or a False or they are the same (basic)
	/*Commented out because this verification is made everytime SimplifyOr is called
	if(branch1 instanceof TopDynamicState)
		return branch1;
	if(branch2 instanceof TopDynamicState)
		return branch2;
	if(branch1 instanceof BotDynamicState)
		return branch2;
	if(branch2 instanceof BotDynamicState)
		return branch1;
	if(branch1.equals(branch2))
		return branch1;*/
	
	//else, both sides are ors and/or Bigveeges 
	if(branch1 instanceof BinaryOrDynamicState ||  branch1 instanceof BigVeeDynamicState ||
			branch2 instanceof BinaryOrDynamicState ||  branch2 instanceof BigVeeDynamicState)
	{
		//a new merge function that will optimize as it merges. 
		return mergeOr(branch1,branch2); 
	}

	//in all other case, return a BinaryOr of both
	return new BinaryOrDynamicState(branch1, branch2);
}



	//this does the merge when either one of the two conjunct is an AND or a BigWedge 
	static DynamicState mergeAnd(DynamicState dyn1, DynamicState dyn2)
	{
	
		if(dyn1 instanceof BigWedgeDynamicState)
			return ((BigWedgeDynamicState) dyn1).merge(dyn2);
		else if (dyn2 instanceof BigWedgeDynamicState)		
			return ((BigWedgeDynamicState) dyn2).merge(dyn1);
		else if (dyn1 instanceof BinaryAndDynamicState)
			return ((BinaryAndDynamicState) dyn1).merge(dyn2);
		else  // if (dyn2 instanceof BinaryAndDynamicState)
			return ((BinaryAndDynamicState) dyn2).merge(dyn1);
		
	}


	//this does the merge when either one of the two conjunct is an OR or a BigVee 
	static DynamicState mergeOr(DynamicState dyn1, DynamicState dyn2)
	{
		if(dyn1 instanceof BigVeeDynamicState)
			return ((BigVeeDynamicState) dyn1).merge(dyn2);
		else if (dyn2 instanceof BigVeeDynamicState)		
			return ((BigVeeDynamicState) dyn2).merge(dyn1);
		else if (dyn1 instanceof BinaryOrDynamicState)
			return ((BinaryOrDynamicState) dyn1).merge(dyn2);
		else //if (dyn2 instanceof BinaryOrDynamicState)
			return ((BinaryOrDynamicState) dyn2).merge(dyn1);
		
		//return new BinaryOrDynamicState(dyn1,dyn2);
	}
	
	
	
}//class
	
	

