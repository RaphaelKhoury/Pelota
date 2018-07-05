package ca.uqac.info.pelota.verification;


import java.util.HashSet;
import java.util.LinkedList;

public class BigWedgeDynamicState extends DynamicState{

	
	private LinkedList<DynamicState> m_children;
	
	BigWedgeDynamicState()
	{
		m_children=new LinkedList<DynamicState>();		
	}

	BigWedgeDynamicState(LinkedList<DynamicState> c)
	{
		m_children=c;		
	}

	BigWedgeDynamicState( DynamicState dyn1, DynamicState dyn2, DynamicState dyn3)
	{
		m_children=new LinkedList<DynamicState>();		
		this.add(dyn1);
		this.add(dyn2);
		this.add(dyn3);
	}
	
	protected int getSize()
	{
		return m_children.size(); 
	}
	
	//merges dyn into the current dynamic state.
	//This does the same as add, but checks the size of the input to make sure that the smallest is added into the biggest 
	//and not the reverse. Also  it returns a BigWedgeObject, rather than simple working on the this pointer.  
	protected BigWedgeDynamicState  merge(DynamicState dyn)
	{
		if(dyn instanceof BigWedgeDynamicState)
		{
			if(this.getSize() >=((BigWedgeDynamicState) dyn).getSize() )
			{	
				add((BigWedgeDynamicState) dyn);
				return this;
			}
			else 
			{
				((BigWedgeDynamicState) dyn).add(this);
				return (BigWedgeDynamicState) dyn;
			}	
		}	
		else if(dyn instanceof BinaryAndDynamicState)
		{
			add(((BinaryAndDynamicState) dyn).getDestination1());
			add(((BinaryAndDynamicState) dyn).getDestination2());
			return this;
		}
		else
		{
			add(dyn);
			return this;
		}
		
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


	
	//adds a dynamic state
	//is it possible to add the biggest into the smallest
	void add(DynamicState dyn)
	{	
	
		if(dyn instanceof BigWedgeDynamicState)
		{
	
			for (DynamicState s : ((BigWedgeDynamicState) dyn).m_children)
			{
				add(s);
			}
		}
		else if(dyn instanceof BinaryAndDynamicState)
		{	 
			add(((BinaryAndDynamicState) dyn).getDestination1());
			add(((BinaryAndDynamicState) dyn).getDestination2());
		}
		else if(!(dyn instanceof TopDynamicState) && !(this.contains(dyn)) )//not true and not already present, add 
		{
			m_children.add(dyn);
		}
	}
	
	
	public LinkedList<DynamicState> getChildren()
	{
		return m_children;
	}
	
	public void print() {
		
		System.out.println("(AND size: " + m_children.size());
		for (DynamicState s : m_children)
		{
			System.out.println(",");
			s.print();
		}

		System.out.println(")END AND");
	}

	public int size() {
		int s=0;
		for(int i=0;i<m_children.size();i++)
		{
			s=s+m_children.get(i).size();
		}
		
		return s;
	}
	
	//equals is used for simplifying complex evaluations.
	//it seems unlikely that the gains from recursively examining
	//the inside of this structure will outweighted the cost
	public boolean equals(DynamicState d) {

		return false;
	}

}
