package httpClient.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class Java11HttpClientDemo {
	
	// Free Tesing APIs: https://github.com/public-apis/public-apis#vehicle
	// Used in this code: https://wizard-world-api.herokuapp.com/swagger/index.html
	
	public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException, InterruptedException {
		sendHttpGet();
		sendHttpPost_WithJosnBody();
	}
	
	
	private static final HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_2) // avoid unless asked
			.connectTimeout(Duration.ofSeconds(10))
			.build(); 

	
	// GET
	
	private static void sendHttpGet() throws URISyntaxException, IOException, InterruptedException {

		String uri = "https://wizard-world-api.herokuapp.com/Houses/0367baf3-1cb6-4baf-bede-48e17e1cd005";
		
		HttpRequest req = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.version(HttpClient.Version.HTTP_2) // avoid unless asked
				.header("key1", "val1")
				.header("key2", "val2")
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();
		
		HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
				
		System.out.println("Response body: " + resp.body());
		System.out.println("Response Status Code: " + resp.statusCode());
		
		// print an attrbute value from response
		Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(resp.body(), JsonObject.class);
		
		System.out.println("name: " + jsonObj.get("name").getAsString());
		
	}
	
	// POST
	
	private static void sendHttpPost_WithJosnBody() throws IOException, URISyntaxException, InterruptedException {
		String uri = "https://wizard-world-api.herokuapp.com/Feedback";
		
		String fileName = "/Users/prashantrai/Documents/git/java-interview-prep/src/main/java/httpClient/demo/feedback_Post_req.json";
		String body = readJsonFromFileAndConvertToString(fileName); // req body
		
		HttpRequest req = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.version(HttpClient.Version.HTTP_2) // avoid unless asked
				.header("key1", "val1")
				.header("key2", "val2")
				.header("content-type", "application/json") 
				.header("Accept", "application/json")
				.timeout(Duration.ofSeconds(10))
				//.POST(HttpRequest.BodyPublishers.noBody())
				.POST(HttpRequest.BodyPublishers.ofString(body)) // req body
				.build();
		
		HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
		
		// will be empty as there is no body returned for this request
		System.out.println("POST: respons body: " + resp.body()); 
		System.out.println("POST: respons status code: " + resp.statusCode());
		
		// Response has no body so we can't print any attribute's value.
		
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
