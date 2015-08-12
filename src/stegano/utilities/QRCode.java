package stegano.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import stegano.Encrypt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/** 
* Generates the QRCode image
* @since 1.0
* @see <a href="http://zxing.github.io/zxing/apidocs/">Zxing</a>
*/

public class QRCode 
{
	public String fname;
	public QRCode(String file, String dir) throws Exception
	{
		readQRCode(file, dir);
	}
	public QRCode(String input, String dir, int n) throws Exception
	{
		gen_qrcode(input, dir, n);
	}
	/**
	  * @param input path to the zip file 
	  *	@param dir Ouput directory 
	  * @param name File name
	  * <p>    
	  * @throws IOException
	  * @throws WriterException If ISO-8859-1 encoded zip string is > ~2.9KB	 
	  */
	private void gen_qrcode(String input, String dir, int n) throws Exception
	{		
		String file = dir+"/QRCode_"+n+".png";		
		String data=new String(Encrypt.read_from_file(input).getBytes(),"ISO-8859-1");	
		Map<EncodeHintType, ErrorCorrectionLevel> hint_map1 = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hint_map1.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);		
		createQRCode(data,file, hint_map1, 400,400);		
	}	
	private void createQRCode(String data, String file, Map<EncodeHintType, ErrorCorrectionLevel> hint_map, int qrh, int qrw) throws IOException, WriterException
	{	   
		BitMatrix matrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, qrw, qrh, hint_map); 
	    MatrixToImageWriter.writeToFile(matrix, "png",new File(file));
	}
	private void readQRCode(String file, String dir) throws Exception 
	{		
		Map<DecodeHintType, ErrorCorrectionLevel> hint_map=new HashMap<DecodeHintType, ErrorCorrectionLevel>();
		hint_map.put(DecodeHintType.TRY_HARDER, ErrorCorrectionLevel.L);
		String l=file.substring(file.lastIndexOf('_')+1,file.lastIndexOf('.'));
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(file)))));
		Result qr_result = new QRCodeReader().decode(bitmap, hint_map);
		String data=qr_result.getText();
		fname=dir+"/decoded_"+l+".png";
		FileOutputStream fos=new FileOutputStream(fname);
		fos.write(data.getBytes("ISO-8859-1"));
		fos.close();				
	}
}