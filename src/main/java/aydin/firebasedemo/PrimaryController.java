package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {
    @FXML
    private TextField ageTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button readButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button switchSecondaryViewButton;

    @FXML
    private Button writeButton;
    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField emailTextField;


    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {

        AccessDataView accessDataViewModel = new AccessDataView();
        nameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }


    @FXML
    void readButtonClicked(ActionEvent event) {
        readFirebase();
    }


    @FXML
    void writeButtonClicked(ActionEvent event) {
        addData();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        DemoApp.setRoot("secondary");
    }

    public boolean readFirebase() {
        key = false;

        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Persons").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if (documents.size() > 0) {
                System.out.println("Getting (reading) data from firabase database....");
                listOfUsers.clear();
                for (QueryDocumentSnapshot document : documents) {
                    outputTextArea.setText(outputTextArea.getText() + document.getData().get("Name") + " , Age: " +
                            document.getData().get("Age") + ",Phone Number: " + document.getData().get("PhoneNumber") +  " \n ");
                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
                    person = new Person(String.valueOf(document.getData().get("Name")),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);
                }
            } else {
                System.out.println("No data");
            }
            key = true;

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    public boolean registerUser(String phone, String password, String name, String email, int age) {
        if (email == null || email.trim().isEmpty() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {

            System.out.println("Error: Invalid email format.");
            return false;
        }

        // Validate phone number format (assuming E.164 format)
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("Error: Phone number cannot be null or empty.");
            return false;
        }

        // Prepend country code if necessary (assuming US country code)
        if (!phone.startsWith("+")) {
            phone = "+1" + phone; // Adjust based on your requirements
        }

        // Create user request
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber(phone)
                .setDisplayName(name)
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            System.out.println("Error creating a new user in Firebase: " + ex.getMessage());
            return false; // Handle other Firebase errors as needed
        }
    }



    public void addData() {

        DocumentReference docRef = DemoApp.fstore.collection("Persons").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameTextField.getText());
        data.put("Password", passwordTextField.getText());
        data.put("PhoneNumber", phoneTextField.getText());
        data.put("Age", Integer.parseInt(ageTextField.getText()));
        data.put("Email", emailTextField.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean checkUser(String temp_username, String temp_password) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Persons")
                .whereEqualTo("Name", temp_username)
                .whereEqualTo("Password", temp_password)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            System.out.println("User login.");
            return true;
        }
        else
            System.out.println("User login fail. Username or password is wrong");
            return false;
    }
}
