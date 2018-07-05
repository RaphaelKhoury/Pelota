package ca.uqac.info.pelota.verification;

public class BinaryOrDynamicState extends DynamicState{
	

	private DynamicState m_destination1;
	private DynamicState m_destination2;
	
	public BinaryOrDynamicState(DynamicState d1, DynamicState d2) {
		m_destination1=d1;
		m_destination2=d2;
	}

	public DynamicState getDestination1() {
		return m_destination1;
	}
	
	public void setDestination1(DynamicState destination1) {
		this.m_destination1 = destination1;
	}

	public DynamicState getDestination2() {
		return m_destination2;
	}

	public void setDestination2(DynamicState destination2) {
		this.m_destination2 = destination2;
	}

	//assumes dyn cannot be BigVee
	protected DynamicState merge(DynamicState dyn)
	{
		if(dyn instanceof BinaryOrDynamicState)
			return merge2BinaryOrStates(this, (BinaryOrDynamicState) dyn);
		else 
			return mergeBinOrStates(this, dyn);
	}
		
	//Merge a dynamic state into a binary and (checks identity)
	static protected DynamicState mergeBinOrStates(BinaryOrDynamicState  dyn1, DynamicState  dyn2)
	{
			
		if(dyn2.equals(dyn1.getDestination1()) || dyn2.equals(dyn1.getDestination2()))
			return dyn1;
		else //create a new BigVee with all three values, no further need to test, anything
			return new BigVeeDynamicState(dyn1.getDestination1(), dyn1.getDestination2(), dyn2);
				
	}
		
		//merges two 2BinaryOr dynamic states. Assumes each does not contains duplicates but togehther they may contain duplicates
		static private  DynamicState merge2BinaryOrStates(BinaryOrDynamicState  dyn1,BinaryOrDynamicState  dyn2)
		{
					
			if( !(dyn2.getDestination1().equals(dyn1.getDestination1()))  
					&& !(dyn2.getDestination1().equals(dyn1.getDestination2()))  )
				
			{
				BigVeeDynamicState dyn= new BigVeeDynamicState(dyn1.getDestination1(),dyn1.getDestination2(),dyn2.getDestination1());
				dyn.add(dyn2.getDestination2());
				return dyn;
			}	
			else if( !(dyn2.getDestination2().equals(dyn1.getDestination1()))  && !(dyn2.getDestination2().equals(dyn1.getDestination2())) )
			{
				BigVeeDynamicState dyn= new BigVeeDynamicState(dyn1.getDestination1(),dyn1.getDestination2(),dyn2.getDestination2());
				return dyn;
			}	
			
			return dyn1;
		}	
	
	public int size() {
			
		return  m_destination1.size()+ m_destination2.size();
	}	
	
	public void print() {
		System.out.println("(");
		m_destination1.print();
		System.out.println("or");
		m_destination2.print();
		System.out.println(")");
	}

	//equals is used for simplifying complex evaluations.
	//it seems unlikely that the gains from recursively examining
	//the inside of this structure will outweighted the cost
	public boolean equals(DynamicState d) {

		return false;
	}

	
}
