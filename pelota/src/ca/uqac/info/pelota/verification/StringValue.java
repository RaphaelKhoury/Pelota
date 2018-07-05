package ca.uqac.info.pelota.verification;

public class StringValue extends Value{
	
	 private String m_stringValue;

	 StringValue(String v)
	 {
		 m_stringValue=v;
	 }
	 
	 protected String getStringValue()
	 {
		 return m_stringValue;
	 }
	
	StringValue getcopy() {
		return new StringValue(m_stringValue);
	}
	
	public boolean  equals(Value v)
	{
		if(v instanceof StringValue )
		{
			if(m_stringValue == ((StringValue) v).getStringValue() )
				return true;
		}
		return false;
	}
	
	public Value getCopy() {
		return new StringValue(m_stringValue);
	}

	
	public String toString() {
		return m_stringValue;
	}


}
