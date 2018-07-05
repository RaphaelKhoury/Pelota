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
package ca.uqac.info.monitor;

public class MonitorNot extends UnaryMonitor
{ 
  public MonitorNot(Monitor m)
  {
    super(m);
  }

  @Override
  public void processEvent(Event e) throws MonitorException
  {
    if (m_verdict == Verdict.INCONCLUSIVE || !SKIP_ON_VERDICT)
    {
      m_phi.processEvent(e);
      m_verdict = updateVerdict();
    }
  }

  protected Verdict updateVerdict()
  {
    if (m_verdict == Verdict.INCONCLUSIVE || !SKIP_ON_VERDICT)
    {
      Verdict v = m_phi.getVerdict();
      m_verdict = threeValuedNot(v);
    }
    return m_verdict;
  }

  @Override
  public Monitor deepClone()
  {
    return new MonitorNot(m_phi.deepClone());
  }
  
  @Override
  public String toString()
  {
    return "¬ (" + m_phi + ")";
  }
  
  @Override
  public void accept(MonitorVisitor v)
  {
    m_phi.accept(v);
    v.visit(this);
  }
  
  @Override
  public void instanceAcceptPostfix(MonitorVisitor v)
  {
    m_phi.instanceAcceptPostfix(v);
    v.visit(this);
  }

}
