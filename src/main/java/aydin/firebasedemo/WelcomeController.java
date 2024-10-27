package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class WelcomeController extends PrimaryController {

    @FXML
    private Button SignIn_Button;
    @FXML
    private TextField WelcomephoneTextField;
    @FXML
    private TextField WelcomepasswordTextField;
    @FXML
    private TextField WelcomeageTextField;
    @FXML
    private TextField WelcomenameTextField;
    @FXML
    private TextField WelcomeemailTextField;

    @FXML
    private void registerButtonClicked() {
        String phone = WelcomephoneTextField.getText().trim();
        String password = WelcomepasswordTextField.getText().trim();
        String name = WelcomenameTextField.getText().trim();
        String ageText = WelcomeageTextField.getText().trim();
        String email = WelcomeemailTextField.getText().trim();
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Age", "Please enter a valid integer for age.");
            return;
        }
        if (phone.length() < 10) {
            showAlert("Invalid Phone Number", "Phone number must be at least 10 digits.");
            return;
        }
        if (!phone.startsWith("+")) {
            phone = "+1" + phone;
        }
        if (registerUser(phone, password, name, email, age)) {
            showAlert("Success", "User registered successfully.");
        } else {
            showAlert("Registration Error", "Error creating a new user in Firebase.");
        }
    }

    @FXML
    void SignIn_ButtonClicked(ActionEvent event) throws IOException {
        DemoApp.setRoot("signin");
        // DemoApp.setRoot("primary");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

