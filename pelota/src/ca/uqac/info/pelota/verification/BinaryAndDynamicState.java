package ca.uqac.info.pelota.verification;

public class BinaryAndDynamicState extends DynamicState {

	private DynamicState m_destination1;
	private DynamicState m_destination2;
	
	public BinaryAndDynamicState(DynamicState d1, DynamicState d2) {		
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
	
	//equals is used for simplifying complex evaluations.
	//it seems unlikely that the gains from recursively examining
	//the inside of this structure will outweighted the cost
	public boolean equals(DynamicState d) {

		return false;
	}

	
	//assumes dyn cannot be BigWedge
	protected DynamicState merge(DynamicState dyn)
	{
		if(dyn instanceof BinaryAndDynamicState)
			return merge2BinaryAndStates(this, (BinaryAndDynamicState) dyn);
		else if(dyn.equals(this.getDestination1()) || dyn.equals(this.getDestination2()))
			return this;
		else //create a new BigWedge with all three values, no further need to test, anything
			return new BigWedgeDynamicState( this.getDestination1(),  this.getDestination2(), dyn); 
			
	}
	

	//merges two 2BinaryAnd dynamic states. Assumes each does not contains duplicates but tothergehther they may contain duplicates
	static private DynamicState merge2BinaryAndStates(BinaryAndDynamicState  dyn1,BinaryAndDynamicState  dyn2)
	{
		if( !(dyn2.getDestination1().equals(dyn1.getDestination1()))  && !(dyn2.getDestination1().equals(dyn1.getDestination2()))  )
		{
			BigWedgeDynamicState dyn= new BigWedgeDynamicState(dyn1.getDestination1(),dyn1.getDestination2(),dyn2.getDestination1());
			dyn.add(dyn2.getDestination2());
			return dyn;
		}	
		else if( !(dyn2.getDestination2().equals(dyn1.getDestination1()))  && !(dyn2.getDestination2().equals(dyn1.getDestination2())) )
		{
			BigWedgeDynamicState dyn= new BigWedgeDynamicState(dyn1.getDestination1(),dyn1.getDestination2(),dyn2.getDestination2());
			return dyn;
		}	
		
		return dyn1;
	}
	
	public void print() {
		System.out.println("(");
		m_destination1.print();
		System.out.println("and");
		m_destination2.print();
		System.out.println(")");
	}

	public int size() {
		return  m_destination1.size()+ m_destination2.size();
	}
	
}
