//A single Valuation is a pair  Variable-value

package ca.uqac.info.pelota.verification;

public class Valuation {
	

	protected String m_name;
	private  Value m_value;
	
	Valuation(String n, Value v)
	{
		m_name=n;
		m_value = v.getCopy();
	}
	

	
	public String getName() {
		return m_name;
	}
	public void setName(String name) {
		this.m_name = name;
	}
	public Value getValue() {
		return m_value;
	}
	public void setValue(Value value) {
		this.m_value = value;
	}

	Valuation getcopy()
	{
		return new Valuation(m_name,m_value);
	}
	void print()
	{
		System.out.println("variable : " + m_name + ": " +  m_value.toString() );
	}
	
	boolean equals(Valuation v)
	{
		if(m_name==v.getName()  && m_value.equals(v.getValue()))
			return true;
		else
			return false;
	}
	
	
}
