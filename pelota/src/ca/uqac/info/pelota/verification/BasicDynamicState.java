package ca.uqac.info.pelota.verification;

import java.util.ArrayList;

import ca.uqac.info.pelota.automaton.Node;

public class BasicDynamicState extends DynamicState{

	private Node m_currentState;
	private ArrayList<Valuation> m_variables;
	
	
	BasicDynamicState(Node startState)
	{
		setCurrentState(startState);
		m_variables= new ArrayList<Valuation>(Verification.nbOfQtfVaribles);
	}
	
	//copy constructor
	BasicDynamicState(BasicDynamicState ds)
	{
		setCurrentState(ds.m_currentState);
		m_variables= new ArrayList<Valuation>();
		for(int i=0;i<ds.m_variables.size();i++)
		{
			m_variables.add(ds.m_variables.get(i).getcopy());
		}	
	}

	public Node getCurrentState() {
		return m_currentState;
	}
	
	
	public void addValuation(Valuation v)
	{
		m_variables.add(v);
	}
	
	public int getVariableCount()
	{
		return m_variables.size();
	}
	
	
	public boolean containsValuation(Valuation v)
	{
		for(int i=0;i<m_variables.size();i++)
		{
			if(m_variables.get(i).equals(v))
				return true;
		}
		
		return false;
	}

	public void setCurrentState(Node node) {
		this.m_currentState = node;
	}
	

	public boolean containsVariable(String varName)
	{
		for(int i=0;i<m_variables.size();i++)
		{
			if(m_variables.get(i).getName().equals(varName))
				return true;
		}
		
		return false;
	}
	
	public Valuation getVariablesValuation(String varName)
	{
		for(int i=0;i<m_variables.size();i++)
		{
			if(m_variables.get(i).getName().equals(varName))
				return m_variables.get(i);
		}		
		return null;
	}

	
	public void print() {
		System.out.println("currentState :" + m_currentState.getName() + " vars: ");
		for(int i =0;i<m_variables.size();i++)
			m_variables.get(i).print();
	}

	public int size() {
		return 1;
	}

	public boolean equals(DynamicState d) {
		
		if(!(d instanceof BasicDynamicState))
			return false;
		else if (m_currentState != ((BasicDynamicState) d).getCurrentState())
			return false;
		else if (m_variables.size() !=   ((BasicDynamicState) d).getVariableCount())
			return false;
		
		for(int i=0; i<m_variables.size();i++)
		{
			if(!(((BasicDynamicState) d).containsValuation(m_variables.get(i)) ))
				return false;
		}
		return true;
	}
	
	
}
