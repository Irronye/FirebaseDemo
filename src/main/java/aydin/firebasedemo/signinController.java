package aydin.firebasedemo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class signinController extends PrimaryController{
    @FXML
    private TextField lonin_username;
    @FXML
    private TextField lonin_password;
    @FXML
    private Button submit_password_button;
    @FXML
    void submit_password_buttonClicked(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        if (checkUser(lonin_username.getText(), lonin_password.getText())){
            DemoApp.setRoot("primary");
        }
    }

}
