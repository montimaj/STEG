package edu.sxccal.stegano;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.File;

/**
 * Uses {@link edu.sxccal.stegano.QR} module to display
 *the paths of the generated Stegano.png, sig and suepk files
 *if successful
 *else raises a Toast that displays the path to Log.txt
 */
public class Encrypt extends Activity implements Runnable,View.OnClickListener
{		
	private static ProgressDialog dialog;
	public static TextView tv;	
	private Button bt;
	private String f;
	public final int PICKFILE_RESULT_CODE = 1;
	public static Exception except;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encrypt);
		tv=(TextView)findViewById(R.id.wfile);
		bt=(Button)findViewById(R.id.doencr);
		bt.setOnClickListener(this);       	
	}

	@Override
	public void onClick(View v)
	{
		Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("file/*");
        try 
        {
            startActivityForResult(fileintent,PICKFILE_RESULT_CODE);            
        } 
        catch (Exception e) 
        {
            Log.create_log(e, getApplicationContext());
        }	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{		  
		  switch(requestCode)
		  {
			  case PICKFILE_RESULT_CODE:
			   if(resultCode==RESULT_OK)
			   {
				    f = data.getData().getPath();	
				    File file=new File(f);
		        	if(!file.exists())
		        	{
		        		tv.setText("");
		        		Log.create_log(new Exception("File not found"), getApplicationContext());
		        	}
		        	else
		        	{
			        	tv.setText("");
		        		dialog = ProgressDialog.show(Encrypt.this, "Enrypting...",
			                     "Please wait!", true,false);	
			        	Thread thread = new Thread(Encrypt.this);
		                thread.start();
		        	}		    
			   }
			   break;		   
		  }
	}

	@Override
	public void run()
	{	
		try
		{
			QR.generateQRCode(f);	
			handler.sendEmptyMessage(0);
		}
		catch(Exception e)
		{			
			except=e;
			handler.sendEmptyMessage(1);
		}
			
	}
	private static Handler handler = new Handler()
	{
        @Override
		public void handleMessage(Message msg)
         	{
         		dialog.dismiss();
         		if(msg.what==1)
         			Log.create_log(except, dialog.getContext());
				tv.setText(QR.str);
         		QR.str="";
         	}
         	
 	};	
}

