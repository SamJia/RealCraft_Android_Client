package com.example.realcraft.SomeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


public class Resource extends Item{
	public int wood;
	public int food;
	public int stone;
	public String origin;
	public String origin2;
	
	Resource(){
		super();
	}
	
	public Resource(int init_id){
		id = init_id;
	}
	
	public Resource(int init_id, double init_latitud, double init_longitud){
		super(init_id, init_latitud, init_longitud);
	}

	@Override
	public int getType(){
		return 1;
	}


	public void updateInfoFromServer(){
		String Id = Integer.toString(id);
        String Type = "1";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("targetId", Id));
        params.add(new BasicNameValuePair("targetType", Type));
        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/request/detail",params);
        origin = response;
        try{
            JSONObject jsonObject = new JSONObject(response);
            wood = Integer.parseInt(jsonObject.getString("wood"));
            stone = Integer.parseInt(jsonObject.getString("stone"));
            food = Integer.parseInt(jsonObject.getString("food"));
        } catch (Exception e){
            e.printStackTrace();
        }
	}

	public int collect(Map playerInfo) {
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
        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/operation/collect",params);
        origin2 = response;
        int res = -64;
        try {
            res = Integer.parseInt(response);
        } catch (Exception e){

            res = -64;
        }
        return res;
	}

	public CharSequence getInfo() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("ID:");
        sb.append(id);
		sb.append("\nwood:");
		sb.append(wood);
		sb.append("\nfood:");
		sb.append(food);
		sb.append("\nstone:");
		sb.append(stone);
		sb.append("\norigin:");
		sb.append(origin);
		sb.append("\norigin2:");
		sb.append(origin2);
		return sb;
	}


}
