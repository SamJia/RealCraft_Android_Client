package com.example.realcraft;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.realcraft.SomeClass.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {

    private static final int REGISTER_MESSAGE = 1;

    private Button button_submit;
    private EditText register_email;
    private EditText register_rePassword;
    private EditText register_name ;
    private EditText register_password;
    private String inputTextName;
    private String inputTextPassword;
    private String inputTextEmail;
    private String inputTextRePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button_submit = (Button)findViewById(R.id.register_submit);
        register_name = (EditText) findViewById(R.id.register_name);
        register_password = (EditText) findViewById(R.id.register_password);
        register_rePassword = (EditText) findViewById(R.id.register_re_password);
        register_email = (EditText) findViewById(R.id.register_email);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mHandler.post(actionRegister);
            }
        });
    }


    private Handler mHandler = new Handler(){
        public void handleMessage (Message msg) {
            switch(msg.what) {
                case REGISTER_MESSAGE:
                    if((((String) msg.obj).equals("1"))){
                        Toast.makeText(RegisterActivity.this,R.string.turning,Toast.LENGTH_SHORT).show();
	                    setResult(3);
	                    finish();
                    }else{
                        Toast.makeText(RegisterActivity.this,R.string.register_fail,Toast.LENGTH_SHORT).show();
                    }
            }
        }  
    };

	Runnable actionRegister = new Runnable(){
		@Override
		public void run() {
            Toast.makeText(RegisterActivity.this,R.string.register_wait,Toast.LENGTH_SHORT).show();
            new RegisterThread().start();
        }
	};
	
	public class RegisterThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		inputTextName = register_name.getText().toString();
                inputTextPassword = register_password.getText().toString();
                inputTextEmail = register_email.getText().toString();
                inputTextRePassword = register_rePassword.getText().toString();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", inputTextName));
                params.add(new BasicNameValuePair("password", inputTextPassword));
                params.add(new BasicNameValuePair("passconf",inputTextRePassword));
                params.add(new BasicNameValuePair("email", inputTextEmail));
                String response = HttpUtil.HttpClientPOST("http://112.74.98.74/account/register",params);
                Log.d("Test", inputTextName);
                Log.d("Test",inputTextPassword);
                Log.d("Test",inputTextEmail);
                Log.d("Test",inputTextRePassword);
                Log.d("Test", "response" + response);
	            Log.d("Thread", "start send message");
	            mHandler.obtainMessage(1, response).sendToTarget();
	            Log.d("Thread", "send message successful");
        	} catch (Exception e) {  
	            Log.d("Thread", "send message failure");
                e.printStackTrace();  
            }  
        }  
    }  

}
