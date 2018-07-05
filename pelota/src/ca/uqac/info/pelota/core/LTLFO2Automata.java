package ca.uqac.info.pelota.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ca.uqac.info.pelota.automaton.*;
import ca.uqac.info.pelota.core.PipeReader;
import ca.uqac.info.pelota.ltl.Operator;
import ca.uqac.info.pelota.ltl.Operator.ParseException;
import ca.uqac.info.pelota.verification.DynamicState;
import ca.uqac.info.pelota.verification.Verification;

public class LTLFO2Automata {

	//	private static String fn="C://Users//rkhoury//workspace//LTLFO2Automata//examples//GreaterThan7.ltlfo";
	//GreaterThan7.ltlfo
	//1-AdditionMakesOverflow.ltlfo
	//2-ProgramComprehention.ltlfo
	//3-noUsedBufferCall
	//4-nofctCallFromBuffer
	//5-script
	//xReleasey.ltlfo
	
	//twiceSameValue
	
	
	public static String DELIMITER="message";
	private static String fn="..//new-pelota-lab-master//bin//lab//data//prop3//prop3.ltlfo";
	private static String trace_filename="..//new-pelota-lab-master//bin//lab//data//prop3//tracey-6.xml";   
	



	//maximal number of messages to process. MAX_INT to process the entire file
	public static int MAX_ITERATION=Integer.MAX_VALUE;//Integer.MAX_VALUE;
	
	//Halt on Verdict
	private static boolean HALT_ON_VERDICT;
	
	//Print Event
	private static boolean PRINT_EVENT=false;
	
	//to print out the automaton after it is constructed (one time after it is created)
	private static boolean SHOW_AUTOMATON=true;
	
	//to print out the dynamic automaton at each step
	private static boolean PRINT_DYN_AUTOMATON=false;
	
	public static void main(String[] args) 
	{
		
		setArguments(args);
		Operator startState=null;//root will contain the start state of the automate		
		
		try 
		{
			
			String fileContent=util.readFile(fn);//reads the property file
			startState=ca.uqac.info.pelota.ltl.Operator.parseFromString(fileContent);
			
			System.out.println(startState.toString());//prints the property


			File f = new File(trace_filename);

	        FileInputStream fis=new FileInputStream(f);
	        
	        final long startTime = System.currentTimeMillis();
	        run(fis,startState);
	        final long endTime = System.currentTimeMillis();
	        System.out.println("Total execution time: " + (endTime - startTime) );
	        //Total execution time: 28825, 15651, 13284, 14053
	                             //16408  25065   13404 11694  13005

			
			
		} catch (IOException | ParseException | UnhandledOperator ex  ) {
			ex.printStackTrace();
		} 
		 	
	}
	
	
	//runs the program from the notify method in EventNotifyerPelota
	public static void run(InputStream is, ca.uqac.info.pelota.ltl.Operator op) throws UnhandledOperator
	{
        
		AutomatonLTLFO  auto = new AutomatonLTLFO(op); //create and print the automaton
		if(SHOW_AUTOMATON)
			auto.print();
		
		DynamicState ds= Verification.initialiazeVerification(auto);//initialize the verification

		
		EventNotifierPelota en = new EventNotifierPelota();
        PipeReader pr = new PipeReader(is, en, true);
        pr.setSeparator(DELIMITER, null); 
        en.setMaxIteration(MAX_ITERATION);
        en.setHaltOnVerdict(HALT_ON_VERDICT);
        en.setPrintEvent(PRINT_EVENT);
        en.setPrintDynAuto(PRINT_DYN_AUTOMATON);
        en.setStartState(ds);
         
        // Start event notifier
        en.reset();
        Thread th = new Thread(pr);
        th.start();
        
        try
        {
          th.join(); // Wait for thread to finish
        }
        catch (InterruptedException e1)
        {
          // Thread is finished
        }
		
	}
	
