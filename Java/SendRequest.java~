 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.Object;
import java.lang.String;

public class SendRequest {

	public void getAllRequest() 
{
 
	  try {
 
		URL url = new URL("http://localhost:3000/devices.json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
 
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
 
		conn.disconnect();
 
	  } catch (MalformedURLException e) {
 
		e.printStackTrace();
 
	  } catch (IOException e) {
 
		e.printStackTrace();
 
	  }
 
	}

    public void sendPostRequest(String data) {
        
        //Build parameter string
        
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
		  System.out.println(output);	
	  }
  
	  conn.disconnect();
 
    } catch (MalformedURLException e) {
 
	  e.printStackTrace();
 
    } catch (IOException e) {
 
		e.printStackTrace();
 
	 }
 
	}

    /**
     * Starts the program
     *
     * @param args the command line arguments
     */
    public static String sendHashTable(Hashtable device) 	
    {  
	
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
    public static void main(String[] args) 
    {


	Hashtable<String,String> device = new Hashtable<String,String>();
	device.put("number",  "1220");
	device.put("name",  "light2");
	device.put("address",  "00:00:40:22");
	String str =  sendHashTable(device);
	System.out.println(str); 
	new SendRequest().sendPostRequest(str);
	//new SendRequest().sendPostRequest("{\"key\":\"name\",\"val\":\"22\"}");
	//new SendRequest().getAllRequest() ;
 }
}