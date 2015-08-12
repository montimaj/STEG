package stegano.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DoStegano
{     
	private String decoded_str="";
	public DoStegano(String cipher, String imgfile, String dir) throws IOException 
	{     
		  encode(cipher, imgfile, dir);		  
	}
	public DoStegano(String imgfile) throws IOException
	{
		decoded_str=decode(imgfile);
	}	
	public String getDecoded_str() 
	{
		return decoded_str;
	}	
	private void encode(String cipher,String imgfile, String dir) throws IOException
	{
		File input = new File(imgfile);
		if(!input.exists())
	    	  throw new IOException("IO error!");
		BufferedImage image = ImageIO.read(input);         
	    int width = image.getWidth();
	    int height = image.getHeight();      
	    int len=cipher.length();
	    int k=0,p,a,r,g,b,bit;	    	    
	    for(int i=0; i<height && k<len; i++)
	    {         
	    	for(int j=0; j<width && k<len; j++)
	        {             
	    		 p=image.getRGB(j, i);                    
	    		 a=(p>>24)&0xff;
	    		 r=(p>>16)&0xff;  
	             g=(p>>8)&0xff;
	             b=p&0xff;
	             bit=cipher.charAt(k++)-48;
	             a=(a&~1)|bit;
	             bit=cipher.charAt(k++)-48;
	             r=(r&~1)|bit;
	             bit=cipher.charAt(k++)-48;
	             g=(g&~1)|bit;
	             bit=cipher.charAt(k++)-48;
	             b=(b&~1)|bit;	   
	             p=(a<<24)|(r<<16)|(g<<8)+b;                  
	             image.setRGB(j, i, p);               
	        }
	    }	    
	    ImageIO.write(image, "png", new File(dir+"/steg_"+len+".png")); 
	}
	private String decode(String file) throws IOException
	{
		File input = new File(file);
		if(!input.exists())
			   throw new IOException("IO error!");
		String l=file.substring(file.lastIndexOf('_')+1,file.lastIndexOf('.'));
		int len=Integer.parseInt(l);
		BufferedImage image = ImageIO.read(input);         
		int width = image.getWidth();
		int height = image.getHeight();         
		String bits=""; 
		int k=0,p,a,r,g,b;		  
		for(int i=0; i<height && k<len; i++)
		{         
			for(int j=0; j<width && k<len; j++,k+=4)
			{		
				p=image.getRGB(j, i);
				a=(p>>24)&0xff;
	    		r=(p>>16)&0xff;  
	            g=(p>>8)&0xff;
	            b=p&0xff;		   
		        bits+=(a&1)!=0?1:0;  
		        bits+=(r&1)!=0?1:0;
		        bits+=(g&1)!=0?1:0;
		        bits+=(b&1)!=0?1:0;
		    }
		}	
		return bits;	  
	}	
}