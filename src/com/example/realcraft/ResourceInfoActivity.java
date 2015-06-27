package com.example.realcraft;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.realcraft.SomeClass.Player;
import com.example.realcraft.SomeClass.Resource;

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

public class ResourceInfoActivity extends ActionBarActivity {

	private static final int PLAYER_INFO = 1;
	private static final int RESOURCE_INFO = 2;
	private static final int COLLECT_INFO = 3;
	// private static int PLAYER_INFO = 4;


    private Boolean resourceUpdated = false;
    private Boolean playerUpdated = false;

	private Button buttonCollect;
    private TextView textViewResourceInfoList;
    private TextView textViewSelfInfoList;
    private Resource resource;
    private Player player;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resource_info);

        Intent intent = getIntent();
        int resource_id = intent.getIntExtra("id", -1);
        resource = new Resource(resource_id);
        player = new Player();
        player.updatePosition(intent.getDoubleExtra("latitude", 0.0),
             intent.getDoubleExtra("longitude", 0.0));

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();

		buttonCollect = (Button)findViewById(R.id.buttonCollect);
        textViewResourceInfoList = (TextView)findViewById(R.id.TextViewResourceInfoList);
        textViewSelfInfoList = (TextView)findViewById(R.id.TextViewSelfInfoList);
        

        // int player_id = intent.getIntExtra("player_id", -1);

        

        buttonCollect.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if(buttonCollect.getText().toString().equals(getResources().getString(R.string.collect))){
            		// addActionInNewThread(actionCollect, 0);
            		mHandler.postDelayed(actionCollect, 0);
            		buttonCollect.setText(R.string.stop);
            	}
            	else{
            		mHandler.removeCallbacks(actionCollect);
            		buttonCollect.setText(R.string.collect);
            	}
            }
        });
	}

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            player.updatePosition(location.getLatitude(), location.getLongitude());
        }
    }
	
    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
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
	                case RESOURCE_INFO:
                        resourceUpdated = true;
						// Toast.makeText(getApplicationContext(), "update resoure info",
						// 	     Toast.LENGTH_SHORT).show();
	                	break;
	                case COLLECT_INFO:
	                	result = (Integer)msg.obj;
						Toast.makeText(getApplicationContext(), String.valueOf(result),
							     Toast.LENGTH_SHORT).show();
	                    if((result < 0)){
				    		mHandler.removeCallbacks(actionCollect);
				    		buttonCollect.setText(R.string.collect);
	                    }
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
            if(!resourceUpdated || !playerUpdated)
                return;
            else
            	buttonCollect.setEnabled(true);
        }
    };

    Runnable actionCollect=new Runnable() {
	    @Override
	    public void run() {
       		mHandler.postDelayed(this, 5000);
			Toast.makeText(getApplicationContext(), "start collect",
				     Toast.LENGTH_SHORT).show();
	    	new CollectThread().start();
	    }
	};

	public class CollectThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		int result = resource.collect(player.getInfo());
				mHandler.obtainMessage(COLLECT_INFO, result).sendToTarget();
        	}catch (Exception e) {
                e.printStackTrace();  
            }  
        }  
    } 

	Runnable actionUpdateResourceInfo = new Runnable() {
	    @Override
	    public void run() {
	        mHandler.postDelayed(this, 2000);
	    	new UpdateResourceInfoThread().start();
	    }
	};

	public class UpdateResourceInfoThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		resource.updateInfoFromServer();
				mHandler.obtainMessage(RESOURCE_INFO, "null").sendToTarget();
        	}catch (Exception e) {
                e.printStackTrace();
            }  
        }  
    } 

	Runnable actionUpdateResourceInfoView = new Runnable() {
	    @Override
	    public void run() {
	        mHandler.postDelayed(this, 500);
	        textViewResourceInfoList.setText(resource.getInfo());
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
            // textViewSelfInfoList.setText(player.getInfo());
	        textViewSelfInfoList.setText(sb);
            mHandler.postDelayed(this, 500);
	    }
	};
	@Override
	protected void onStart(){
        mLocationClient.start();    
		Toast.makeText(getApplicationContext(), "on start",
			     Toast.LENGTH_SHORT).show();  
		buttonCollect.setEnabled(false);
        mHandler.post(actionUpdateButton);
        mHandler.post(actionUpdateResourceInfo);
        mHandler.post(actionUpdateResourceInfoView);
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
        mHandler.removeCallbacks(actionCollect);
        mHandler.removeCallbacks(actionUpdateResourceInfo);
        mHandler.removeCallbacks(actionUpdateResourceInfoView);
        mHandler.removeCallbacks(actionUpdatePlayerInfo);
        mHandler.removeCallbacks(actionUpdatePlayerInfoView);
		mLocationClient.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();
        Toast.makeText(getApplicationContext(), "onDestroy",
         Toast.LENGTH_SHORT).show();
	    super.onDestroy();
    };
}
