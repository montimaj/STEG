package edu.sxccal.stegano;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCode
{
    public static String str="";

    /**
     * Creates QRCode image of size img_sizeXimg_size
     * @param dataPath Path to input data
     * @throws IOException
     * @throws WriterException
     */
    public static void encode(String dataPath, boolean flag) throws IOException, WriterException
    {
            String data = "";
            QRCodeWriter writer = new QRCodeWriter();
            String genqr="";
            int img_size=400;
            if(flag)
            {
                data=DoEncrypt.read_from_file(dataPath);
                data = new String(data.getBytes(), "ISO-8859-1");
                genqr = Stegano.filePath + "/QRCode.png";
            }
            else {
                FileInputStream fis=new FileInputStream(dataPath);
                byte[] b=new byte[fis.available()];
                fis.read(b);
                fis.close();
                data=Base64.encodeToString(b,Base64.DEFAULT);
                genqr = Stegano.filePath + "/QRCode_key.png";
            }
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
                throw new WriterException("QRCode generation failed!");
    }
}
