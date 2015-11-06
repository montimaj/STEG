package edu.sxccal.stegano;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Uses {@link DoEncrypt} module to display
 *the paths of the generated files
 *if successful
 *else raises a Toast that displays the path to Log.txt
 */
public class Encrypt extends Activity implements Runnable,View.OnClickListener
{		
	private static ProgressDialog dialog;
	private static String f,img, skey;
	public static Exception except;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encrypt);
        Button bt1=(Button)findViewById(R.id.doencr);
		Button bt2=(Button)findViewById(R.id.select_img);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
        final EditText passwd=(EditText)findViewById(R.id.secret_key);
        passwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((!f.isEmpty() || !img.isEmpty()) && actionId == EditorInfo.IME_ACTION_DONE) {
                    skey = passwd.getText().toString();
                    if (f.isEmpty() || img.isEmpty() || skey.isEmpty())
                        Log.create_log(new IOException("Invalid Input"), getApplicationContext());
                    else {
                        dialog = ProgressDialog.show(Encrypt.this, "Encrypting...",
                                "Please wait!", true, false);
                        Thread thread = new Thread(Encrypt.this);
                        thread.start();
                        return true;
                    }
                }
                return false;
            }
        });
	}
	@Override
	public void onClick(View v)
	{
		try
        {
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("file/*");
            if (v.getId() == R.id.doencr)
                startActivityForResult(fileintent, 1);
            else
                startActivityForResult(fileintent,2);
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
			  case 1:
			   if(resultCode==RESULT_OK)
			    f = data.getData().getPath();
			   break;
              case 2:
                  if(resultCode==RESULT_OK)
                      img = data.getData().getPath();
		  }
	}
	@Override
	public void run()
	{	
		try
		{
			new DoEncrypt(skey,f,img);
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
		public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1)
                Log.create_log(except, dialog.getContext());
            else {
                Toast toast = Toast.makeText(dialog.getContext(), "Success!\nGenerated files are in: " + Stegano.filePath, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
 	};	
}

