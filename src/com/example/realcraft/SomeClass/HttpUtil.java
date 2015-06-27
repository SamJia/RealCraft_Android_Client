package com.example.realcraft.SomeClass;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by user on 2015/6/2.
 */
public class HttpUtil {
    public static final int POST = 0;
    public static final int GET = 1;
    public static String SESSIONID = null;

    public static String sendHttpRequest(String address) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String HttpClientGET(String address) {
         final String pass = address;
        // res = "begin";
        // new Thread(new Runnable(){
        //     @Override
        //     public void run(){
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(pass);
                    if(SESSIONID != null){
                        httpGet.setHeader("Cookie","ci_session="+SESSIONID);
                    }
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        CookieStore mCookieStore = httpClient.getCookieStore();
                        List<Cookie> cookies = mCookieStore.getCookies();
                        Log.d("Test","cookies siez"+Integer.toString(cookies.size()));
                        for(int i = 0; i < cookies.size(); ++i){
                            Log.d("Test",cookies.get(i).getName());
                            if("ci_session".equals(cookies.get(i).getName())){
                                SESSIONID = cookies.get(i).getValue();
                                break;
                            }
                        }
                        Log.d("Test","PHPSWSSID: "+SESSIONID);
                        // Message message = new Message();
                        // message.what = GET;
                        // message.obj = response.toString();
                        // handler.sendMessage(message);
                        // handler.handleMessage(message);
                        // res = response;
                        Log.d("Test", "GET successfully "+response);
                        return response;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
        //     }
        // }).start();
        // try {
        //     for(int i = 0; i < 40 && res.equals("begin"); ++i)
        //         Thread.sleep(50);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // Log.d("Test", "GET successfully "+res);
        // return res;
    }

    public static String HttpClientPOST(String address, List<NameValuePair> params) {
        final String pass = address;
        final List<NameValuePair> param = params;
        // res = "begin";
        // new Thread() {
        //     public void run() {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(pass);
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(param, "utf-8");
                    httpPost.setEntity(entity);
                    if(SESSIONID != null){
                        httpPost.setHeader("Cookie","ci_session="+SESSIONID);
                    }
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entityGet = httpResponse.getEntity();
                        String response = EntityUtils.toString(entityGet, "utf-8");
                        CookieStore mCookieStore = httpClient.getCookieStore();
                        List<Cookie> cookies = mCookieStore.getCookies();
                        Log.d("Test","cookies siez"+Integer.toString(cookies.size()));
                        for(int i = 0; i < cookies.size(); ++i){
                            Log.d("Test",cookies.get(i).getName());
                            if("ci_session".equals(cookies.get(i).getName())){
                                SESSIONID = cookies.get(i).getValue();
                                break;
                            }
                        }
                        Log.d("Test","PHPSWSSID: "+SESSIONID);
                        // Message message = new Message();
                        // message.what = POST;
                        // message.obj = response.toString();
                        // handler.sendMessage(message);
                        // handler.handleMessage(message);
                        // res = response;
                        Log.d("Test", "POST successfully " + response);
                        return response;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            // }
        // }.start();
        // try {
        //     for(int i = 0; i < 40 && res.equals("begin"); ++i)
        //         Thread.sleep(50);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // return res;
    }
}