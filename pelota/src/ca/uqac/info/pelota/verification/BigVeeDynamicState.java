package ca.uqac.info.pelota.verification;


import java.util.HashSet;
import java.util.LinkedList;

public class BigVeeDynamicState extends DynamicState {
	
	private LinkedList<DynamicState> m_children;
	
	BigVeeDynamicState()
	{
		m_children=new LinkedList<DynamicState>();		
	}

	
	BigVeeDynamicState( DynamicState dyn1, DynamicState dyn2, DynamicState dyn3)
	{
		m_children=new LinkedList<DynamicState>();		
		this.add(dyn1);
		this.add(dyn2);
		this.add(dyn3);
	}
	
	BigVeeDynamicState(LinkedList<DynamicState> c)
	{
		m_children=c;		
	}
	

	
	protected int getSize()
	{
		return m_children.size(); 
	}
	
	//checks if one of the children contained in m_children is equal to dyn
	private boolean contains(DynamicState dyn)
	{		
		for (DynamicState s : m_children)
		{
			if(s.equals(dyn))
				return true; 
		}
		
		return false;
	}
	
	
	
	//merges dyn into the current dynamic state.
	//does the same as add but checks wich bigVee is the biggest one, to avoid adding the smallest into the biggest. 
	//Also differs from add in that it returns a objects instead of the this pointer
	protected BigVeeDynamicState  merge(DynamicState dyn)
	{
		if(dyn instanceof BigVeeDynamicState)
		{
			if(this.getSize() >=((BigVeeDynamicState) dyn).getSize() )
			{	
				add((BigVeeDynamicState) dyn);
				return this;
			}
			else 
			{
				((BigVeeDynamicState) dyn).add(this);
				return (BigVeeDynamicState) dyn;
			}	
		}	
		else if(dyn instanceof BinaryOrDynamicState)
		{
			addBinaryOr((BinaryOrDynamicState) dyn);
			return this;
		}
		else
		{
			add(dyn);
			return this;
		}
		
	}
	
	
	//adds a binaryOr to the current BigVee
	protected void addBinaryOr(BinaryOrDynamicState dyn)
	{
		add(dyn.getDestination1());
		add(dyn.getDestination2());
	}
	



	public LinkedList<DynamicState> getChildren()
	{
		return m_children;
	}
	
	public void print() {
		System.out.println("(OR size: " + m_children.size());
		for (DynamicState s : m_children) {
		    s.print();
			System.out.println("OR");

		}		
		System.out.println(")");
	}
	
	//adds a dynamic state
	//this could go in a superclass that BigVee and BigWedge inherit from
	//if its a BasicDynamicState, it verifies that it is not already present in the children array
	//adds a dynamic state
	void add(DynamicState dyn)
	{	
		if(dyn instanceof BigVeeDynamicState)
		{
			for (DynamicState s : ((BigVeeDynamicState) dyn).m_children)
			{
				add(s);
			}
		}
		else if(dyn instanceof BinaryOrDynamicState)
		{	 
			add(((BinaryOrDynamicState) dyn).getDestination1());
			add(((BinaryOrDynamicState) dyn).getDestination2());
		}
		else if(!(dyn instanceof BotDynamicState) && !(this.contains(dyn)) )//not true and not already present, add 
		{
			m_children.add(dyn);
		}
	}
	
	
	
	
	//equals is used for simplifying complex evaluations.
	//it seems unlikely that the gains from recursively examining
	//the inside of this structure will outweighted the cost
	public boolean equals(DynamicState d) {

		return false;
	}



	public int size() {
		int s=0;
		for(int i=0;i<m_children.size();i++)
		{
			s=s+m_children.get(i).size();
		}
		
		return s;
	}

	
	
}

