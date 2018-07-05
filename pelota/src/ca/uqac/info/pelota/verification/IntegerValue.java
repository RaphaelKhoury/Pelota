package ca.uqac.info.pelota.verification;

public class IntegerValue extends Value{
	
	 private int m_intValue;

	 IntegerValue(int v)
	 {
		 m_intValue=v;
	 }
	  
	 protected int getIntValue()
	 {
		 return m_intValue;
	 }
	
	IntegerValue getcopy() {
		return new IntegerValue(m_intValue);
	}
	
	public  String toString()
	{
		return Integer.toString( m_intValue );
	}
	
	public boolean  equals(Value v)
	{
		if(v instanceof IntegerValue )
		{
			if(m_intValue == ((IntegerValue) v).getIntValue() )
				return true;
		}
		return false;
	}


	
	public Value getCopy() {
		return new IntegerValue(m_intValue);
	}


}
