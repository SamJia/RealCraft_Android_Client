package com.example.realcraft;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.realcraft.SomeClass.Player;
import com.example.realcraft.SomeClass.Resource;
import com.example.realcraft.SomeClass.Workforce;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class HireWorkforceActivity extends ActionBarActivity {

	private static final int PLAYER_INFO = 1;
	private static final int WORKFORCE_INFO = 2;
	private static final int HIRE_INFO = 3;

    private Boolean workforceUpdated = false;
    private Boolean playerUpdated = false;

	TextView textViewSelfInfoList;
	TextView textViewWorkforceInfoList;
	Button buttonHire;

    private Player player;
    private Workforce workforce;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hire_workforce);

//		Intent intent = getIntent();

		buttonHire = (Button)findViewById(R.id.buttonHire);
        textViewWorkforceInfoList = (TextView)findViewById(R.id.TextViewWorkforceInfoList);
        textViewSelfInfoList = (TextView)findViewById(R.id.TextViewSelfInfoList);
        
        player = new Player();
        workforce = new Workforce();
        

        buttonHire.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
        		mHandler.postDelayed(actionHire, 0);
            }
        });
	}

	Handler mHandler=new Handler(){
		public void handleMessage (Message msg) {
			try{
				int result = -100;
	            switch(msg.what) {
	                case PLAYER_INFO:
                        playerUpdated = true;
						// Toast.makeText(getApplicationContext(), "update player info",
						// 	     Toast.LENGTH_SHORT).show();
	                	break;
	                case WORKFORCE_INFO:
                        workforceUpdated = true;
						// Toast.makeText(getApplicationContext(), "update resoure info",
						// 	     Toast.LENGTH_SHORT).show();
	                	break;
	                case HIRE_INFO:
	                	result = (Integer)msg.obj;
						Toast.makeText(getApplicationContext(), String.valueOf(result),
							     Toast.LENGTH_SHORT).show();
						workforceUpdated = false;
	                    break;
	                default:
						// Toast.makeText(getApplicationContext(), "other message",
						// 	     Toast.LENGTH_SHORT).show();
	                	break;
	            }	
			} catch(Exception e){
				Log.d("message", "handle message failed");
				e.printStackTrace();
			}
        }  
	};

    Runnable actionUpdateButton=new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 500);
            if(!workforceUpdated || !playerUpdated)
                return;
            else
            	buttonHire.setEnabled(true);
        }
    };

    Runnable actionHire=new Runnable() {
	    @Override
	    public void run() {
//    		mHandler.postDelayed(this, 5000);
			// Toast.makeText(getApplicationContext(), "start collect",
			// 	     Toast.LENGTH_SHORT).show();
	    	new HireThread().start();
	    }
	};

	public class HireThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		int result = workforce.hire(player.getInfo());
				mHandler.obtainMessage(HIRE_INFO, result).sendToTarget();
        	}catch (Exception e) {
                e.printStackTrace();
            }  
        }  
    }

	Runnable actionUpdateWorkforceInfo = new Runnable() {
	    @Override
	    public void run() {
	        mHandler.postDelayed(this, 2000);
	    	new UpdateWorkforceInfoThread().start();
	    }
	};

	public class UpdateWorkforceInfoThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		workforce.updateInfoFromServer();
				mHandler.obtainMessage(WORKFORCE_INFO, "null").sendToTarget();
        	}catch (Exception e) {
                e.printStackTrace();
            }  
        }  
    } 

   	Runnable actionUpdateWorkforceInfoView = new Runnable() {
	    @Override
	    public void run() {
	        mHandler.postDelayed(this, 500);
	        textViewWorkforceInfoList.setText(workforce.getInfo());
	    }
	};

	Runnable actionUpdatePlayerInfo = new Runnable() {
	    @Override
	    public void run() {
	        mHandler.postDelayed(this, 2000);
	        new UpdatePlayerInfoThread().start();
	    }
	};

	public class UpdatePlayerInfoThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
		        player.updateInfoFromServer();
				mHandler.obtainMessage(PLAYER_INFO, "null").sendToTarget();
        	}catch (Exception e) {
                e.printStackTrace();
            }  
        }  
    } 


	Runnable actionUpdatePlayerInfoView = new Runnable() {
	    @Override
	    public void run() {
			Map playerInfo = player.getInfo();
			StringBuffer sb = new StringBuffer(256);
            sb.append("name:");
            sb.append((String)playerInfo.get("name"));
            sb.append("\nwood:");
            sb.append(String.valueOf((Integer) playerInfo.get("wood")));
            sb.append("\nstone:");
            sb.append(String.valueOf((Integer) playerInfo.get("stone")));
            sb.append("\nfood:");
            sb.append(String.valueOf((Integer) playerInfo.get("food")));
            sb.append("\nworkforce:");
            sb.append(String.valueOf((Integer) playerInfo.get("workforce")));
            sb.append("\nlatitude:");
            sb.append(String.valueOf((Double) playerInfo.get("latitude")));
            sb.append("\nlongitude:");
            sb.append(String.valueOf((Double) playerInfo.get("longitude")));
	        textViewSelfInfoList.setText(sb);
	        mHandler.postDelayed(this, 500);
	    }
	};
	@Override
	protected void onStart(){  
		Toast.makeText(getApplicationContext(), "on start",
			     Toast.LENGTH_SHORT).show();  
		buttonHire.setEnabled(false);
	    mHandler.post(actionUpdateButton);
	    mHandler.post(actionUpdateWorkforceInfo);
		mHandler.post(actionUpdateWorkforceInfoView);
		mHandler.post(actionUpdatePlayerInfo);
		mHandler.post(actionUpdatePlayerInfoView);
		super.onStart();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "on stop",
			     Toast.LENGTH_SHORT).show();  
        mHandler.removeCallbacks(actionUpdateButton);
        mHandler.removeCallbacks(actionHire);
        mHandler.removeCallbacks(actionUpdateWorkforceInfo);
        mHandler.removeCallbacks(actionUpdateWorkforceInfoView);
        mHandler.removeCallbacks(actionUpdatePlayerInfo);
        mHandler.removeCallbacks(actionUpdatePlayerInfoView);
		super.onStop();
	}
}
