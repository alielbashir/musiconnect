package Home.Controllers;

import Home.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Home.Helper.executeQuery;

public class adminPanel implements Initializable {
    @FXML Text displayName;
    @FXML Circle profilePicture;
    @FXML ChoiceBox navigator;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         instantiateAdmin();
         populateNavigator();
    }


   private void instantiateAdmin(){
       String query = "SELECT * FROM app_user WHERE email = ?";
       try (PreparedStatement statement = App.connection.prepareStatement(query)) {
           statement.setString(1, App.getUserEmail());
           ResultSet resultSet = executeQuery(statement, query);
           resultSet.next();
           String username = resultSet.getString("name");
           String imagePath = resultSet.getString("picture");
           displayName.setText(username);
           File imageFile = new File(imagePath);
           String imageLocation = imageFile.toURI().toString();
           Image pic = new Image(imageLocation,false);
           profilePicture.setFill(new ImagePattern(pic));

       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }
   }
    @FXML private void logoutApp() throws IOException {
        App.navigateTo("login");
    }
    private void populateNavigator(){
        ObservableList<String> pages = FXCollections.observableArrayList();
        pages.addAll("Dashboard","Users","Singers","Albums","Songs");
        navigator.setItems(pages);
        navigator.getSelectionModel().select(0);
    }

}
