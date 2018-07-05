package ca.uqac.info.pelota.verification;

public class BotDynamicState extends DynamicState{
	

	public void print() {
		System.out.println("Bot");
	}

	
	public boolean equals(DynamicState d) {
		
		if(d instanceof BotDynamicState)
			return true;
		else
			return false;
	}



	public int size() {
		return 1;
	}

}
