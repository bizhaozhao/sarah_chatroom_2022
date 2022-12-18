/**
 * 
 */
package chat01;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class RegisterController {
	
	
	private Scene scene;
	private Stage stage;
	private Parent root;
	Client client;

	
	@FXML
	private TextField userName;
	@FXML
	private PasswordField password;
	@FXML
	private Button registerButton;
	@FXML
	private Label goToLoginPage;
	@FXML
	private Label checkFeedback;
	
	
	private void register() {
		
		
		
	}
	
	
	private void checkUsernameDuplicate() {
		
		
		
	}
	
	
	private void showCheckFeedback() {
		
		
	}
	
	
	public void goToLoginPage(MouseEvent event) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	
	
}