	//run locally here, seems a little bit slower
/*	public static void runOnTrace(InputStream is, ltl.Operator op ) throws UnhandledOperator
	{
		
		
		int ev_count = 0;
		AutomatonLTLFO  auto = new AutomatonLTLFO(op); //create and print the automaton
		if(SHOW_AUTOMATON)
			auto.print();
		DynamicStates startState= Verification.initialiazeVerification(auto);//initialize the verification
		
		boolean in_event = false;
		Scanner scanner = new Scanner(is);
		StringBuilder event = new StringBuilder();
		long start_time = System.currentTimeMillis();
		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine().trim();
			if (line.isEmpty())
				continue;
			if (line.startsWith("<message>"))
			{
				event.setLength(0);
				event.append(line).append("\n");
				in_event = true;
			}
			else if (line.startsWith("</message>"))
			{
				event.append(line).append("\n");

				core.Event e;
				try {
					e = new core.XPathEvent(event.toString());
					startState=Verification.evaluate(startState,e);
					
				} catch (core.Event.EventException e1) {
		
					e1.printStackTrace();
				}

				ev_count++;
				in_event = false;
				System.out.println(ev_count+"  "+ startState.getVerdict() );
				if((ev_count %10000 )==0)
					System.out.println(System.currentTimeMillis()-start_time);
			}
			else if (in_event)
			{
				event.append(line).append("\n");//what does this do?
			}
		}
		scanner.close();
	}*/
	
	//TODO: slowdown, 
	private static void setArguments(String[] args)
	{
		if(args.length==1)
		{
			if(args[0].startsWith("-h") || args[0].startsWith("--help") )
				printHelp();
			else if(args[0].startsWith("--version") )
				printVersion();
		}	
		if(args.length<2)
			return; //use when running the program from eclipse; eventually delete
		if(args.length==2)
		{
			trace_filename=args[0];
			fn=args[1];
		}
		else
		{
			for(int i=0;i<args.length-2;i++)
			{
				if(args[i].startsWith("-h") || args[i].startsWith("--help") )
					printHelp();
				else if(args[i].startsWith("--version") )
					printVersion();
				else if(args[i].startsWith("--eventname") )
				{
					String [] line=args[i].split("=");
					DELIMITER= line[1];
				}
				else if(args[i].startsWith("--automaton") )
				{
					SHOW_AUTOMATON=true;
				}
				else if(args[i].startsWith("--halt") )
				{
					HALT_ON_VERDICT=true;
				}
				else if(args[i].startsWith("--maxCount") )
				{
					String [] line=args[i].split("=");
					MAX_ITERATION= Integer.parseInt(line[1]);
				}
				else if(args[i].startsWith("--printEvent") )
				{
					PRINT_EVENT=true;
				}
				trace_filename=args[args.length-2];
				fn=args[args.length-1];
				
			}//for
		}//else
	}//setArguments
	
	private static void printVersion()
	{
	    System.out.println("Pelota : An automata-based monitor for LTL-FO+. ");
		System.out.println("(C) 2017 Raphael Khoury et al., Universite du Quebec a� Chicoutimi");
		System.out.println("version 0.9");
		System.exit(0);
	}
	
	private static void printHelp()
	{
	    System.out.println("Pelota : An automata-based monitor for LTL-FO+. v.09");
	    System.out.println("usage : java -jar Pelota.jar [options] trace_file.xml  property_file.ltlfo");
	    System.out.println();
	    System.out.println("	-h,--help		Display command line usage");
	    System.out.println("	-v,--version		Show version");
	    System.out.println("	--eventname=<x>		Set event name to x (default: \"message\")");
	    System.out.println("	--automaton		Print the property automaton ");
	    System.out.println("	--halt				halts when a verdict is obtained ");
	    System.out.println("	--printEvent		print each event as it is read");
	    System.out.println("	--maxCount=<x>		Run only on the first x events ");
	    System.out.println();
	    System.out.println();
	    System.out.println("(C) 2017 Raphaël Khoury et al., Université du Québec à Chicoutimi");
	    System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
	    System.out.println("This is a free software, and you are welcome to redistribute it");
		System.exit(0);
	}
	
	
	public static void setTraceFileName(String f)
	{
		trace_filename=f;
	}
	public static void setPropertyFileName(String f)
	{
		fn=f;
	}
	public static void setMaxIteration(int i)
	{
		MAX_ITERATION =i;
	}
	
	
	private void printStackTrace()//useful for testing 
	{
		System.out.println("Printing stack trace:");
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
		     StackTraceElement s = elements[i];
		     System.out.println("\tat " + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
		}
		
	}
	
	

}




