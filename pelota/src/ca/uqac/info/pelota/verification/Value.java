package ca.uqac.info.pelota.verification;

 abstract public class Value {

	public abstract Value getCopy();
	public abstract boolean  equals(Value v);
	public abstract String toString();
}
