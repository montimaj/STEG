package edu.sxccal.stegano;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DoStegano
{
    private String decoded_str="";
    public DoStegano(String cipher, String imgfile) throws IOException
    {
        encode(cipher, imgfile);
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
    private void encode(String cipher,String imgfile) throws IOException {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPremultiplied=false;
        options.inMutable=true;
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;
        FileInputStream fis=new FileInputStream(imgfile);
        byte[] img_bytes=new byte[fis.available()];
        fis.read(img_bytes);
        fis.close();
        Bitmap img = BitmapFactory.decodeByteArray(img_bytes,0,img_bytes.length,options);
        int len = cipher.length();
        int width = img.getWidth();
        int height = img.getHeight();
        String lbits = get_Bits(len);
        int k = 0, i, j = 0, p, a, r, g, b, bit;
        for (i = 0; i < height && k < 16; i++) {
            for (j = 0; j < width && k < 16; j++) {
                p = img.getPixel(j, i);
                a = (p>>24) & 0xff;
                r = (p>>16) & 0xff;
                g = (p>>8) & 0xff;
                b = p & 0xff;
                bit = lbits.charAt(k++) - 48;
                a = (a & ~1) | bit;
                bit = lbits.charAt(k++) - 48;
                r = (r & ~1) | bit;
                bit = lbits.charAt(k++) - 48;
                g = (g & ~1) | bit;
                bit = lbits.charAt(k++) - 48;
                b = (b & ~1) | bit;
                p=(a << 24) | (r << 16) | (g << 8) | b;
                img.setPixel(j,i,p);
            }
        }
        k = 0;
        for (int x = i + 1; x < height && k<len; x++) {
            for (int y = j + 1; y < width && k<len; y++) {
                p = img.getPixel(y, x);
                a = (p>>24) & 0xff;
                r = (p>>16) & 0xff;
                g = (p>>8) & 0xff;
                b = p & 0xff;
                bit = cipher.charAt(k++) - 48;
                a = (a & ~1) | bit;
                bit = cipher.charAt(k++) - 48;
                r = (r & ~1) | bit;
                bit = cipher.charAt(k++) - 48;
                g = (g & ~1) | bit;
                bit = cipher.charAt(k++) - 48;
                b = (b & ~1) | bit;
                p = (a<<24) | (r<<16) | (g<<8) | b;
                img.setPixel(y, x, p);
            }
        }
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, bao);
        FileOutputStream ios=new FileOutputStream(Stegano.filePath + "/steg.png");
        bao.writeTo(ios);
        ios.close();
    }
    private String decode(String imgfile) throws IOException
    {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPremultiplied=false;
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;
        FileInputStream fis=new FileInputStream(imgfile);
        byte[] img_bytes=new byte[fis.available()];
        fis.read(img_bytes);
        fis.close();
        Bitmap img = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length, options);
        img.setHasAlpha(true);
        int width = img.getWidth();
        int height = img.getHeight();
        String bits="";
        int k=0,p,a,r,g,b,i,j=0;
        for(i=0; i<height && k<16; i++)
        {
            for(j=0; j<width && k<16; j++,k+=4)
            {
                p=img.getPixel(j, i);
                a = (p>>24) & 0xff;
                r = (p>>16) & 0xff;
                g = (p>>8) & 0xff;
                b = p & 0xff;
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
                p=img.getPixel(y, x);
                a = (p>>24) & 0xff;
                r = (p>>16) & 0xff;
                g = (p>>8) & 0xff;
                b = p & 0xff;
                bits+=(a&1)!=0?1:0;
                bits+=(r&1)!=0?1:0;
                bits+=(g&1)!=0?1:0;
                bits+=(b&1)!=0?1:0;
            }
        }
        return bits;
    }
}