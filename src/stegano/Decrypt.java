package stegano;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

import stegano.utilities.GenKey;
import stegano.utilities.Log;
import stegano.utilities.doStegano;
import stegano.utilities.QRCode;

/**
 * Main Decryption module
 * @author Sayantan Majumdar
 * @since 1.0
 */
public class Decrypt 
{
	private static String extract_chars(int nrows, int ncols, char mat[][], boolean flag[][])
	{
		String s="";		
		for(int i=0;i<nrows ;++i)		
			for(int j=0;j<ncols;++j)	
				if(flag[i][j])
					s+=mat[i][j];		
		return s;
	}
	private static void init_matrix(String s, int nrows, int ncols, int num[], char mat[][], boolean flag[][]) throws IOException
	{
		try
		{
			int k=0;		
			for(int i=0;i<ncols;++i)
				for(int j=0;j<nrows;++j)
					if(flag[j][num[i]])
						mat[j][num[i]]=s.charAt(k++);	
		}	
		catch(StringIndexOutOfBoundsException e)
		{
			throw new IOException("Invalid Secret Key or input files");
		}
	}
	private static void init_key_array(String keyfile, int num[]) throws IOException
	{
		DataInputStream key=new DataInputStream(new FileInputStream(keyfile));
		int i=0;
		boolean eof=false;
		while(!eof)
		{			
			try
			{
				num[i++]=key.readInt();
			}				
			catch(EOFException e)
			{
				eof=true;
				key.close();
			}
			catch(ArrayIndexOutOfBoundsException e1)
			{
				throw new IOException("Invalid Secret Key or input files");
			}
		}
	}		
	/**
	 * Performs decryption operation
	 * @param cipher Path to the cipher file
	 * @param ext Extension of the plain text file
	 * @throws IOException
	 */
	private static void decrypt(String skey,String qrimg, String qrkey, String dir) throws Exception
	{	
		String imgfile=new QRCode(qrimg,dir,false).fname;
		String keyfile=new QRCode(qrkey,dir,true).fname;
		String bits=new doStegano(imgfile).getDecoded_str();
		GenKey gk=new GenKey(skey);		
		int ncols=gk.get_colsize(),num[]=new int[ncols], len=bits.length(),nrows=len/ncols, ndecrypt=gk.get_encryption_number();
		init_key_array(keyfile, num);
		if(len>nrows*ncols)
			nrows++;		
		boolean flag[][]=new boolean[nrows][ncols];
		Encrypt.init_matrix(bits, nrows, ncols, flag);
		char mat[][]=new char[nrows][ncols];
		for(int i=0;i<ndecrypt;++i)
		{			
			init_matrix(bits, nrows, ncols, num, mat,flag);			
			bits=extract_chars(nrows, ncols, mat, flag);		
		}		
		String dt=Encrypt.bits_to_ascii(bits);		
		FileOutputStream dos=new FileOutputStream(dir+"/decrypted.txt");
		for(int i=0;i<dt.length();++i)
			dos.write(dt.charAt(i));
		dos.close();		
	}
	public static void main(String[] args) throws InterruptedException, IOException
	{
		Process p=null;
		try
		{			
			if(args.length<4 || args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty() || args[3].isEmpty())
				throw new IOException("Invalid input");	
			else if(args[0].length()>16)
				throw new IOException("Secret key length must be between 1-16");	
			String[] x={"zenity","--progress","--pulsate","--no-cancel","--text=Decrypting..."};
			p=new ProcessBuilder(x).start();
			decrypt(args[0], args[1], args[2], args[3]);
			p.destroy();
			String x1[]={"zenity","--info","--title=Result","--text=Done!"};
			p=new ProcessBuilder(x1).start();
			p.waitFor();			
		}		
		catch(Exception e)
		{
			if(p!=null)
				p.destroy();
			String s=Log.create_log(args[3],e), x[]={"zenity","--error","--text="+s};
			p=new ProcessBuilder(x).start();
			p.waitFor();
		}
	}
}
