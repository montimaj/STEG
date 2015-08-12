package edu.sxccal.stegano;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

/**
 * Creates the Stegano image, digital signature and the public key
 * @since 1.0
 */
 public class QR
{
	public static String str="";

	/**
	 * Creates Stegano image of size 400X400
	 * @param dataPath Path to input data
	 * @throws Exception
	 */
	public static void generateQRCode(String dataPath) throws Exception
	{	    
			String data =read_from_file(dataPath);
		    QRCodeWriter writer = new QRCodeWriter();
			String genqr= Stegano.filePath+"/Stegano.png";
			int img_size=400;		
			BitMatrix bm = writer.encode(data, BarcodeFormat.QR_CODE,img_size,img_size);
			Bitmap bmp = Bitmap.createBitmap(img_size,img_size,Bitmap.Config.ARGB_8888); 		
			if (bmp != null) 
			{
				File f=new File(genqr);
			    if(f.exists())
			    	f.delete();
			    FileOutputStream gqr=new FileOutputStream(genqr);
			    for (int i = 0; i < img_size; i++) 		   
					for (int j = 0; j < img_size; j++) 
						bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);		
			    bmp.compress(Bitmap.CompressFormat.PNG, 100,gqr);
			    str+="\nStegano img: "+genqr;
			    gqr.close();
			}
			else
				throw new WriterException("Stegano generation failed!");

	}

	/**
	 * @param s Input file to be read
	 * @return {@link android.util.Base64} encoded String
	 * @throws Exception
	 */
	public static String read_from_file(String s) throws Exception
	{		
		String ext=s.substring(s.lastIndexOf('.')+1,s.length());
		boolean flag=false;
		File file=new File(s);
		if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png") ||
												ext.equalsIgnoreCase("jpeg") && file.length()>1500)
		{
			//if input file is an image of size>~1.5KB convert it to black and white
			s=ImgtoBW.toBW(s);
			flag=true;			
		}
		file=new File(s);
		FileInputStream fp=new FileInputStream(file);
		byte[] data=new byte[fp.available()];
		fp.read(data); //store data read from input file in a string
		fp.close();
		if(flag)
			str+="\nB&W image: "+s;
		s = Base64.encodeToString(data, Base64.DEFAULT);
		return s;
	}
}