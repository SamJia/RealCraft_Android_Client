package com.example.realcraft;

import com.example.realcraft.SomeClass.Construction;
import com.example.realcraft.SomeClass.HttpUtil;
import com.example.realcraft.SomeClass.Item;
import com.example.realcraft.SomeClass.Player;
import com.example.realcraft.SomeClass.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.app.Activity;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
//import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private static final int PLAYER_INFO = 1;
	private Boolean playerUpdated = false;

	MapView mMapView = null;  
	private Button buttonStartLocation;
	private Button buttonHire;
	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	private BaiduMap mBaiduMap;
	boolean isFirstLoc = true;
	boolean moveToCenter = true;
	private InfoWindow mInfoWindow;
	BitmapDescriptor bds[];
	private List<Marker> markers = new ArrayList<Marker>();
	private List<Item> items = new ArrayList<Item>();
	private double latitude, longitude;
	private Player player = new Player();
	private TextView textViewFood;
	private TextView textViewStone;
	private TextView textViewWood;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);  
        //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
        // mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		InitLocation();
		textViewFood = (TextView) findViewById(R.id.textViewFood);
		textViewStone = (TextView) findViewById(R.id.textViewStone);
		textViewWood = (TextView) findViewById(R.id.textViewWood);
		buttonStartLocation = (Button)findViewById(R.id.request);
		buttonStartLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveToCenter = true;
			}
		});
		buttonHire = (Button)findViewById(R.id.buttonHire);
		buttonHire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, HireWorkforceActivity.class);
				// Toast.makeText(getApplicationContext(), ""+index,
				// 	     Toast.LENGTH_SHORT).show();
			    startActivity(intent);
			}
		});
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap
		.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
		initOverlay();
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				button.setText(R.string.check_info);
				OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
						Intent intent = new Intent();
						int index = markers.indexOf(marker);
						int itemType = ((Item)items.get(index)).getType();
						if(itemType == 1)
							intent.setClass(MainActivity.this, ResourceInfoActivity.class);
						else
							intent.setClass(MainActivity.this, ConstructionInfoActivity.class);
						// Toast.makeText(getApplicationContext(), ""+index,
						// 	     Toast.LENGTH_SHORT).show();
						intent.putExtra("id", ((Item)items.get(index)).id);
						intent.putExtra("latitude", latitude);
						intent.putExtra("longitude", longitude);
					    startActivity(intent);
					}
				};
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
    }
	private Handler mHandler = new Handler(){
		public void handleMessage (Message msg) {
			try{
				int result = -100;
				switch(msg.what) {
					case PLAYER_INFO:
						playerUpdated = true;
//						 Toast.makeText(getApplicationContext(), "update player info",
//						       Toast.LENGTH_SHORT).show();
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

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
	        MyLocationData locData = new MyLocationData.Builder()
		            .accuracy(location.getRadius())
		            .direction(location.getDirection()).latitude(location.getLatitude())
		            .longitude(location.getLongitude()).build();
	        mBaiduMap.setMyLocationData(locData);
	        latitude = location.getLatitude();
	        longitude = location.getLongitude();
	        if(isFirstLoc){
	        	isFirstLoc = false;
	        	 mHandler.post(actionUpdateOverlay);
	        }
	        if(moveToCenter){
	        	moveToCenter = false;
	            LatLng ll = new LatLng(location.getLatitude(),
	                    location.getLongitude());
	            MapStatus mapStatus = new MapStatus.Builder()
	            		.target(ll).zoom(18).build();
	            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
	            mBaiduMap.animateMapStatus(u);
				Toast.makeText(getApplicationContext(), "moveToCenter",
					     Toast.LENGTH_SHORT).show();
	        }
//			Toast.makeText(getApplicationContext(), "SetLocationDone",
//				     Toast.LENGTH_SHORT).show();
//			mLocationClient.stop();
		}
	}
	

	public List<Item> getNewItems(){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        String latitudeString = Double.toString(latitude);
        String longitudeString = Double.toString(longitude);
        params.add(new BasicNameValuePair("latitude", latitudeString));
        params.add(new BasicNameValuePair("longitude", longitudeString));
        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/request/download",params);
		// String response = HttpUtil.HttpClientGET("http://112.74.98.74/request/download");
		
		int id, type;
		double latitude, longitude;
		List<Item> newItems = new ArrayList<Item>();
		try{
			JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//    			Toast.makeText(getApplicationContext(), jsonObject.getString("location"),
//			     Toast.LENGTH_SHORT).show();
                id = Integer.parseInt(jsonObject.getString("id"));
                type = Integer.parseInt(jsonObject.getString("type"));
                latitude = Double.parseDouble(jsonObject.getString("latitude"));
                longitude = Double.parseDouble(jsonObject.getString("longitude"));
                if(type == 1)
                	newItems.add(new Resource(id, latitude, longitude));
                else
                	newItems.add(new Construction(id, latitude, longitude));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
		return newItems;
	}
	
	Runnable actionUpdateOverlay = new Runnable() {
	   @Override
	    public void run() {
	        mHandler.postDelayed(this, 10000);
			new UpdateOverlayThread().start();
	    }
	};

	public class UpdateOverlayThread extends Thread{
        @Override
        public void run() {  
        	super.run();
        	try{
        		List<Item> new_items = getNewItems();
				for(Iterator i = new_items.iterator(); i.hasNext();){
					Item r = (Item) i.next();
					if(!items.contains(r) && items.size() < 100){
						LatLng ll = new LatLng(r.latitude, r.longitude);
						OverlayOptions oo = new MarkerOptions().position(ll).icon(bds[r.getType()])
								.zIndex(9).draggable(true);
						Marker marker = (Marker) mBaiduMap.addOverlay(oo);
						marker.setDraggable(false);
						items.add(r);
						markers.add(marker);
					}
				}
        	} catch (Exception e) {  
	            Log.d("Thread", "send message failure");
                e.printStackTrace();  
            }  
        }  
    }

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
//			Toast.makeText(getApplicationContext(), "update player info begin",
//					Toast.LENGTH_SHORT).show();
			if(playerUpdated) {
//				Toast.makeText(getApplicationContext(), "update player info",
//					Toast.LENGTH_SHORT).show();
				textViewFood.setText(String.valueOf((Integer) playerInfo.get("food")));
				textViewStone.setText(String.valueOf((Integer) playerInfo.get("stone")));
				textViewWood.setText(String.valueOf((Integer) playerInfo.get("wood")));
			}
			mHandler.postDelayed(this, 500);
		}
	};
	
	public void initOverlay() {
		bds = new BitmapDescriptor[3];
		bds[0] = null;
		bds[1] = BitmapDescriptorFactory
				.fromResource(R.drawable.resources_mini);;
		bds[2] = BitmapDescriptorFactory
				.fromResource(R.drawable.house_a);;
	}
	
    @Override  
    protected void onDestroy() {  
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();
        for(int i = 0; i < bds.length; ++i){
        	if(bds[i] != null)
        		bds[i].recycle();
        }
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		isFirstLoc = true;
		playerUpdated = false;
		mLocationClient.start();
		mHandler.post(actionUpdatePlayerInfo);
		mHandler.post(actionUpdatePlayerInfoView);
		super.onStop();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(actionUpdateOverlay);
		mLocationClient.stop();
		mHandler.removeCallbacks(actionUpdatePlayerInfo);
		mHandler.removeCallbacks(actionUpdatePlayerInfoView);
		super.onStop();
	}
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
	}

	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
			Toast.makeText(getApplicationContext(), "Logout",
				     Toast.LENGTH_SHORT).show();
			setResult(1);
            finish();
        }
        else if (id == R.id.action_exit) {
			Toast.makeText(getApplicationContext(), "Exit",
				     Toast.LENGTH_SHORT).show();
			setResult(0);
			finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
