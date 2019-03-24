package sample;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

import javax.swing.tree.AbstractLayoutCache;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureClassLoader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Vector;

public class Controller {

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

    // Password input instance on login window
    @FXML
    private com.jfoenix.controls.JFXPasswordField PasswordField;

    // Username input instance on login window
    @FXML
    private com.jfoenix.controls.JFXTextField UsernameField;

    // Refresh button on main window
    @FXML
    private javafx.scene.control.Button Refresh_list_button;

    // Car list on main window
    @FXML
    private javafx.scene.control.TableView<cars> Car_list;

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
            Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/lwmU9Ib3M7", "lwmU9Ib3M7", "cnTrfKQqso");
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
                Stage Main = new Stage();
                Main.setTitle("Inspire Parking Management Software. Welcome " + Userinput);
                Main.setScene(new Scene(root));
                Main.show();
                Label rightStatus = (Label)Main.getScene().lookup("#rightStatus");
                rightStatus.setText(Userinput);
                refresh_list(Main,true);
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

            if(firstTime) {
                Label leftStatus = (Label) stage.getScene().lookup("#leftStatus");
                Label carColorDetail = (Label) stage.getScene().lookup("#carColorDetail");
                Label carMakeDetail = (Label) stage.getScene().lookup("#carMakeDetail");
                Label carModelDetail = (Label) stage.getScene().lookup("#carModelDetail");
                Label carPlateDetail = (Label) stage.getScene().lookup("#carPlateDetail");
                Label timeDurationDetail = (Label) stage.getScene().lookup("#timeDurationDetail");
                ImageView carPhotoDetail = (ImageView) stage.getScene().lookup("#carPhotoDetail");
                leftStatus.setText("Number of cars in list: " + rowCount);
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
                        Image image = new Image(getClass().getResource("./carPhoto/" + car.getCarId() + ".png").toExternalForm());
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
        Integer second=0, minite=0,hour=0;
        int timeBegin = car.getTimeBegin();
        int timeEnd = car.getTimeEnd();
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

        if(minite > 60){
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
        Stage window = (Stage) Refresh_list_button.getScene().getWindow();
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
            String secret_key = "sk_aeaf16bdbe49c343c5404ae2";

            // Read image file to byte array
            System.out.println(ID);
            Path path = Paths.get("D:\\SE_Project_Software\\src\\sample\\carPhoto\\" + "5" + ".png");
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
                Image image = new Image(getClass().getResource("./carPhoto/" + 5 + ".png").toExternalForm());
                ImageView imageView = (ImageView) stage.getScene().lookup("#carPhotoProceed") ;
                imageView.setImage(image);
                carColorProceed.setText(color);
                carMakeProceed.setText(carMake);
                carModelProceed.setText(carModel);
                carPlateProceed.setText(plate);
                carRegionProceed.setText(region);

                int timeBegin = LocalDateTime.now().getSecond() + LocalDateTime.now().getMinute()*100 + LocalDateTime.now().getHour()*10000;
                String strSelect = String.format("insert into cars values (%s,'%s','%s','%s','%s',%s,null);",ID, carMake,carModel, color, plate,timeBegin);
                stmt.executeUpdate(strSelect);
                RefreshList();
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
            AlertBox.display("Error","There is a problem with your key.");
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
