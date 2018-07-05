/*
	BeepBeep, an LTL-FO+ runtime monitor with XML events
	Copyright (C) 2008-2013 Sylvain Hallé

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.info.pelota.core;

import java.util.Scanner;

import ca.uqac.info.pelota.core.PipeCallback;
import ca.uqac.info.pelota.verification.DynamicState;
import ca.uqac.info.pelota.verification.Verification;
import ca.uqac.info.pelota.xml.Event;
import ca.uqac.info.pelota.xml.XPathEvent;
import ca.uqac.info.pelota.xml.Event.EventException;
//import lab.PelotaExperiment;



/**
 * 
 * <strong>NOTE:</strong> We must make sure that all methods that iterate
 * over the vector of monitors are <strong>synchronized</strong>, to avoid
 * one thread accessing this vector and modifying its contents while
 * another thread iterates over it.
 * @author sylvain
 *
 */
public class EventNotifierPelota implements PipeCallback<String>
{
  //protected Vector<MonitorInfo> m_minfo;
  
  private DynamicState startState;
  protected int m_numEvents = 0;
  //private static long ct=System.nanoTime();
  private int m_MaxCount;
  private boolean m_HaltOnVerdict;
  private boolean m_printEvent;
  private boolean m_printDynAuto;

  
 // protected PelotaExperiment m_experiment;
  
  int temp=0;
  
 /* public EventNotifierPelota(PelotaExperiment e)
  {
	  super();
	  m_experiment = e;
  }
  
  public boolean isRunning()
  {
	  return m_experiment.isRunning();
  }*/
  
  public int eventCount()
  {
    return m_numEvents;
  }

  @Override
  public synchronized void notify(String token, long buffer_size) throws CallbackException
  {
    //System.out.println(ESC_HOME + ESC_CLEARLINE + "Event received");
    if (m_printEvent)//change to true to print events as they are read 
    {
      System.out.print(token);
    }
   // if (m_experiment != null)
   // {
    	// Update experiment state
   // 	if (m_numEvents % m_experiment.getUpdateInterval() == 0)
   // 	{
   // 		m_experiment.addReading(m_numEvents);
   // 	}
   // }
    // Update all monitors
    Event e = null;
    try
    {
      e = new XPathEvent(token);
    }
    catch (EventException e1)
    {
      // Some problem occurred when making an event out of the received
      // token; print a warning and ignore the event
      e1.printStackTrace();
      return;
    }
    

    //this is to test the time to read events
    //if((m_numEvents %1000) ==0)
    //{ 
    //	System.out.println((System.nanoTime()-ct));
    //	ct=System.nanoTime();
    //}
	
    //this does the actual work 
    Verification.resetHashTable();
	startState=Verification.evaluate(startState,e);

	if (m_printDynAuto) //to print the automaton after each step
		startState.print();
	
	//print the result
	System.out.println(m_numEvents+"  "+ startState.getVerdict() );
	
	//use this to pause after each event (useful for debugging)
	//promptEnterKey();

	
	///////////////////////////////////memory cons
	/*Runtime runtime = Runtime.getRuntime();

	NumberFormat format = NumberFormat.getInstance();

	StringBuilder sb = new StringBuilder();
	long maxMemory = runtime.maxMemory();
	long allocatedMemory = runtime.totalMemory();
	long freeMemory = runtime.freeMemory();

	sb.append("free memory: " + format.format(freeMemory / 1024) + "<br/>");
	sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "<br/>");
	sb.append("max memory: " + format.format(maxMemory / 1024) + "<br/>");
	sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "<br/>");
	System.out.println(sb);
	*////////////////////////////////////

	
	
	
	if( m_HaltOnVerdict && !startState.getVerdict().equals("?"))//halt on Verdict
		throw new VerdictObtainedException(startState.getVerdict());
	if(m_numEvents>m_MaxCount)
		throw new MaximumEventsReachedException();
	
	//if((m_numEvents %10000 )==0)
	//	System.out.println(System.currentTimeMillis()-ct);
	
	//prints the size of the automaton every 1000 steps (useful for testing)
	//if((m_numEvents %1000 )==0)
	//	System.out.print(startState.size()+ "  ");
	
	m_numEvents++; // Increment counter 
	
  }

  //initialize in constructor and then we don't need this reset
  public synchronized void reset()
  {
    m_numEvents = 0;
  }


 /* protected synchronized void printHeader()
  {
    System.err.print("\nMsgs |Last   |Total     |Max heap |Buffer   |");
    for (int i = 0; i < m_minfo.size(); i++)
    {
      MonitorInfo mi = m_minfo.elementAt(i);
      Map<String,String> metadata = mi.m_metadata;
      String caption = metadata.get("Caption");
      if (caption == null || caption.isEmpty())
        System.err.print("· ");
      else
        System.err.print(caption + " ");  
    }
    System.err.println(" ");
  }*/
  

	
  //Use to pauyse after each event. usefull for testing
  private void promptEnterKey(){
		   System.out.println("Press \"ENTER\" to continue...");
		   Scanner scanner = new Scanner(System.in);
		   scanner.nextLine();
		}
  
  
  public DynamicState getStartState() {
	return startState;
	}
	
	
	public void setStartState(DynamicState startState) {
		this.startState = startState;
	}
	
	public void setMaxIteration(int mi) {
		m_MaxCount=mi;
		
	}
	
	public void setHaltOnVerdict(boolean h) {	
		m_HaltOnVerdict =h;
	}
	
	public void setPrintEvent(boolean p) {	
		m_printEvent =p;
	}
	
	public void setPrintDynAuto(boolean p) {
		m_printDynAuto =p;
		
	}
	
	
	public static class VerdictObtainedException extends CallbackException
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 1L;
		
		private transient String m_verdict = "?";

		public VerdictObtainedException(String verdict)
		{
			super("A verdict has been obtained");
			m_verdict = verdict;
		}
		
		public String getVerdict()
		{
			return m_verdict;
		}
		
	}
	
	public static class MaximumEventsReachedException extends CallbackException
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 1L;

		public MaximumEventsReachedException()
		{
			super("The maximum number of events has been reached");
		}
		
	}



}
