package com.example.realcraft.SomeClass;

public class Item{
	public int id;
	public double latitude, longitude;
	 Item(){
	 	id = -1;
	 	latitude = longitude = 0;
	 }
	public Item(int init_id, double init_latitud, double init_longitud){
		id = init_id;
		latitude = init_latitud;
		longitude = init_longitud;
	}

	public int getType(){
		return 0;
	}

	@Override
	public boolean equals(Object obj) {   
        if (obj instanceof Item) {   
        	Item r = (Item) obj;   
            return (this.id == r.id) && (this.getType() == r.getType());
        }   
        return super.equals(obj);
	}
}
