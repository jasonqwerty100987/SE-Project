package sample;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;

public class Controller {

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
    public static void main(String[] args){
        System.out.println(sha256("Zz4410387"));
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

    // Login button instance on login window
    @FXML
    private com.jfoenix.controls.JFXButton LogButton;

    @FXML
    private void handleButtonClick() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void validateInfo() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/lwmU9Ib3M7", "lwmU9Ib3M7", "cnTrfKQqso");
            Statement stmt = conn.createStatement();
            String strSelect = "select username, passwd from users";
            ResultSet rset = stmt.executeQuery(strSelect);
            boolean status = false;
            while (rset.next()) {   // Move the cursor to the next row, return false if no more row
                String username = rset.getString("username");
                String Password = rset.getString("passwd");
                if(username.equals(UsernameField.getText())){
                    if(Password.equals(sha256(PasswordField.getText()))){
                        status = true;
                        break;
                    }
                }
            }
            if(status){
                AlertBox.display("Login Success","You logged in");
            }else{
                AlertBox.display("Login Fail", "System cannot identity a user with the credential you entered \n           please check and try again");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
