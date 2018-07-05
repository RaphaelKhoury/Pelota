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
package ca.uqac.info.trace;

import java.util.*;

public class CarsFeeder extends MessageFeeder
{
  protected Vector<String> m_carsIn;
  protected Vector<String> m_carsOut;
  protected Random m_random;
  
  public CarsFeeder(String param_string)
  {
    int size = Integer.parseInt(param_string);
    m_carsIn = new Vector<String>();
    m_carsOut = new Vector<String>();
    m_random = new Random();
    // All cars are out
    for (int i = 0; i < size; i++)
    {
      m_carsOut.add(Integer.toString(i));
    }
  }
  
  @Override
  public String next()
  {
    StringBuilder out = new StringBuilder();
    Vector<String> to_go_in = new Vector<String>();
    Vector<String> to_go_out = new Vector<String>();
    boolean first;
    
    // Pick cars that will go out. If at least one car is in,
    // at least one car will go out (that's the purpose of variable "first")
    first = true;
    while ((first || m_random.nextBoolean()) && !m_carsIn.isEmpty())
    {
      first = false;
      int index = m_random.nextInt(m_carsIn.size());
      to_go_out.add(m_carsIn.elementAt(index));
      m_carsIn.removeElementAt(index);
    }
    // Pick cars that will go out
    first = true;
    while ((first || m_random.nextBoolean()) && !m_carsOut.isEmpty())
    {
      first = false;
      int index = m_random.nextInt(m_carsOut.size());
      to_go_in.add(m_carsOut.elementAt(index));
      m_carsOut.removeElementAt(index);
    }
    // Create event
    out.append("<message>\n");
    for (String s : to_go_out)
    {
      out.append(" <out>" + s + "</out>\n");
      m_carsOut.add(s);
    }
    for (String s : to_go_in)
    {
      out.append(" <in>" + s + "</in>\n");
      m_carsIn.add(s);
    }
    out.append("</message>\n");
    return out.toString();
  }
}
