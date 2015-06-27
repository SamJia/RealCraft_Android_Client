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
public class LoginActivity extends ActionBarActivity {

    private static final int LOGIN_MESSAGE = 1;
    // private static final int LOGIN_FAILURE = 0;

    private  Button button_submit;
    private Button button_register;
    private EditText login_name ;
    private EditText login_password;
    private  String inputTextName;
    private String inputTextPassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button_submit = (Button)findViewById(R.id.login_submit);
        button_register = (Button)findViewById(R.id.turn_to_register);
        login_name = (EditText) findViewById(R.id.login_name);
        login_password = (EditText) findViewById(R.id.login_password);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.d("Thread", "login button click");
                mHandler.post(actionLogin);
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent, 2);
            }
        });
	}

    private Handler mHandler = new Handler(){
        public void handleMessage (Message msg) {
            switch(msg.what) {
                case LOGIN_MESSAGE:
                    if((((String) msg.obj).equals("1"))){
                        Toast.makeText(LoginActivity.this,R.string.turning,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivityForResult(intent, 3);
                    }else{
                        Toast.makeText(LoginActivity.this,R.string.login_fail,Toast.LENGTH_SHORT).show();
                    }
            }
        }  
    };

	Runnable actionLogin = new Runnable(){
		@Override
		public void run() {
            Toast.makeText(LoginActivity.this,R.string.login_wait,Toast.LENGTH_SHORT).show();
            new LoginThread().start();
        }
	};
	
	public class LoginThread extends Thread{
        @Override
        public void run() {
        	super.run();  
        	try{
	            Log.d("Thread", "should be in new thread");
	            inputTextName = login_name.getText().toString();
	            inputTextPassword = login_password.getText().toString();
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username",inputTextName));
	            params.add(new BasicNameValuePair("password", inputTextPassword));
	            String response = HttpUtil.HttpClientPOST("http://112.74.98.74/account/login",params);
	            Log.d("Test",inputTextName);
	            Log.d("Test",inputTextPassword);
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
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == 3)
        {                
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(intent, 3);
        }
        else if(requestCode == 3){
            if(resultCode == 0){
                onBackPressed();
            }
            else if(resultCode == 1){
                //None
            }
        }
    }
}
