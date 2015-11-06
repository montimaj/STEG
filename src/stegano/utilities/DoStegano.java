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
	    int k=0,i=0,j=0,p,a,r,g,b,bit;	    	    
	    for(i=0; i<height && k<16; i++)
	    {         
	    	for(j=0; j<width && k<16; j++)
	        {             
	    		p=image.getRGB(j, i);
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
		        image.setRGB(j, i, p);	    			
	    	}
	    }
	    k=0;	    
	    for(int x=i+1;x<height && k<len;x++)
	    {
	    	for(int y=j+1;y<width && k<len;y++)
	    	{
	    		 p=image.getRGB(y, x);
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
	             image.setRGB(y, x, p);               
	        }
	    }	    
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
		int k=0,p,a,r,g,b,i=0,j=0;		  
		for(i=0; i<height && k<16; i++)
		{         
			for(j=0; j<width && k<16; j++,k+=4)
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
		int power=15,len=0;
		for(int x=0;x<16;++x)
		{
			len+=(bits.charAt(x)-48)*(int)Math.pow(2,power--);
		}		
		k=0;
		bits="";
		for(int x=i+1; x<height && k<len; x++)
		{         
			for(int y=j+1; y<width && k<len; y++,k+=4)
			{		
				p=image.getRGB(y, x);
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