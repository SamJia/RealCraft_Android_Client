package com.example.realcraft.SomeClass;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;


public class Player{
	public int id;
	public String name;
	public int wood;
	public int food;
	public int stone;
	public int workforce;
	public double latitude;
	public double longitude;

	public Player(){
		
	}

	public Player(int init_id){
		id = init_id;
	}

	public Map getInfo() {
		Map info = new HashMap();
		info.put("name", name);
		info.put("food", food);
		info.put("wood", wood);
		info.put("stone", stone);
		info.put("workforce", workforce);
		info.put("latitude", latitude);
		info.put("longitude", longitude);
		return info;
	}

	public void updatePosition(double lati, double longi){
		latitude = lati;
		longitude = longi;
	}
	
	public void updateInfoFromServer(){
		String response = HttpUtil.HttpClientGET("http://112.74.98.74/request/property");
		try{
            JSONObject jsonObject = new JSONObject(response);
            id = Integer.parseInt(jsonObject.getString("playerId"));
            name = jsonObject.getString("username");
            wood = Integer.parseInt(jsonObject.getString("wood"));
            stone = Integer.parseInt(jsonObject.getString("stone"));
            food = Integer.parseInt(jsonObject.getString("food"));
            workforce = Integer.parseInt(jsonObject.getString("workforce"));
        } catch (Exception e){
            e.printStackTrace();
        }
	}
	
	@Override
	public boolean equals(Object obj) {   
        if (obj instanceof Player) {   
        	Player p = (Player) obj;   
            return (this.id == p.id);
        }   
        return super.equals(obj);  
	}
}