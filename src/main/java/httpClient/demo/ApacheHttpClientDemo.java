package httpClient.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class ApacheHttpClientDemo {
	
	// Free Tesing APIs: https://github.com/public-apis/public-apis#vehicle
	// Used in this code: https://wizard-world-api.herokuapp.com/swagger/index.html

	public static void main(String[] args) throws ClientProtocolException, IOException {
		sendHttpGet(); // working
		sendHttpGet_v2(); // working (same as above just a try is resource try)
		
		sendHttpPost_WithBody(); // working
		
		// Util methods - to print data from json
		printAllTheElementsFromJson(); //-- working
		
		jsonStrToMap_usingTypeToken(); // using TypeToken - working
		jsonStrToMap(); // simple approach - working
		
	}

	/* Working
	 * 
	 * Reference:  https://mkyong.com/java/apache-httpclient-examples/
	 */
	private static void sendHttpGet() throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String result = "";

		try {
//			HttpGet request = new HttpGet("https://wizard-world-api.herokuapp.com/Houses");
			HttpGet request = new HttpGet("https://wizard-world-api.herokuapp.com/Houses/0367baf3-1cb6-4baf-bede-48e17e1cd005");

			// add request headers
			request.addHeader("custom-key", "prai");
			request.addHeader(HttpHeaders.USER_AGENT, "bot");

			CloseableHttpResponse response = httpClient.execute(request);

			try {

				// Get HttpResponse Status
				System.out.println("1. " + response.getStatusLine().getStatusCode()); // 200
				System.out.println("2. " + response.getStatusLine().getReasonPhrase()); // OK

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					result = EntityUtils.toString(entity);
					System.out.println(result);
					
					//print json data using Gson
					Gson gson = new Gson();
					JsonObject jsonObj = gson.fromJson(result, JsonObject.class);
					System.out.println("3. name: " + jsonObj.get("name"));
					System.out.println("4. founder" + jsonObj.get("founder"));
				}

			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}

	}
	
	// Working
	// above can also be implemented like below 
	// Close with try-with-resources.
	private static void sendHttpGet_v2() throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet("https://wizard-world-api.herokuapp.com/Houses/0367baf3-1cb6-4baf-bede-48e17e1cd005");

        // add request headers
        request.addHeader("custom-key", "prai");
        request.addHeader(HttpHeaders.USER_AGENT, "bot");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

        	// Get HttpResponse Status
			System.out.println("11. " + response.getStatusLine().getStatusCode()); // 200
			System.out.println("22. " + response.getStatusLine().getReasonPhrase()); // OK

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                
                //print json data using Gson
				Gson gson = new Gson();
				JsonObject jsonObj = gson.fromJson(result, JsonObject.class);
				System.out.println("33. name: " + jsonObj.get("name"));
				System.out.println("44. founder" + jsonObj.get("founder"));
            }

        }
	}
	

	//-- POST - Working
	/*
	 * Reference:  https://mkyong.com/java/apache-httpclient-examples/
	 */
	private static void sendHttpPost_WithBody() throws IOException {
	    String result = "";
	    
	    /* POST with below request body (reading it from a json file for this demo)
	     * 
	     */
	    String url = "https://wizard-world-api.herokuapp.com/Feedback";
        HttpPost post = new HttpPost(url);

        // add request parameters or form parameters
        /*List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", "abc"));
        urlParameters.add(new BasicNameValuePair("password", "123"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters)); */
        
        // add request headers
        post.addHeader("Accept", "application/json");
        
        /** Make sure to add this otherwise it might fail with HTTP 414
         * Unsupported Media Type
         */
        post.addHeader("content-type", "application/json"); 
     	
     	String fileName = "/Users/prashantrai/Documents/git/java-interview-prep/src/main/java/httpClient/demo/feedback_Post_req.json";
     	String jsonStr_req = readJsonFromFileAndConvertToString(fileName);
     	StringEntity entity = new StringEntity(jsonStr_req);
     	post.setEntity(entity);
     	

        /* try with resourced closes the httpClient and response without closing them explicitly
         * refer sendHttpGet (with simple try) to see how to close them in finally.
         * 
         */
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

            result = EntityUtils.toString(response.getEntity());
            
            System.out.println("POST: status code: " + response.getStatusLine().getStatusCode());
            
            // this POST service doesn't return anything just HTTP status code
            System.out.println("POST: result: "+ result); 
        }

	}
	
	/* Read JSON from file and convert to String
	 */
	private static String readJsonFromFileAndConvertToString(String fileName) throws IOException {
		String jsonStr = "";
		
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String inputLine;
		while((inputLine = in.readLine()) != null) {
			jsonStr += inputLine;
		}
		
		System.out.println("jsonStr: " + jsonStr);
		
		return jsonStr;
	}

	
	
	/** Utility methods - to print individual data from Json response
	 * here we are reading a file to get the json but these method can work
	 * for json received in response as well
	 * @throws IOException 
	 */

	// Json str to Map - using TypeToken
	private static void jsonStrToMap_usingTypeToken() throws IOException {
		String fileName = "/Users/prashantrai/Documents/git/java-interview-prep/src/main/java/httpClient/demo/json_for_reading_and_printing_elements.json";
     	String jsonStr = readJsonFromFileAndConvertToString(fileName);
     	
     	Gson gson = new Gson();
     	
     	java.lang.reflect.Type mapType 
     		= new TypeToken<Map<String, ?>>(){}.getType();
     	
     	Map<String, ?> map = gson.fromJson(jsonStr, mapType);
     	
     	System.out.println("TypeToken: map: " + map);
     	
     	// print map data
     	LinkedTreeMap<String, ?> ltm_prdKey = (LinkedTreeMap<String, ?>) map.get("productKey");
     	String name = (String) map.get("name");
     	
     	System.out.println("productKey: "+ ltm_prdKey.get("productCode") + ", name: " + name);
     	
     	List<LinkedTreeMap<String, ?>> list = (List<LinkedTreeMap<String, ?>>) map.get("heads");
     	
     	for(LinkedTreeMap<String, ?> ltm : list) {
     		String firstName = (String) ltm.get("firstName");
     		String lastName = (String) ltm.get("lastName");
     		System.out.println("firstName: " + firstName + ", lastName: " + lastName);
     	}
     	
	}
	
	
	// Json str to Map - Simple way
	private static void jsonStrToMap() throws IOException {
		String fileName = "/Users/prashantrai/Documents/git/java-interview-prep/src/main/java/httpClient/demo/json_for_reading_and_printing_elements.json";
     	String jsonStr = readJsonFromFileAndConvertToString(fileName);
     	
     	Gson gson = new Gson();
     	Map<String, ?> map = (Map<String, ?>) gson.fromJson(jsonStr, Map.class);
     	System.out.println("SIMPLE: map: "+ map);
     	
     	// print map data
     	LinkedTreeMap<String, ?> ltm_prdKey = (LinkedTreeMap<String, ?>) map.get("productKey");
     	String name = (String) map.get("name");
     	
     	System.out.println("productKey: "+ ltm_prdKey.get("productCode") + ", name: " + name);
     	
     	List<LinkedTreeMap<String, ?>> list = (List<LinkedTreeMap<String, ?>>) map.get("heads");
     	
     	for(LinkedTreeMap<String, ?> ltm : list) {
     		String firstName = (String) ltm.get("firstName");
     		String lastName = (String) ltm.get("lastName");
     		System.out.println("firstName: " + firstName + ", lastName: " + lastName);
     	}
     	
	}
	
	
	public static void printAllTheElementsFromJson() throws IOException {
		String fileName = "/Users/prashantrai/Documents/git/java-interview-prep/src/main/java/httpClient/demo/json_for_reading_and_printing_elements.json";
     	String jsonStr = readJsonFromFileAndConvertToString(fileName);
     	
     	Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(jsonStr, JsonObject.class);
		System.out.println("1. PRINT: name: " + jsonObj.get("name").getAsString());
		System.out.println("2. PRINT: founder: " + jsonObj.get("founder").getAsString());
     	
     	/* when structure is like below
     	 {
     	 	"productKey": {
				"productCode": "HP"
			}
		 }
     	 */
		String prdCode = jsonObj.get("productKey").getAsJsonObject().get("productCode").getAsString();
     	System.out.println("3. PRINT: productCode" + jsonObj.get("productCode"));
     	
     	JsonArray jsonArr = jsonObj.get("heads").getAsJsonArray();
     	
     	/* Another way to iterate. But us iterator to get Set<Entry> obj
     	for(int i=0; i<jsonArr.size(); i++) {
     		JsonElement je = jsonArr.get(i); System.out.println("je: "+ je);
     	}
     	*/
     	
     	Iterator iter = jsonArr.iterator();
     	while(iter.hasNext()) {
     		JsonObject jo = (JsonObject) iter.next();
     		Set<Entry<String, JsonElement>> set = jo.entrySet();
     		System.out.println("set: "+set);
     		
     		for(Entry<String, JsonElement> entry : set) {
     			System.out.println("Attr: " + entry.getKey() +", Val: "+ entry.getValue());
     		}
     	}
     	
     	
	}
	
}
