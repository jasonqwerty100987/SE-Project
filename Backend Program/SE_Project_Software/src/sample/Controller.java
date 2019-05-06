package sample;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXComboBox;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import com.mysql.cj.protocol.Resultset;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.tree.AbstractLayoutCache;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.SecureClassLoader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Vector;

public class Controller {

    private static String SK = "sk_aeaf16bdbe49c343c5404ae2";
    public class Image extends javafx.scene.image.Image{

        String url;

        public Image(String url) {
            super(url);
            this.url = url;
        }

        public String geturl() {
            return url;
        }

    }
    public class cars{
        private int carId;
        private String carMake;
        private String carModel;
        private String carColor;
        private String carPlate;
        private int timeBegin;
        private int timeEnd;
        public cars(int ID,String Car_make, String Car_model, String Car_color, String CarPlate , int time_begin, int time_end){
            setCarId(ID);
            setCarMake(Car_make);
            setCarModel(Car_model);
            setCarColor(Car_color);
            setCarPlate(CarPlate);
            setTimeBegin(time_begin);
            if(time_end != 0){
                setTimeEnd(time_end);
            }
        }

        public int getCarId() {
            return carId;
        }

        public String getCarMake() {
            return carMake;
        }

        public String getCarModel() {
            return carModel;
        }

        public String getCarColor() {
            return carColor;
        }

        public String getCarPlate() {
            return carPlate;
        }

        public int getTimeBegin() {
            return timeBegin;
        }

        public int getTimeEnd() {
            return timeEnd;
        }

        public void setCarId(int carId) {
            this.carId = carId;
        }

        public void setCarMake(String carMake) {
            this.carMake = carMake;
        }

        public void setCarModel(String carModel) {
            this.carModel = carModel;
        }

        public void setCarColor(String carColor) {
            this.carColor = carColor;
        }

        public void setCarPlate(String carPlate) {
            this.carPlate = carPlate;
        }

        public void setTimeBegin(int timeBegin) {
            this.timeBegin = timeBegin;
        }

        public void setTimeEnd(int timeEnd) {
            this.timeEnd = timeEnd;
        }
    }

    private static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    // Login window close button instance on login window
    @FXML
    private com.jfoenix.controls.JFXButton CloseButton;

    @FXML
    private javafx.scene.control.Button currentTime;

    @FXML
    private javafx.scene.control.Button changeNow;

    // Password input instance on login window
    @FXML
    private com.jfoenix.controls.JFXPasswordField PasswordField;

    // Username input instance on login window
    @FXML
    private com.jfoenix.controls.JFXTextField UsernameField;

    // Refresh button on main window
    @FXML
    private  javafx.scene.control.Button Refresh_list_button;

    @FXML
    private javafx.scene.control.Button deleteButton;

    @FXML
    private javafx.scene.control.Button changeKeyNow;
    @FXML
    private javafx.scene.control.Button changeRateButton;
    @FXML
    private com.jfoenix.controls.JFXTextField rateField;


