package com.example.realcraft.SomeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


public class Construction extends Item{

    public static final int CONSTRUCTION_DONE = 1;
    public static final int CONSTRUCTION_DESTORY = 2;

	int hp;
	int maxHp = 10;
	public int ownerId;
	public String ownerName;
//	public Player owner;
	public String origin;
	
	Construction(){
		super();
	}
	
	public Construction(int init_id){
		id = init_id;
	}

	public Construction(int init_id, double init_latitud, double init_longitud){
		super(init_id, init_latitud, init_longitud);
	}

	@Override
	public int getType(){
		return 2;
	}

	public int updateInfoFromServer(){
		String Id = Integer.toString(id);
        String Type = "2";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("targetId", Id));
        params.add(new BasicNameValuePair("targetType", Type));
        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/request/detail",params);
        origin = response;
        try{
            JSONObject jsonObject = new JSONObject(response);
            ownerName = jsonObject.getString("username");
            hp = Integer.parseInt(jsonObject.getString("value"));
            maxHp = Integer.parseInt(jsonObject.getString("maxdurability"));
            ownerId = Integer.parseInt(jsonObject.getString("playerId"));
        } catch (Exception e){
            e.printStackTrace();
        }
        if(hp == maxHp)
        	return CONSTRUCTION_DONE;
        else if(hp == 0)
        	return CONSTRUCTION_DESTORY;
        return hp;
	}

	public int build(Map playerInfo) {
		if(hp<maxHp){
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	        String Id = Integer.toString(id);
	        String latitudeString = Double.toString((Double) playerInfo.get("latitude"));
	        String longitudeString = Double.toString((Double) playerInfo.get("longitude"));
	        params.add(new BasicNameValuePair("targetId", Id));
	        params.add(new BasicNameValuePair("latitude", latitudeString));
	        params.add(new BasicNameValuePair("longitude", longitudeString));
	        String vc = String.valueOf((Integer) playerInfo.get("wood")) + ","
	                    + String.valueOf((Integer) playerInfo.get("stone")) + ","
	                    + String.valueOf((Integer) playerInfo.get("food"));
	        params.add(new BasicNameValuePair("vc", Encrypt.encrypt(vc)));
	        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/operation/build",params);
	        int res = -64;
	        try {
	            res = Integer.parseInt(response);
	        } catch (Exception e){
	            res = -64;
	        }
//	        if(res<0)return false;
//	        return true;
	        return res;
	    }
	    else
	    	return -5;
	}
	
	public int attack(Map playerInfo){
		if(hp>0){
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        String Id = Integer.toString(id);
	        String latitudeString = Double.toString((Double) playerInfo.get("latitude"));
	        String longitudeString = Double.toString((Double) playerInfo.get("longitude"));
	        params.add(new BasicNameValuePair("targetId", Id));
	        params.add(new BasicNameValuePair("latitude", latitudeString));
	        params.add(new BasicNameValuePair("longitude", longitudeString));
	        String vc = String.valueOf((Integer) playerInfo.get("wood")) + ","
	                    + String.valueOf((Integer) playerInfo.get("stone")) + ","
	                    + String.valueOf((Integer) playerInfo.get("food"));
	        params.add(new BasicNameValuePair("vc", Encrypt.encrypt(vc)));
	        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/operation/attack",params);
	        int res = -64;
	        try {
	            res = Integer.parseInt(response);
	        } catch (Exception e){
	            res = -64;
	        }
	        return res;
//	        if(res<0)return false;
//	        else return true;
	    }
	    else
	    	return -5;
//	    	return false;
	}

	public int abandon(Map playerInfo){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String Id = Integer.toString(id);
        String latitudeString = Double.toString((Double) playerInfo.get("latitude"));
        String longitudeString = Double.toString((Double) playerInfo.get("longitude"));
	    params.add(new BasicNameValuePair("targetId", Id));
        params.add(new BasicNameValuePair("latitude", latitudeString));
        params.add(new BasicNameValuePair("longitude", longitudeString));
        String vc = String.valueOf((Integer) playerInfo.get("wood")) + ","
                    + String.valueOf((Integer) playerInfo.get("stone")) + ","
                    + String.valueOf((Integer) playerInfo.get("food"));
        params.add(new BasicNameValuePair("vc", Encrypt.encrypt(vc)));
	    String response = HttpUtil.HttpClientPOST("http://112.74.98.74/operation/abandon",params);
        int res = -64;
        try {
            res = Integer.parseInt(response);
        } catch (Exception e){
            res = -64;
        }
        return res;
//        if(res<0)return false;
//        else return true;
	}

	public void setMAXHP(int newMAXHP){
		maxHp = newMAXHP;
	}

	public CharSequence getInfo() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("ID:");
        sb.append(id);
        sb.append("\nHP:");
        sb.append(hp);
        sb.append("\nownerId:");
        sb.append(ownerId);
        sb.append("\nownerName:");
        sb.append(ownerName);
        sb.append("\norigin:");
        sb.append(origin);
		return sb;
	}

}
