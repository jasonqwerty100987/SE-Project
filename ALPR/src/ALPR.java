import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


class ALPR {

	public static void main(String[] args)
	{
		try
		{
			String secret_key = "sk_81d57d1165a4240f24b7cdfb";

			// Read image file to byte array
			Path path = Paths.get("/Users/shijiexu/Desktop/6.png");
			// image file location : need to be revised
			byte[] data = Files.readAllBytes(path);

			// Encode file bytes to base64
			byte[] encoded = Base64.getEncoder().encode(data);


			// Setup the HTTPS connection to api.openalpr.com
			URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=us&topn=1&secret_key=" + secret_key);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setFixedLengthStreamingMode(encoded.length);
			http.setDoOutput(true);

			// Send our Base64 content over the stream
			try(OutputStream os = http.getOutputStream()) {
				os.write(encoded);
			}

			int status_code = http.getResponseCode();
			if (status_code == 200)
			{
				// Read the response
				BufferedReader in = new BufferedReader(new InputStreamReader(
						http.getInputStream()));
				String json_content = "";
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					json_content += inputLine;
				in.close();

				
				System.out.println("Plate: "+ getPlate(json_content));
				System.out.println("Region: "+getRegion(json_content));
				System.out.println("Color: "+getColor(json_content));
				System.out.println("Year: "+getYear(json_content));
				System.out.println("Body Type: "+getBodyType(json_content));
				System.out.println("Make: "+getMake(json_content));
				System.out.println("Make Model: "+getMakeModel(json_content));
			}
			else
			{
				System.out.println("Got non-200 response: " + status_code);
			}


		}
		catch (MalformedURLException e)
		{
			System.out.println("Bad URL");
		}
		catch (IOException e)
		{
			System.out.println("Failed to open connection");
		}

	}


	public static String getPlate(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		String plate = null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			plate = obj.getString("plate");
		}
		return plate;
	}
	
	public static String getRegion(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		String region = null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			region = obj.getString("region");
		}
		return region;
	}
	
	public static String getColor(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		JSONObject vehicle= null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			vehicle = obj.getJSONObject("vehicle");
		}
		JSONArray color = vehicle.getJSONArray("color");
		String color_str= null;
		for(int i = 0; i < color.size();i++) {
			JSONObject obj = color.getJSONObject(i);
			color_str= obj.getString("name");
		}
		color_str = color_str.replace('-', ' ');
		return color_str;
	}
	
	public static String getYear(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		JSONObject vehicle= null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			vehicle = obj.getJSONObject("vehicle");
		}
		JSONArray year = vehicle.getJSONArray("year");
		String year_str= null;
		for(int i = 0; i < year.size();i++) {
			JSONObject obj = year.getJSONObject(i);
			year_str= obj.getString("name");
		}
		return year_str;
	}
	
	public static String getBodyType(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		JSONObject vehicle= null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			vehicle = obj.getJSONObject("vehicle");
		}
		JSONArray body_type = vehicle.getJSONArray("body_type");
		String body_type_str= null;
		for(int i = 0; i < body_type.size();i++) {
			JSONObject obj = body_type.getJSONObject(i);
			body_type_str= obj.getString("name");
		}
		body_type_str = body_type_str.replace('-', ' ');
		return body_type_str;
	}
	
	public static String getMake(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		JSONObject vehicle= null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			vehicle = obj.getJSONObject("vehicle");
		}
		JSONArray make = vehicle.getJSONArray("make");
		String make_str= null;
		for(int i = 0; i < make.size();i++) {
			JSONObject obj = make.getJSONObject(i);
			make_str= obj.getString("name");
		}
		return make_str;
	}
	
	public static String getMakeModel(String json_content) {
		JSONObject jsonObj = JSON.parseObject(json_content);
		JSONArray result = jsonObj.getJSONArray("results");
		JSONObject vehicle= null;
		for(int i = 0; i < result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			vehicle = obj.getJSONObject("vehicle");
		}
		JSONArray make_model = vehicle.getJSONArray("make_model");
		String make_model_str= null;
		for(int i = 0; i < make_model.size();i++) {
			JSONObject obj = make_model.getJSONObject(i);
			make_model_str= obj.getString("name");
		}
		make_model_str = make_model_str.replace('-', ' ');
		make_model_str = make_model_str.replace('_', ' ');
		return make_model_str;
	}
}