package ca.uqac.info.pelota.automaton;

import ca.uqac.info.pelota.core.util;

public class EvaluationState extends Node {
	
	
	//private String m_label;  //useful for testing
	//TODO: make predicates final 
	private Predicate m_leftPredicate;
	private Predicate m_rightPredicate;
	private String m_operator;  
	
	private Node ifCase;
	private Node elseCase;
	
	
	
	public EvaluationState(String l, String o, String r)
	{

		//m_label=l+o+r;
		
		m_leftPredicate= _CreatePredicate(l);
		m_operator =o;
		m_rightPredicate = _CreatePredicate(r);
		
		
		name=AutomatonLTLFO.stateCounter++;
	}
	
	//Reads a string from the formula and creates a predicate of the approproate type
	private Predicate _CreatePredicate(String s)
	{
		if(util.isInteger(s)) //if the value is an integer
			return  new ConstantPredicate(Integer.parseInt(s));
		else 
			return new StringPredicate(s);
	}
	
	public Predicate getLeftPredicate() {
		return m_leftPredicate;
	}

	
	public Predicate  getRightPredicate() {
		return m_rightPredicate;
	}


	public String getOperator() {
		return m_operator;
	}

	public void setOperator(String operator) {
		this.m_operator = operator;
	}

	public Node getIfCase() {
		return ifCase;
	}

	public void setIfCase(Node ifCase) {
		this.ifCase = ifCase;
	}

	public Node getElseCase() {
		return elseCase;
	}

	public void setElseCase(Node elseCase) {
		this.elseCase = elseCase;
	}

	
	void print() {
		//System.out.println("Eval state " + m_label + " name : " +  getName());
		System.out.println(getName() + " -> " + ifCase.getName());
		System.out.println(getName() + " -> " + elseCase.getName());
		
	}
	
}
