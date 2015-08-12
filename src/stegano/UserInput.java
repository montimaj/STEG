package stegano;

import java.io.IOException;

import stegano.utilities.Log;

/**
 * Takes plain text file path as input and uses 
 * <p>
 * {@link stegano.Encrypt} to generate key.txt and cipher_text.txt 
 * @author Sayantan Majumdar
 * @since 1.0
 */
public class UserInput 
{

	/**
	 * Main module 
	 * @param args Input file path
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Process p=null;
		try
		{
			if(args.length<4 || args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty() || args[3].isEmpty())
				throw new IOException("Invalid Input"); 
			else if(args[0].length()>16 )
				throw new IOException("Secret key length must be between 1-16");
			String[] x={"zenity","--progress","--pulsate","--no-cancel","--text=Encrypting..."};
			p=new ProcessBuilder(x).start();
			new Encrypt(args[0],args[1],args[2],args[3]);
			p.destroy();
			String x1[]={"zenity","--info","--title=Result","--text=Done!"};
			p=new ProcessBuilder(x1).start();
			p.waitFor();			
		}
		catch(Exception e)
		{
			if(p!=null)
				p.destroy();
			String s=Log.create_log(args[3], e), x[]={"zenity","--error","--text="+s};
			p=new ProcessBuilder(x).start();
			p.waitFor();
		}
	}
}
