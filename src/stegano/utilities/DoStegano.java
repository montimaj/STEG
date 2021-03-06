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
	private String get_Bits(int n)
	{
		int mask=1<<15;
		String bits="";
		while(mask!=0)
		{
			bits+=(n&mask)!=0?1:0;
			mask>>=1;
		}		
		return bits;
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
	    String lbits=get_Bits(len);
	    int k=0,i=0,p,a,r,g,b,bit;	
	    int pixels[]=new int[width*height];
	    image.getRGB(0, 0, width, height, pixels, 0, width);
	    for(i=0; i<4; i++)
	    {	    	             
	    		p=pixels[i];
	    		a=(p>>24)&0xff;
	    		r=(p>>16)&0xff;  
	            g=(p>>8)&0xff;
	            b=p&0xff;
	    		bit=lbits.charAt(k++)-48;
	    		a=(a&~1)|bit;
		        bit=lbits.charAt(k++)-48;
		        r=(r&~1)|bit;
		        bit=lbits.charAt(k++)-48;
		        g=(g&~1)|bit;
		        bit=lbits.charAt(k++)-48;
		        b=(b&~1)|bit;	   
		        p=(a<<24)|(r<<16)|(g<<8)|b;                  
		        pixels[i]=p;	    	
	    }	    
	    k=0;
	    for(int x=i+1;x<width*height && k<len;x++)
	    {    
	    		 p=pixels[x];
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
	             p=(a<<24)|(r<<16)|(g<<8)|b;                  
	             pixels[x]=p;	        
	    }	    
	    image.setRGB(0, 0, width, height, pixels, 0, width);
	    ImageIO.write(image, "png", new File(dir+"/steg.png")); 
	}
	private String decode(String file) throws IOException
	{
		File input = new File(file);
		if(!input.exists())
			   throw new IOException("IO error!");
		BufferedImage image = ImageIO.read(input);         
		int width = image.getWidth();
		int height = image.getHeight();         
		String bits=""; 
		int pixels[]=new int[width*height];
	    image.getRGB(0, 0, width, height, pixels, 0, width);
		int p,a,r,g,b,i=0;		  
		for(i=0; i<4; i++)
		{			
				p=pixels[i];
				a=(p>>24)&0xff;
	    		r=(p>>16)&0xff;  
	            g=(p>>8)&0xff;
	            b=p&0xff;		   
		        bits+=(a&1)!=0?1:0;  
			    bits+=(r&1)!=0?1:0;
			    bits+=(g&1)!=0?1:0;
			    bits+=(b&1)!=0?1:0;			
		}
		int power=15,len=0;
		for(int x=0;x<16;++x)
		{
			len+=(bits.charAt(x)-48)*(int)Math.pow(2,power--);
		}		
		bits="";
		for(int x=i+1,k=0; x<width*height && k<len; x++,k+=4)
		{         
				p=pixels[x];
				a=(p>>24)&0xff;
	    		r=(p>>16)&0xff;  
	            g=(p>>8)&0xff;
	            b=p&0xff;		   
		        bits+=(a&1)!=0?1:0;  
			    bits+=(r&1)!=0?1:0;
			    bits+=(g&1)!=0?1:0;
			    bits+=(b&1)!=0?1:0;		    			
		}
		return bits;	  
	}	
}