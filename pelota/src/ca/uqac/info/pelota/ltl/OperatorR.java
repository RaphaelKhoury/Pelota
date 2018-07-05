//RK (adapted from SH)
package ca.uqac.info.pelota.ltl;

public class OperatorR extends BinaryOperator
{
	public static final String SYMBOL = "R";
	
	public OperatorR()
	{
		super();
		m_symbol = OperatorR.SYMBOL;
		m_commutes = false;
	}

	public OperatorR(Operator left, Operator right)
	{
		super(left, right);
		m_symbol = OperatorR.SYMBOL;
		m_commutes = false;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return super.equals((BinaryOperator) o);
	}

    @Override
	public void accept(OperatorVisitor v)
	{
	  super.accept(v);
	  v.visit(this);
	}
}
