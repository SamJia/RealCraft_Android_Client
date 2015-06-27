package com.example.realcraft;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.realcraft.SomeClass.Player;
import com.example.realcraft.SomeClass.Construction;

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

public class ConstructionInfoActivity extends ActionBarActivity {

    private static final int PLAYER_INFO = 1;
    private static final int CONSTRUCTION_INFO = 2;
    private static final int BUILD_INFO = 3;
    private static final int ATTACK_INFO = 4;
    private static final int ABANDON_INFO = 5;

    private Boolean constructionUpdated = false;
    private Boolean playerUpdated = false;

	private Button buttonBuild;
    private Button buttonAbandon;
	private Button buttonAttack;
    private TextView textViewBuilding;
    private TextView textViewPlayer;
    private Construction construction;
    private Player player;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_construction_info);
        
        Intent intent = getIntent();
        int construction_id = intent.getIntExtra("id", -1);
        construction = new Construction(construction_id);
        player = new Player();
        player.updatePosition(intent.getDoubleExtra("latitude", 0.0),
             intent.getDoubleExtra("longitude", 0.0));

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();

        buttonBuild = (Button)findViewById(R.id.buttonBuild);
        buttonAttack = (Button)findViewById(R.id.buttonAttack);
        buttonAbandon = (Button)findViewById(R.id.buttonAbandon);

        textViewBuilding = (TextView)findViewById(R.id.TextViewConstructionInfoList);
        textViewPlayer = (TextView)findViewById(R.id.TextViewSelfInfoList);
        

        
        buttonBuild.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if(buttonAbandon.getText().toString().equals(getResources().getString(R.string.stop))){
            		mHandler.removeCallbacks(actionAbandon);
            		buttonAbandon.setText(R.string.abandon);
            	}
            	if(buttonBuild.getText().toString().equals(getResources().getString(R.string.build))){
            		mHandler.postDelayed(actionBuild, 0);
            		buttonBuild.setText(R.string.stop);
            	}
            	else{
            		mHandler.removeCallbacks(actionBuild);
            		buttonBuild.setText(R.string.build);
            	}
            }
        });
        buttonAttack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if(buttonBuild.getText().toString().equals(getResources().getString(R.string.stop))){
            		mHandler.removeCallbacks(actionBuild);
            		buttonBuild.setText(R.string.build);
            	}
            	if(buttonAttack.getText().toString().equals(getResources().getString(R.string.attack))){
            		mHandler.postDelayed(actionAttack, 0);
            		buttonAttack.setText(R.string.stop);
            	}
            	else{
            		mHandler.removeCallbacks(actionAttack);
            		buttonAttack.setText(R.string.attack);
            	}
            }
        });
        buttonAbandon.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(buttonBuild.getText().toString().equals(getResources().getString(R.string.stop))){
                    mHandler.removeCallbacks(actionBuild);
                    buttonBuild.setText(R.string.build);
                }
                mHandler.postDelayed(actionAbandon, 0);
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
                        //       Toast.LENGTH_SHORT).show();
                        break;
                    case CONSTRUCTION_INFO:
                        constructionUpdated = true;
                        // Toast.makeText(getApplicationContext(), "update resoure info",
                        //       Toast.LENGTH_SHORT).show();
                        break;
                    case BUILD_INFO:
                        result = (Integer)msg.obj;
                        Toast.makeText(getApplicationContext(), String.valueOf(result),
                                 Toast.LENGTH_SHORT).show();
                        if((result < 0)){
                            mHandler.removeCallbacks(actionBuild);
                            buttonBuild.setText(R.string.build);
                        }
                        break;
                    case ATTACK_INFO:
                        result = (Integer)msg.obj;
                        Toast.makeText(getApplicationContext(), String.valueOf(result),
                                 Toast.LENGTH_SHORT).show();
                        if((result < 0)){
                            mHandler.removeCallbacks(actionAttack);
                            buttonAttack.setText(R.string.attack);
                        }
                        break;
                    case ABANDON_INFO:
                        result = (Integer)msg.obj;
                        Toast.makeText(getApplicationContext(), String.valueOf(result),
                                 Toast.LENGTH_SHORT).show();
                        break;
                    default:
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
            if(!constructionUpdated || !playerUpdated)
                return;
            if(construction.ownerId == -1){
                buttonBuild.setEnabled(true);
                buttonAbandon.setEnabled(false);
                buttonAttack.setEnabled(false);
            }
            else if(player.id == construction.ownerId){
                buttonBuild.setEnabled(true);
                buttonAbandon.setEnabled(true);
                buttonAttack.setEnabled(false);
            }
            else{
                buttonBuild.setEnabled(false);
                buttonAbandon.setEnabled(false);
                buttonAttack.setEnabled(true);
            }
        }
    };
	
	Runnable actionBuild=new Runnable() {
	    @Override
	    public void run() {
            mHandler.postDelayed(this, 5000);
            Toast.makeText(getApplicationContext(), "start build",
                     Toast.LENGTH_SHORT).show();
            new BuildThread().start();
	    }
	};

    public class BuildThread extends Thread{
        @Override
        public void run() {  
            super.run();
            try{
                int result = construction.build(player.getInfo());
                mHandler.obtainMessage(BUILD_INFO, result).sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();  
            }  
        }
    }

    Runnable actionAttack=new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 5000);
            Toast.makeText(getApplicationContext(), "start attack",
                     Toast.LENGTH_SHORT).show();
            new AttackThread().start();
        }
    };

    public class AttackThread extends Thread{
        @Override
        public void run() {  
            super.run();
            try{
                int result = construction.attack(player.getInfo());
                mHandler.obtainMessage(ATTACK_INFO, result).sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();  
            }  
        }  
    }

    Runnable actionAbandon=new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "start abandon",
                     Toast.LENGTH_SHORT).show();
            new AbandonThread().start();
        }
    };

    public class AbandonThread extends Thread{
        @Override
        public void run() {  
            super.run();
            try{
                int result = construction.abandon(player.getInfo());
                mHandler.obtainMessage(ABANDON_INFO, result).sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();  
            }  
        }  
    }

    Runnable actionUpdateConstructionInfo = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 2000);
            new UpdateConstructionInfoThread().start();
        }
    };

    public class UpdateConstructionInfoThread extends Thread{
        @Override
        public void run() {  
            super.run();
            try{
                construction.updateInfoFromServer();
                mHandler.obtainMessage(CONSTRUCTION_INFO, "null").sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();
            }  
        }  
    } 

	Runnable actionUpdateConstructionInfoView = new Runnable() {
	    @Override
	    public void run() {
	        textViewBuilding.setText(construction.getInfo());
	        mHandler.postDelayed(this, 500);
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
            // textViewPlayer.setText(player.getInfo());
	        textViewPlayer.setText(sb);
	        mHandler.postDelayed(this, 500);
	    }
	};
	@Override
	protected void onStart(){
        Toast.makeText(getApplicationContext(), "on start",
                 Toast.LENGTH_SHORT).show();
        buttonBuild.setEnabled(false);
        buttonBuild.setText(R.string.build);
        buttonAttack.setEnabled(false);
        buttonAttack.setText(R.string.attack);
        buttonAbandon.setEnabled(false);
        buttonAbandon.setText(R.string.abandon);
        mLocationClient.start();
        mHandler.post(actionUpdateConstructionInfo);
        mHandler.post(actionUpdateConstructionInfoView);
        mHandler.post(actionUpdatePlayerInfo);
        mHandler.post(actionUpdatePlayerInfoView);
        mHandler.post(actionUpdateButton);
		super.onStart();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "on stop",
                 Toast.LENGTH_SHORT).show();  
		mLocationClient.stop();
        mHandler.removeCallbacks(actionUpdateButton);
        mHandler.removeCallbacks(actionBuild);
        mHandler.removeCallbacks(actionAbandon);
        mHandler.removeCallbacks(actionAttack);
        mHandler.removeCallbacks(actionUpdateConstructionInfo);
        mHandler.removeCallbacks(actionUpdateConstructionInfoView);
        mHandler.removeCallbacks(actionUpdatePlayerInfo);
        mHandler.removeCallbacks(actionUpdatePlayerInfoView);
		super.onStop();
	}
}
