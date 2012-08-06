
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;

import java.util.Enumeration;
import java.lang.Object;
import java.net.URLEncoder;
import java.lang.String;

public class SendRequest {

    public static String sendPostRequest(String data) {
        
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

    /**
     * Starts the program
     *
     * @param args the command line arguments
     */
    public static String sendHashTable(Hashtable device){  
	
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
    

    public static String sendGetRequest(String data) {
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

    public static void main(String[] args) 
    {


	Hashtable<String,String> device = new Hashtable<String,String>();
	device.put("var_name",  "light7");
	device.put("address",  "00:00:40:25");
	String str =  sendHashTable(device);
	
	String res = sendPostRequest(str);
	 
	//String res = sendGetRequest("/getDeviceByName?name=light");
	System.out.println(res);

	//sendGetRequest("/setDeviceStateByName?name=light&state=0");
	//sendGetRequest("/setDeviceStateByAddress?address=00&state=0");
	//sendGetRequest("/getDeviceByName?name=light");
	//sendGetRequest("/getDeviceByAddress?address=11");
	//sendGetRequest("/getRangeByName?name=light&start_time=2012-08-05 22:53:02&end_time=2012-08-05 23:55:02");
	//sendGetRequest("/getRangeByName?address=11&start_time=2012-08-05 18:57:02&end_time=2012-08-05 22:41:02");
	//sendGetRequest(".json");
 }
}