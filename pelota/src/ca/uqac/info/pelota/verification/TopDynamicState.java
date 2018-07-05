package ca.uqac.info.pelota.verification;

public class TopDynamicState extends DynamicState {


	
	public void print() {
		System.out.println("Top");
	}

	public int size() {
		return 1;
	}
	
	public boolean equals(DynamicState d) {
	
		if(d instanceof TopDynamicState)
			return true;
		else
			return false;
	}

}