    @FXML // CLose window method
    private void handleButtonClick() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }


    @FXML // Login validate info method
    private void validateInfo() throws Exception{
        try {
            String Userinput = UsernameField.getText();
            String Userpd = sha256(PasswordField.getText());
            Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/t75K9YqGJ6", "t75K9YqGJ6", "TGFCdJIG2D");
            Statement stmt = conn.createStatement();
            String strSelect = String.format("select username, passwd from users where username='%s' and passwd='%s';",Userinput,Userpd);
            ResultSet rset = stmt.executeQuery(strSelect);
            boolean status = false;

            /*while (rset.next()) {   // Move the cursor to the next row, return false if no more row
                String username = rset.getString("username");
                String Password = rset.getString("passwd");
                if(username.equals(UsernameField.getText())){
                    if(Password.equals(sha256(PasswordField.getText()))){
                        status = true;
                        break;
                    }
                }
            }*/

            if(rset.next()){
                status = true;
            }
            if(status){
                AlertBox.display("Login Success","You logged in");
                Stage stage = (Stage) CloseButton.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage main = new Stage();
                main.setTitle("Inspire Parking Management Software. Welcome " + Userinput);
                main.setScene(new Scene(root));
                main.show();
                Label rightStatus = (Label)main.getScene().lookup("#rightStatus");
                rightStatus.setText(Userinput);
                refresh_list(main,true);
                Main.setPrimaryStage(main);
            }else{
                AlertBox.display("Login Fail", "System cannot identity a user with the credential you entered \n           please check and try again");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML // refresh cars list method with parameter of source stage
    private void refresh_list(Stage stage, boolean firstTime){
        try{
            Scene scene = stage.getScene();
            TableView tableView = (TableView) scene.lookup("#Car_list") ;
            tableView.getItems().clear();

            Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
            Statement stmt = conn.createStatement();
            String strSelect = String.format("select * from cars;");
            ResultSet rset = stmt.executeQuery(strSelect);

            int rowCount = 0;
            while (rset.next()) {   // Move the cursor to the next row, return false if no more row
                Integer second=0, minite=0,hour=0,day=0,month=0,year=0;
                double a,b;
                int id = rset.getInt("id");
                String Car_make = rset.getString("Car_make");
                String Car_model = rset.getString("Car_model");
                String Car_color = rset.getString("Car_color");
                String CarPlate = rset.getString("CarPlate");
                int timeBegin = rset.getInt("time_begin");
                int timeEnd = rset.getInt("time_end");
                cars car = new cars(id, Car_make, Car_model, Car_color, CarPlate, timeBegin, timeEnd);
                tableView.getItems().add(car);
                ++rowCount;
            }
            Label leftStatus = (Label) stage.getScene().lookup("#leftStatus");
            leftStatus.setText("Number of cars in list: " + rowCount);
            if(firstTime) {
                Label carColorDetail = (Label) stage.getScene().lookup("#carColorDetail");
                Label carMakeDetail = (Label) stage.getScene().lookup("#carMakeDetail");
                Label carModelDetail = (Label) stage.getScene().lookup("#carModelDetail");
                Label carPlateDetail = (Label) stage.getScene().lookup("#carPlateDetail");
                Label timeDurationDetail = (Label) stage.getScene().lookup("#timeDurationDetail");
                ImageView carPhotoDetail = (ImageView) stage.getScene().lookup("#carPhotoDetail");
                tableView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
                    cars car = (cars) newValue;
                    if(car == null){
                        carPhotoDetail.setImage(null);
                        carColorDetail.setText("No Selection");
                        carMakeDetail.setText("No Selection");
                        carModelDetail.setText("No Selection");
                        carPlateDetail.setText("No Selection");
                        timeDurationDetail.setText("No Selection");
                    }else {
                    	String directory = System.getProperty("user.dir");
                        Image image = new Image(directory + "/carPhoto/" + car.getCarId() + ".png");
                        carColorDetail.setText(car.getCarColor());
                        carMakeDetail.setText(car.getCarMake());
                        carModelDetail.setText(car.getCarModel());
                        carPlateDetail.setText(car.getCarPlate());
                        timeDurationDetail.setText(timeDuration(car));
                        carPhotoDetail.setImage(image);
                    }
                });
                AlertBox.display("Car list update info","There are " + rowCount + " cars in the list. You also can check the total number of cars at the left bottom corner.");
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void addCar() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("addCar.fxml"));
        Stage main = new Stage();
        main.setTitle("Add A New Car");
        main.setScene(new Scene(root));
        main.show();
    }

    @FXML
    private void deleteCar() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("deleteNow.fxml"));
        Stage main = new Stage();
        main.setTitle("");
        main.setScene(new Scene(root));
        main.show();
    }

    @FXML
    private void changeCar() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("changeCar.fxml"));
        Stage main = new Stage();
        main.setTitle("Change A Car Information");
        main.setScene(new Scene(root));
        JFXComboBox jfxComboBox = (JFXComboBox)main.getScene().lookup("#changeSelection");
        jfxComboBox.getItems().addAll("Car_make","Car_model","Car_color", "CarPlate", "time_begin", "time_end");
        main.show();
    }

    @FXML
    private void changeKey()throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("changeKey.fxml"));
        Stage main = new Stage();
        main.setScene(new Scene(root));
        Label label = (Label) main.getScene().lookup("#secretKey");
        label.setText(SK);
        main.setTitle("Change Car Reader API Key. Current Secret Key is: " + SK);
        main.show();
    }
    @FXML void changeRate() throws Exception{
    	Parent root = FXMLLoader.load(getClass().getResource("changeRate.fxml"));
    	Stage changeRateStage = new Stage();
    	changeRateStage.setScene(new Scene(root));
    	changeRateStage.show();
    }
    @FXML
    private void changeRateNow() throws Exception{
    	String inputRate = rateField.getText();
    	HttpURLConnection url = getURLConnection(
    			"https://inspark2019.000webhostapp.com/v2/php/lotmanager.php"+
    		    "?op=set_rate&name=Hypothetical Parking Lot 2B&rate="+inputRate,"POST");
    	System.out.println(url.getResponseCode());
    	System.out.println(getURLConnectionOutput(url));
    	
    	
    }
    
    private String getURLConnectionOutput(HttpURLConnection urlc) {
    	  StringBuilder str = new StringBuilder();
    	  try {
    	    BufferedReader in = new BufferedReader(
    	      new InputStreamReader(
    	      urlc.getInputStream(),"UTF-8"));
    	    for(int c;(c=in.read())>=0;) {
    	      str.append((char)c);
    	    }
    	    in.close();
    	  } catch(Exception e) {
    	    e.printStackTrace();
    	  }
    	  return str.toString();
    	}
    private HttpURLConnection getURLConnection(String url, String type) throws Exception {
    	  
    	  String agent =
    	    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "+
    	    "AppleWebKit/537.36 (KHTML, like Gecko) "+
    	    "Chrome/70.0.3538.102 Safari/537.36";
    	    
    	  if(type.equals("POST")) {
    	    
    	    //println("POST request detected!");
    	    //println("given url is: "+url);
    	    
    	    int param_index = url.lastIndexOf("?");
    	    String params = "";
    	    if(param_index!=-1) {
    	      params = url.substring(param_index+1);
    	      url = url.substring(0,param_index);
    	    }
    	    byte[] data = params.getBytes(StandardCharsets.UTF_8);
    	    
    	    //println("trimmed url is: "+url);
    	    //println("parameter text is: "+params);
    	    
    	    HttpURLConnection urlc = (HttpURLConnection)new URL(url).openConnection();
    	    urlc.setDoOutput(true);
    	    urlc.setInstanceFollowRedirects(false);
    	    urlc.setRequestMethod("POST");
    	    urlc.setRequestProperty("user-agent",agent);
    	    urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
    	    urlc.setRequestProperty("charset","utf-8");
    	    urlc.setRequestProperty("Content-Length",Integer.toString(data.length));
    	    urlc.setUseCaches(false);
    	    urlc.getOutputStream().write(data);
    	    
    	    return urlc;
    	  } else if(type.equals("GET")) {
    	    
    	    HttpURLConnection urlc = (HttpURLConnection)new URL(url).openConnection();
    	    urlc.setRequestMethod("GET");
    	    urlc.setRequestProperty("user-agent",agent);
    	    
    	    return urlc;
    	  }
    	  throw new Exception("only POST and GET allowed");
    	}
    @FXML
    private void changeKeyNow1(){
        JFXTextField jfxTextField = (JFXTextField)changeKeyNow.getScene().lookup("#newSK");
        if(jfxTextField.getText().isEmpty()){
            AlertBox.display("Error","Please enter a key");
            return;
        }
        SK = jfxTextField.getText();
        Stage window = (Stage)changeKeyNow.getScene().getWindow();
        window.close();
    }

    @FXML
    private void deleteNow()throws Exception{
        JFXTextField jfxTextField = (JFXTextField)deleteButton.getScene().lookup("#keyField");
        if(jfxTextField.getText().isEmpty()){
            AlertBox.display("Error","Please enter a ID");
        }
        int ID = Integer.parseInt(jfxTextField.getText());
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
        Statement stmt = conn.createStatement();
        String query = "DELETE FROM cars WHERE ID = " + ID;
        stmt.executeUpdate(query);
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();

        RefreshList();
    }

    @FXML
    private void useCurrentTime() throws Exception{
        Integer time = LocalDateTime.now().getSecond() + LocalDateTime.now().getMinute()*100 + LocalDateTime.now().getHour()*10000;
        JFXTextField jfxTextField = (JFXTextField)currentTime.getScene().lookup("#timeBegin");
        jfxTextField.setText(time.toString());
    }

    @FXML
    private void changeNowInfo() throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
        Statement stmt = conn.createStatement();
        JFXComboBox keyValue = (JFXComboBox)changeNow.getScene().lookup("#changeSelection") ;
        JFXTextField recordID = (JFXTextField)changeNow.getScene().lookup("#recordID");
        JFXTextField newValueText = (JFXTextField)changeNow.getScene().lookup("#newInfo");
        keyValue.getSelectionModel().isSelected(0);
        if(recordID.getText().isEmpty()){
            AlertBox.display("Error","Please enter a ID");
            return;
        }
        if(newValueText.getText().isEmpty()){
            AlertBox.display("Error","Please enter a new value");
            return;
        }
        if(keyValue.getSelectionModel().getSelectedItem().toString() == "time_begin" || keyValue.getSelectionModel().getSelectedItem().toString() == "time_end"){
            int ID = Integer.parseInt(recordID.getText());
            String Key = keyValue.getSelectionModel().getSelectedItem().toString();
            int newValue = Integer.parseInt(newValueText.getText());
            String query = String.format("UPDATE cars SET %s = '%s' WHERE ID = %s",Key,newValue,ID);
            stmt.executeUpdate(query);
            RefreshList();
        }else{
            int ID = Integer.parseInt(recordID.getText());
            String Key = keyValue.getSelectionModel().getSelectedItem().toString();
            String newValue = newValueText.getText();
            String query = String.format("UPDATE cars SET %s = '%s' WHERE cars.ID = %s",Key,newValue,ID);
            System.out.println(query);
            stmt.executeUpdate(query);
            RefreshList();
        }
        Stage stage = (Stage) keyValue.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void addNowFunction() throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
        Statement stmt = conn.createStatement();
        String query = "SELECT count(*) as total FROM cars;";
        ResultSet rset = stmt.executeQuery(query);
        rset.next();
        int ID = rset.getInt("total") + 1;
        JFXTextField timeBeginText = (JFXTextField)currentTime.getScene().lookup("#timeBegin");
        JFXTextField carMakeText = (JFXTextField)currentTime.getScene().lookup("#carMake");
        JFXTextField carModelText = (JFXTextField)currentTime.getScene().lookup("#carModel");
        JFXTextField carColorText = (JFXTextField)currentTime.getScene().lookup("#carColor");
        JFXTextField carPlateText = (JFXTextField)currentTime.getScene().lookup("#carPlate");
        JFXTextField timeEndText = (JFXTextField)currentTime.getScene().lookup("#timeEnd");
        String carMake,carModel,color,plate;
        int timeBegin,timeEnd;
        if(timeBeginText.getText().isEmpty() || carMakeText.getText().isEmpty() || carModelText.getText().isEmpty() || carColorText.getText().isEmpty() || carPlateText.getText().isEmpty()){
            AlertBox.display("Error", "Please enter all required information");
            return;
        }
        timeBegin = Integer.parseInt(timeBeginText.getText());
        carMake = carMakeText.getText();
        carModel = carModelText.getText();
        color = carColorText.getText();
        plate = carPlateText.getText();
        if(timeEndText.getText().isEmpty()){
            timeEnd = 0;
        }else{
            timeEnd = Integer.parseInt(timeEndText.getText());
        }
        String strSelect = String.format("insert into cars values (%s,'%s','%s','%s','%s',%s,%s);",ID, carMake,carModel, color, plate,timeBegin,timeEnd);
        stmt.executeUpdate(strSelect);
        Stage stage = (Stage) timeBeginText.getScene().getWindow();
        stage.close();
        RefreshList();
    }

    @FXML
    private void About() throws Exception{
        URI uri = new URI("https://inspark2019.000webhostapp.com/v2/");
        java.awt.Desktop.getDesktop().browse(uri);
    }

    private String timeFormate(int time){
        Integer second=0, minite=0,hour=0,day=0,month=0,year=0;
        double a,b;
        String ssecond,sminite,shour,sday,smonth,syear;
        Integer[] result = new Integer[6];
        for (int i = 0; i < 6; i++){
            a=time%10;
            time =(int) Math.floor((double)time/10);
            b=time%10 * 10;
            time =(int) Math.floor((double)time/10);
            result[i] = (int)a + (int)b;
        }

        second = result[0];
        minite = result[1];
        hour = result[2];
        day = result[3];
        month = result[4];
        year = result[5];

        syear = "20" + format(year);
        smonth = format(month);
        sday = format(day);
        shour = format(hour);
        sminite = format(minite);
        ssecond = format(second);

        String resultFormat = smonth + "/" + sday + "/" + syear + "\n" + shour + ":" + sminite + ":" + ssecond;
        return resultFormat;
    }

    private String format(Integer time){
        String result;
        if(time == 0){
            result = "00";
        }else if(time < 10){
            result = "0" + time.toString();
        }else{
            result = time.toString();
        }
        return result;
    }

    private String timeDuration(cars car){
        int timeEnd = car.getTimeEnd();
        String temp = "No exit record";
        if(timeEnd == 0){
            return temp;
        }
        Integer second=0, minite=0,hour=0;
        int timeBegin = car.getTimeBegin();

        double a,b;
        String ssecond,sminite,shour;
        Integer[] result = new Integer[3];

        for(int i = 0; i < 3; i++){
            a=timeBegin%10;
            timeBegin =(int) Math.floor((double)timeBegin/10);
            b=timeBegin%10 * 10;
            timeBegin =(int) Math.floor((double)timeBegin/10);
            result[i] = (int)a + (int)b;
            a=timeEnd%10;
            timeEnd = (int) Math.floor((double)timeEnd/10);
            b=timeEnd%10 * 10;
            timeEnd = (int) Math.floor((double)timeEnd/10);
            if((int)a + (int)b - result[i] < 0){
                result[i] = (int)a + (int)b - result[i] + 60;
                timeEnd -= 1;
            }else{
                result[i] = (int)a + (int)b - result[i];
            }
        }

        second = result[0];
        minite = result[1];
        hour = result[2];

        while(minite > 60){
            minite -= 60;
            hour += 1;
        }
        shour = format(hour);
        sminite = format(minite);
        ssecond = format(second);

        String Duration = shour + ":" + sminite + ":" + ssecond;
        return Duration;
    }

    @FXML // refresh cars list method with no parameter
    private void RefreshList(){
        Stage window = Main.getPrimaryStage();
        refresh_list(window,false);
    }

    @FXML // proceed car info via API call
    private void proceedCar() throws Exception{
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/Vneao4rF2A", "Vneao4rF2A", "r1Futn7r47");
            Statement stmt = conn.createStatement();
            String query = "SELECT count(*) as total FROM cars;";
            ResultSet rset = stmt.executeQuery(query);
            rset.next();
            int ID = rset.getInt("total") + 1;
            String secret_key = SK;

            // Read image file to byte array
            String directory = System.getProperty("user.dir");
            Image image = new Image("file:/" + directory + "/carPhoto/" + ID + ".png");
            String path = image.geturl();
            path = path.substring(6);
            Path realPath = Paths.get(path);
            // image file location : need to be revised
            byte[] data = Files.readAllBytes(realPath);

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

                String plate = getPlate(json_content);
                String region = getRegion(json_content);
                String color = getColor(json_content);
                String year = getYear(json_content);
                String bodyType = getBodyType(json_content);
                String carMake = getMake(json_content);
                String carModel = getMakeModel(json_content);
                Stage stage = (Stage) Refresh_list_button.getScene().getWindow();
                Label carColorProceed = (Label) stage.getScene().lookup("#carColorProceed");
                Label carMakeProceed = (Label) stage.getScene().lookup("#carMakeProceed");
                Label carModelProceed = (Label) stage.getScene().lookup("#carModelProceed");
                Label carPlateProceed = (Label) stage.getScene().lookup("#carPlateProceed");
                Label carRegionProceed = (Label) stage.getScene().lookup("#carRegionProceed");

                ImageView imageView = (ImageView) stage.getScene().lookup("#carPhotoProceed") ;
                imageView.setImage(image);
                carColorProceed.setText(color);
                carMakeProceed.setText(carMake);
                carModelProceed.setText(carModel);
                carPlateProceed.setText(plate);
                carRegionProceed.setText(region);

                int timeBegin = LocalDateTime.now().getSecond() + LocalDateTime.now().getMinute()*100 + LocalDateTime.now().getHour()*10000;
                String strSelect = String.format("insert into cars values (%s,'%s','%s','%s','%s',%s,0);",ID, carMake,carModel, color, plate,timeBegin);
                stmt.executeUpdate(strSelect);
                RefreshList();

                /*StringBuilder tokenUri=new StringBuilder("activity=");
                tokenUri.append(URLEncoder.encode("enter","UTF-8"));
                tokenUri.append("&lpnum=");
                tokenUri.append(URLEncoder.encode(plate,"UTF-8"));

                String url1 = "https://inspark2019.000webhostapp.com/v2/traffic.php?";
                URL obj = new URL(url1);
                HttpsURLConnection con1 = (HttpsURLConnection) obj.openConnection();

                con1.setRequestMethod("POST");

                con1.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
                outputStreamWriter.write(tokenUri.toString());
                outputStreamWriter.flush();
                */
            }
            else
            {
                AlertBox.display("Error", "Error code " + status_code);
            }


        }
        catch (MalformedURLException e)
        {
            AlertBox.display("Error","There is a connection error with the given URL.");
        }
        catch (IOException e)
        {
            AlertBox.display("Error","There is a problem with picture.");
        }
    }

    private static String getPlate(String json_content) {
        JSONObject jsonObj = JSON.parseObject(json_content);
        JSONArray result = jsonObj.getJSONArray("results");
        String plate = null;
        for(int i = 0; i < result.size(); i++) {
            JSONObject obj = result.getJSONObject(i);
            plate = obj.getString("plate");
        }
        return plate;
    }

    private static String getRegion(String json_content) {
        JSONObject jsonObj = JSON.parseObject(json_content);
        JSONArray result = jsonObj.getJSONArray("results");
        String region = null;
        for(int i = 0; i < result.size(); i++) {
            JSONObject obj = result.getJSONObject(i);
            region = obj.getString("region");
        }
        return region;
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

    private static String getYear(String json_content) {
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

    private static String getBodyType(String json_content) {
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
        return body_type_str;
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


}
