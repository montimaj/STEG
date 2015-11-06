package edu.sxccal.stegano;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class DoDecrypt
{
    public DoDecrypt(String skey, String keyfile, String imgfile) throws Exception
    {
        decrypt(skey,keyfile,imgfile);
    }
    private String extract_chars(int nrows, int ncols, char mat[][], boolean flag[][])
    {
        String s="";
        for(int i=0;i<nrows ;++i)
            for(int j=0;j<ncols;++j)
                if(flag[i][j])
                    s+=mat[i][j];
        return s;
    }
    private void init_matrix(String s, int nrows, int ncols, int num[], char mat[][], boolean flag[][]) throws IOException
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
    private void init_key_array(String keyfile, int num[]) throws IOException
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

    private void decrypt(String skey,String keyfile, String imgfile) throws Exception
    {
        //String imgfile=new QRCode(qrimg,dir,false).fname;
        //String keyfile=new QRCode(qrkey,dir,true).fname;
        String bits=new DoStegano(imgfile).getDecoded_str();
        GenKey gk=new GenKey(skey);
        int ncols=gk.get_colsize(),num[]=new int[ncols], len=bits.length(),nrows=len/ncols, ndecrypt=gk.get_encryption_number();
        init_key_array(keyfile, num);
        if(len>nrows*ncols)
            nrows++;
        boolean flag[][]=new boolean[nrows][ncols];
        DoEncrypt.init_matrix(bits, nrows, ncols, flag);
        char mat[][]=new char[nrows][ncols];
        for(int i=0;i<ndecrypt;++i)
        {
            init_matrix(bits, nrows, ncols, num, mat,flag);
            bits=extract_chars(nrows, ncols, mat, flag);
        }
        String dt=DoEncrypt.bits_to_ascii(bits);
        File out=new File(Stegano.filePath+"/Decrypted");
        if(!out.exists())
            out.mkdir();
        FileOutputStream dos=new FileOutputStream(out.getAbsolutePath()+"/decrypted.txt");
        for(int i=0;i<dt.length();++i)
            dos.write(dt.charAt(i));
        dos.close();
    }
}