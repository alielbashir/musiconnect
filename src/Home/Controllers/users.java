package Home.Controllers;

import Home.App;
import Home.Helper;
import Home.Modules.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import static Home.Helper.*;

public class users implements Initializable {
    @FXML
    Text displayName;
    @FXML
    Circle profilePicture;
    @FXML
    ChoiceBox<String> navigator;
    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private ChoiceBox<String> country;

    @FXML
    private TextField picturePath;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> ID;

    @FXML
    private TableColumn<User, String> colName;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colCountry;

    @FXML
    private TableColumn<User, String> colPath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUserData(displayName, profilePicture);
        populateAdminNavigator(navigator, "Users");
        importUsers();
        importCountries(country);
    }


    @FXML
    private void importUsers() {
        final ObservableList<User> data = FXCollections.observableArrayList();
        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        colPath.setCellValueFactory(new PropertyValueFactory<>("picture"));
        String query = "SELECT * FROM app_user ORDER BY id";
        try (PreparedStatement statement = App.connection.prepareStatement(query)) {
            ResultSet resultSet = executeQuery(statement);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String picture = resultSet.getString("picture");
                int countryID = resultSet.getInt("country_id");
                String country = getCountry(countryID);
                data.add(new User(id, name, email, country, picture));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        usersTable.setItems(data);
    }

    @FXML
    private void createUser() throws SQLException {
        if (!email.getText().equals("") && email.getText() != null) {
            String emailQuery = "SELECT email FROM app_user WHERE email = ?";

            PreparedStatement emailStatement = App.connection.prepareStatement(emailQuery);
            emailStatement.setString(1, email.getText());

            ResultSet emailResult = executeQuery(emailStatement);
            if (emailResult.next()) {
                App.showError("this email already exists", "please change the email");
                return;
            }
            HashMap<String, Integer> countries = createCountries();
            String query = "INSERT INTO app_user(name, email, password_hash, country_id, picture) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = App.connection.prepareStatement(query);
            statement.setString(1, name.getText());
            statement.setString(2, email.getText());
            statement.setString(3, getHashedPassword(password.getText()));
            statement.setInt(4, countries.get(country.getValue()));
            statement.setString(5, picturePath.getText());
            execute(statement);
            importUsers();
            App.showSuccessMessage("user " + name.getText() + " has been created", "");
            clear();
        }
    }

    @FXML
    private void deleteUser() throws SQLException {
        if (email.getText() != null && !email.getText().equals("")) {
            String query = "DELETE FROM  app_user WHERE email = ?";
            PreparedStatement statement = App.connection.prepareStatement(query);
            statement.setString(1, email.getText());
            execute(statement);
            App.showSuccessMessage("user " + name.getText() + " has been deleted", "");
            importUsers();
            clear();
        }
    }


    @FXML
    private void updateUser() throws SQLException {
        if (!email.getText().equals("") && email.getText() != null) {
            User user =usersTable.getSelectionModel().getSelectedItem();
            int id;
            if(user==null)
                id=-1;
            else
                id=user.getID();
            String idQuery = "SELECT * FROM app_user WHERE id = ?";

            PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
            idStatement.setInt(1, id);

            ResultSet idResult = executeQuery(idStatement);
            if (!idResult.next()) {
                App.showError("user doesn't exist", "");
                return;
            }
            HashMap<String, Integer> countries = createCountries();
            String query = "UPDATE  app_user SET name =?,email =?,password_hash =?,country_id =?,picture = ? WHERE id = ?";
            PreparedStatement statement = App.connection.prepareStatement(query);
            statement.setString(1, name.getText());
            statement.setString(2, email.getText());
            statement.setString(3, getHashedPassword(password.getText()));
            statement.setInt(4, countries.get(country.getValue()));
            statement.setString(5, picturePath.getText());
            statement.setInt(6, id);

            if (execute(statement) != 0) {
                importUsers();
                App.showSuccessMessage("user " + name.getText() + " has been updated", "");
            } else {
                App.showError("User does not exist", "");
            }
            clear();

        }
    }

    @FXML
    private void onRowClickAction() throws SQLException {
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            picturePath.setText(user.getPicture());
            String query = "SELECT * FROM app_user WHERE email = ?";
            PreparedStatement statement = App.connection.prepareStatement(query);
            statement.setString(1, email.getText());
            ResultSet resultSet = executeQuery(statement);
            resultSet.next();
            int countryID = resultSet.getInt("country_id");
            String country = getCountry(countryID);
            this.country.getSelectionModel().select(country);
        }
    }

    private void clear() {
        name.setText("");
        email.setText("");
        password.setText("");
        picturePath.setText("");
        country.getSelectionModel().select(0);
    }

    @FXML
    private void selectProfileImage() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            picturePath.setText(selectedFile.getPath());
        }
    }



    private String getCountry(int id) throws SQLException {
        String query = "SELECT * FROM country WHERE id = ?";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        return resultSet.getString("name");
    }


    @FXML
    private void logoutApp() throws IOException {
        App.navigateTo("login");
    }

    @FXML
    private void close() {
        App.close();
    }

    @FXML
    private void navigate() {
        Helper.navigateAdmin(navigator);
    }
}
