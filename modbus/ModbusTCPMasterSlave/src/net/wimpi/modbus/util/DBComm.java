/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 *
 * @author nur
 */
public class DBComm {
    
    public  String sendPostRequest(String data) {
        
        //Build parameter string
        String res = "";
        try {
            
            // Send the request
            URL url = new URL("http://localhost:3000/devices.json");

	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setDoOutput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json");
 
	    String input = data;
 
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                    res=res+output;	
            }

            conn.disconnect();

        } catch (RuntimeException e) {
            return "RuntimeException: "+e;
        }
        catch (MalformedURLException e) {
            return res;
        } catch (IOException e) {
            return res;
        }
     return res;
    }


    public  String sendHashTable(Hashtable device){  

	Enumeration en = device.keys();
	String result = "{\"device\":{";
	while(en.hasMoreElements())
	{
	    String sKey = (String)en.nextElement();
	    String sVal = (String)device.get(sKey);
	    String temp = "\"" + sKey + "\": \"" + sVal +"\",";
	    result +=temp;
	}
	result = result.substring(0,(result.length()-1));
	result+="}}";

	return result;
  
    }
    

    public String sendGetRequest(String data) {
        //Build parameter string
    String res = "";
        try {
            
            // Send the request
            String urlString = "http://localhost:3000/devices"+data;
	    URLEncoder.encode(urlString, "UTF-8");
	    URL url = new URL(urlString);
	    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	    // Read the response

	    BufferedReader in = new BufferedReader(
	    new InputStreamReader(urlConnection.getInputStream()));
	    String line ="";
	    while ((line = in.readLine()) != null){
	     res = res+line;
	    }
	    in.close(); 
 
	  } 
	  catch (RuntimeException e) {
	      return "RuntimeException: "+e;
	  }
	  catch (MalformedURLException e) 
	  {
	      return res;
	  } catch (IOException e) {
 
	      return res;
	  }
      return res;
    }
    
    //Fills device table with name and adress of each house device (if the device already exists nothing happens)
    public void FillDevices(HashMap houseVariables){
        
        HouseVariable v;
        
        Hashtable<String,String> devices = new Hashtable<String,String>();
        
        //Iterate over all houseVariables 
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();
            devices.put("var_name",  v.name);
            devices.put("address",  ""+v.modbusAddr+"");
            
            String str =  sendHashTable(devices);

            System.out.println("Post device to databse result"+ sendPostRequest(str));
        }
        
    }

}
/*

	//String res = sendGetRequest("/getDeviceByName?name=light");
	System.out.println(res);

	//sendGetRequest("/setDeviceStateByName?name=light&state=0");
	//sendGetRequest("/setDeviceStateByAddress?address=00&state=0");
	//sendGetRequest("/getDeviceByName?name=light");
	//sendGetRequest("/getDeviceByAddress?address=11");
	//sendGetRequest("/getRangeByName?name=light&start_time=2012-08-05 22:53:02&end_time=2012-08-05 23:55:02");
	//sendGetRequest("/getRangeByName?address=11&start_time=2012-08-05 18:57:02&end_time=2012-08-05 22:41:02");
	//sendGetRequest(".json");
        * 
        */