package edu.sxccal.stegano;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This is the main activity
 * @since 1.0
 */
public class Stegano extends Activity implements OnClickListener
{
	/**
	 * @param filePath External storage directory path
	 */
	private Button scanBtn1,scanBtn2,encr,decr,ab,dqr;
	private boolean flag;
	public static String scanContent="No result";
	public static final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Stegano";
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);        
        //load the main activity layout
        setContentView(R.layout.activity_stegano);
        //Create directory 
        File dir=new File(Stegano.filePath);
        if(!dir.exists())
        	dir.mkdir();        
        //Check which button is pressed
        scanBtn1 = (Button)findViewById(R.id.scan_button);
        scanBtn2=(Button)findViewById(R.id.scan_key);
		decr=(Button)findViewById(R.id.decrypt);
        ab=(Button)findViewById(R.id.ab);
        encr=(Button)findViewById(R.id.encrypt);
        dqr=(Button)findViewById(R.id.decode);
        ab.setOnClickListener(this);    
        decr.setOnClickListener(this);
        scanBtn1.setOnClickListener(this);
        scanBtn2.setOnClickListener(this);
		encr.setOnClickListener(this);
        dqr.setOnClickListener(this);
    }

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.scan_button)
		{			
			flag=false;
            IntentIntegrator scanner = new IntentIntegrator(this); //Zxing android interface library
			scanner.initiateScan(); //Requires BarcodeScanner app by Zxing to be installed in the phone			
		}
		if(v.getId()==R.id.scan_key)
		{
			flag=true;
			IntentIntegrator scanner = new IntentIntegrator(this);
			scanner.initiateScan();
		}
		if(v.getId()==R.id.ab)
		{
			Intent about=new Intent(Stegano.this,About.class);
			startActivity(about);
		}
		if(v.getId()==R.id.decrypt)
		{
			Intent verify= new Intent(Stegano.this,Decrypt.class);
        	startActivity(verify);      
		}
		if(v.getId()==R.id.encrypt)
		{
			Intent qr=new Intent(Stegano.this,Encrypt.class);
			startActivity(qr);
		}
		if(v.getId()==R.id.decode)
		{
			Intent qr=new Intent(Stegano.this,DecodeQR.class);
			startActivity(qr);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(intent!=null)
		{
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);		
			if (scanningResult != null)
			{
				scanContent = scanningResult.getContents();		
				if(checkExternalMedia())
					write_to_file(flag);
			}
		}
		else
		{
		    Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
		    toast.setGravity(Gravity.CENTER,0,0);
		    toast.show();		    
		}	
	}

	/**
	 * checks if there is read and write access to device storage
	 * @return true if media has both RW access false otherwise
	 */
	public boolean checkExternalMedia()
	{
		    boolean readable;
		    boolean writeable;
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state))
		        readable = writeable = true;
		    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		    {		        
		        readable = true;
		        writeable = false;
		    }
		    else
		        readable = writeable = false;
		    return (readable && writeable);
	}		
	public void write_to_file(boolean flag)
	{			 			
		    File dir = new File (filePath),file;
			if(flag)
		    	file = new File(dir, "decoded_key.txt");
			else
				file= new File(dir,"decoded_img.png");
		    try 
		    {
		        FileOutputStream fos = new FileOutputStream(file);
				if(!flag)
					fos.write(scanContent.getBytes("ISO-8859-1"));
				else
					fos.write(Base64.decode(scanContent,Base64.DEFAULT));
		        fos.close();
		    } 
		    catch(IOException e)
		    {		    	
		    	Log.create_log(e, getApplicationContext()); //Write logs to log.txt
		    }
			Toast toast = Toast.makeText(getApplicationContext(),"File written to: "+file, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER,0,0);
			toast.show();
	}	
}