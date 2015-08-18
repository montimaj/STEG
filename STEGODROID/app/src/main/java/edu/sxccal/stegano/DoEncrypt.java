package edu.sxccal.stegano;

import com.google.zxing.WriterException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Main Encryption module
 * @author Sayantan Majumdar
 * @since 1.0
 */
public class DoEncrypt
{
    private static int ncols, nencrypt;
    DoEncrypt(String skey, String s, String imgfile) throws Exception
    {
        encrypt_file(skey,s,imgfile);
    }
    private void init_matrix(String s, int nrows, char mat[][])
    {
        int k=0;
        for(int i=0;i<nrows;++i)
            for(int j=0;j<ncols;++j)
                if(k<s.length())
                    mat[i][j]=s.charAt(k++);
    }

    /**
     * Initializes a 2D boolean flag matrix
     * @param s Input String
     * @param nrows row size
     * @param ncols column size
     * @param flag 2D boolean matrix
     */
    public static void init_matrix(String s, int nrows, int ncols, boolean flag[][])
    {
        int k=0;
        for(int i=0;i<nrows;++i)
        {
            for(int j=0;j<ncols;++j)
            {
                flag[i][j]=false;
                if(k++<s.length())
                    flag[i][j]=true;
            }
        }
    }
    private void generate_key(int key_arr[]) throws IOException
    {
        SecureRandom srand=new SecureRandom();
        DataOutputStream k=new DataOutputStream(new FileOutputStream(Stegano.filePath+"/key.txt"));
        boolean arr[]=new boolean[ncols];
        for(int i=0;i<ncols;++i)
        {
            int r=srand.nextInt(ncols);
            if(!arr[r])
            {
                key_arr[i]=r;
                k.writeInt(r);
            }
            else
                i--;
            arr[r]=true;
        }
        k.close();
    }
    /**
     * Converts ASCII to 8-bit binary string
     * @param cipher String containing the cipher text
     * @return Binary string
     */
    public static String String_to_bits(String cipher)
    {
        String bits="";
        for(int i=0;i<cipher.length();++i)
        {
            int mask=1<<7, n=cipher.charAt(i);
            while(mask!=0)
            {
                bits+=(n&mask)!=0?1:0;
                mask>>=1;
            }
        }
        return bits;
    }
    private String generate_cipher(int nrows, int key[], char mat[][], boolean flag[][])
    {
        String cipher_text="";
        for(int i=0;i<ncols;++i)
            for(int j=0;j<nrows;++j)
                if(flag[j][key[i]])
                    cipher_text+=mat[j][key[i]];
        return cipher_text;
    }
    /**
     * Converts bits to ASCII
     * @param bits Input bits in String format
     * @return ASCII string
     */
    public static String bits_to_ascii(String bits)
    {
        String ascii="";
        for(int i=0;i<bits.length();)
        {
            int asc=0,j,power=7;
            for(j=i;j<i+8 && j<bits.length();++j,--power)
            {
                int c=bits.charAt(j)-48;
                asc+=c*(int)Math.pow(2, power);
            }
            i=j;
            ascii+=(char)asc;
        }
        return ascii;
    }
    /**
     * Read file contents
     * @param s
     * @return
     * @throws IOException
     */
    public static String read_from_file(String s) throws IOException
    {
        FileInputStream fis=new FileInputStream(s);
        String data="";
        int c;
        while((c=fis.read())!=-1)
            data+=(char)c;
        fis.close();
        return data;
    }
    /**
     * Performs the encryption operation
     * <p>
     * Also encodes the cipher text and key file into a QRCode
     * </p>
     * @param s String containing the plain text
     * @throws IOException
     */
    private void encrypt_file(String skey, String s, String imgfile) throws Exception
    {
        GenKey gk=new GenKey(skey);
        ncols=gk.get_colsize();
        nencrypt=gk.get_encryption_number();
        String pt=read_from_file(s);
        int len=pt.length()*8,nrows=len/ncols;
        if(len>(nrows*ncols))
            nrows++;
        String binary_pt=String_to_bits(pt);
        boolean flag[][]=new boolean[nrows][ncols];
        init_matrix(binary_pt,nrows,ncols, flag);
        int k[]=new int[ncols];
        String cipher=binary_pt;
        generate_key(k);
        char mat[][]=new char[nrows][ncols];
        for(int i=0;i<nencrypt;++i)
        {
            init_matrix(cipher, nrows, mat);
            cipher=generate_cipher(nrows,k,mat, flag);
        }
        new DoStegano(cipher,imgfile);
        try {
            QRCode.encode(Stegano.filePath + "/steg.png", true);
            QRCode.encode(Stegano.filePath + "/key.txt", false);
        }
        catch (WriterException|IOException e){}
    }
}