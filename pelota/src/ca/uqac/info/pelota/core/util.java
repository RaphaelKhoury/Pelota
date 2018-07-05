package ca.uqac.info.pelota.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public abstract class util {

	/**
	 * Reads a file and puts its contents in a string
	 * @param filename The name (and path) of the file to read
	 * @return The file's contents, and empty string if the file
	 * does not exist
	 */
	protected static String readFile(java.util.Scanner scanner) throws IOException
	{
		//java.util.Scanner scanner = null;
		StringBuilder out = new StringBuilder();

		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			line = line.trim();
			if (line.isEmpty())
				continue;
			out.append(line).append(System.getProperty("line.separator"));
		}

		return out.toString();
	}



	protected static  String readFile(String filename) throws IOException
	{
		java.util.Scanner scanner = null;
		String out;
		try
		{
			scanner = new java.util.Scanner(new java.io.FileInputStream(filename), "UTF-8");
			out=readFile(scanner);
		}
		catch (java.io.IOException e)
		{
			throw e;
		}
		finally
		{
			if (scanner != null)
				scanner.close();
		}
		return out.toString();
	}


	//opens the file and creates the XML event reader
	//deprecated 
	@SuppressWarnings("unused")
	private static XMLEventReader getXMLEventHandlerFromFile(String trace_filename) throws FileNotFoundException, XMLStreamException
	{

		XMLInputFactory f = XMLInputFactory.newInstance();
		XMLEventReader xr;

		xr = f.createXMLEventReader(new FileInputStream(trace_filename));

		return xr;
	}


	//opens an input stream and creates the XML event reader
	//deprecated (too slow)
	@SuppressWarnings("unused")
	private  static XMLEventReader getXMLEventHandlerFromFile(InputStream file_stream) throws FileNotFoundException, XMLStreamException
	{

		XMLInputFactory f = XMLInputFactory.newInstance();
		XMLEventReader xr;

		xr = f.createXMLEventReader(file_stream);

		return xr;
	}


	//TODO: there may be a more effiscne tmanner to do this also. 
	public static boolean isInteger( String input )
	{
		try {
			Integer.parseInt( input );
			return true;
		}
		catch( Exception e ) {
			return false;
		}
	}

	//This is a more  efficient ParseInt method. It does NOT test if the input really is an int. This must be done before. 
	//Otherwise it will behave unexpectedly. 
	public static int efficientParseInt( final String s )
	{
		// Check for a sign.
		int num  = 0;
		int sign = -1;
		final int len  = s.length( );
		final char ch  = s.charAt( 0 );
		if ( ch == '-' )
			sign = 1;
		else
			num = '0' - ch;

		// Build the number.
		int i = 1;
		while ( i < len )
			num = num*10 + '0' - s.charAt( i++ );

		return sign * num;
	} 




	/*	//parses aan int in the text. returns an Integer object 
		//not clear which is faster, this ir isInteger
		public static Integer tryParse(String text) {
			  try {
			    return Integer.parseInt(text);
			  } catch (NumberFormatException e) {
			    return null;
			  }
			}*/




}
