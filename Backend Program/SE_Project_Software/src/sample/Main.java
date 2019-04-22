package sample;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Controller.cars;

public class Main extends Application {

    private static Stage primaryStage;
    private static int times = 0;
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Main.primaryStage = primaryStage;
    }
    
    private static String getColor(String json_content) {
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
        return color_str;
    }
    
    private static String getMake(String json_content) {
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
        return make_model_str;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
    	Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
    	
    	Timer timer = new Timer(true);
    	timer.schedule(new TimerTask() {
    		@Override
    		public void run() {
    			try {
    				ArrayList<Integer> arrlist = new ArrayList<Integer>();
    				 Statement stmt = conn.createStatement();
    	             String strSelect = String.format("select id from car where car.Car_make = 'unknown';");
    	             ResultSet rset = stmt.executeQuery(strSelect);
    	             while(rset.next()) {
    	            	 arrlist.add(rset.getInt("id"));
    	             }
    	             System.out.println(arrlist);
    	             for(int i = 0; i < arrlist.size(); i++) {
    	                 int id = arrlist.get(i);
    	                 //String Car_make = rset.getString("Car_make");
    	                 //if(Car_make.equals("unknown")) {
    	                	 String strSelect1 = String.format("select * from carjson where carjson.id = %s", id);
    	                	 ResultSet rset1 = stmt.executeQuery(strSelect1);
    	                	 rset1.next();
    	                	 String json_file = rset1.getString("json");
    	                	 String Carmake = getMake(json_file);
    	                	 String CarModel = getMakeModel(json_file);
    	                	 String CarColor = getColor(json_file);
    	                	 strSelect1 = String.format("UPDATE car SET Car_make = '%s', Car_model = '%s', Car_color = '%s'  WHERE car.id = %s", Carmake,CarModel,CarColor,id);
    	                	 System.out.println(strSelect1);
    	                	 stmt.executeUpdate(strSelect1);
    	                 //}
    	                 
    	             }
    	             System.out.println(times);
    	             times++;
    			} catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
    	}, 0,10000);
    	
   
        setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Inspire Manage System");
        primaryStage.setScene(new Scene(root));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
