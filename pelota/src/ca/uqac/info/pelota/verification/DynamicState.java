/*This is the top level of the hierarchy that includes all types of dynamic state */
//When evaluation using the evaluate method, the variable evaluation is ignored.
//However, when evaluating using the evaluateAndSimplify method, the final verdict is stored in this variable. 
package ca.uqac.info.pelota.verification;

public abstract class DynamicState {
	
	private String m_evaluation="";
	
	abstract public void print();
	abstract public boolean equals(DynamicState d);
	
	
	public String getEvaluation() {
		return m_evaluation;
	}
	public void setEvaluation(String evaluation) {
		this.m_evaluation = evaluation;
	}

	public abstract int size();
	
	public String getVerdict()
	{
		if(this instanceof TopDynamicState)
			return "T";
		else if (this instanceof BotDynamicState)
			return "F";
		else
			return "?";
	}

}
