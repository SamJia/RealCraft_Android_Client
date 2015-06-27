package com.example.realcraft.SomeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;


public class Workforce extends Item{
	public int cost = 0;
    public String origin;
	public String origin2;
	
	public Workforce(){
		super();
	}

	public void updateInfoFromServer(){
        String response = HttpUtil.HttpClientGET("http://112.74.98.74/request/workforceCost");
        origin = response;
        try{
            cost = Integer.parseInt(response);
        } catch (Exception e){
            e.printStackTrace();
        }
	}

    public int hire(Map playerInfo){
        int food = (Integer) playerInfo.get("food");
    	if(food < cost)
    		return -5;
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
        String vc = String.valueOf((Integer) playerInfo.get("wood")) + ","
                    + String.valueOf((Integer) playerInfo.get("stone")) + ","
                    + String.valueOf((Integer) playerInfo.get("food"));
        params.add(new BasicNameValuePair("vc", Encrypt.encrypt(vc)));
        String response = HttpUtil.HttpClientPOST("http://112.74.98.74/operation/hire",params);
        Log.d("test", "hire response is : '" + response + "'");
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
        sb.append("next workforce cost:");
        sb.append(cost);
		sb.append("\norigin:");
		sb.append(origin);
        sb.append("\norigin2:");
        sb.append(origin2);
		return sb;
	}


}
