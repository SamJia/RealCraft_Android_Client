package com.example.realcraft.SomeClass;

import java.util.*; 
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;
import android.util.Log;
public class Encrypt{
    public static String encrypt(String s) {
    	String vc = passport_encrypt(s, "RealCraft");
    	Log.d("encrypt", vc);
    	return vc;
    }

    public static String passport_encrypt(String txt, String key){
    	Date dt = new Date();
		Long time= dt.getTime();
    	Random r = new Random(time);
    	String encrypt_key = MD5.encode(r.nextInt(32000)+"");
    	int ctr = 0;
    	String tmp = new String();
    	for (int i = 0; i < txt.length(); i++){
    		ctr = ctr == encrypt_key.length() ? 0 : ctr;
    		tmp += encrypt_key.charAt(ctr) + "" + (char)(txt.charAt(i) ^ encrypt_key.charAt(ctr++));
    	}
    	return Base64.encodeToString(passport_key(tmp,key).getBytes(), Base64.DEFAULT);
    	// return (new BASE64Encoder()).encode("test".getBytes());
    }
    public static String passport_key(String txt, String encrypt_key){
    	encrypt_key = MD5.encode(encrypt_key);
    	int ctr = 0;
    	String tmp = new String();
    	for (int i = 0;i < txt.length() ; i++ ) {
    		ctr = ctr == encrypt_key.length() ? 0 : ctr;
    		tmp += (char)(txt.charAt(i) ^ encrypt_key.charAt(ctr++));
    	}
    	return tmp;
    }
    public static class MD5 {    
    	public static void main(String[] args){
    	}
	    public static String encode(String originstr){
	        String result = null;
	        char hexDigits[] = {
	             '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; 
	        if(originstr != null){
	            try {
	                MessageDigest md = MessageDigest.getInstance("MD5");
	                byte[] source = originstr.getBytes("utf-8");
	                md.update(source);
	                byte[] tmp = md.digest();
	                char[] str = new char[32];
	                for(int i=0,j=0; i < 16; i++){
	                    byte b = tmp[i];
	                    str[j++] = hexDigits[b>>>4 & 0xf];
	                    str[j++] = hexDigits[b&0xf];
	                }
	                result = new String(str);
	            } catch (NoSuchAlgorithmException e) {
	                e.printStackTrace();
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }
	        }
	        return result;
	    }
	    public boolean checkPWD(String typPWD, String relPWD){
	        if(encode(typPWD).equals(encode(relPWD))){
	            return true;
	        }
	        return false;
	    }
}
} 
