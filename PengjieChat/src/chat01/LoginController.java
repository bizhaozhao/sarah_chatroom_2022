/**
 * 
 */
package chat01;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Target;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {//extends Applicationimplements Initializable

	private Socket clientSocket = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	boolean isConnect = false;
	private String userName;
	private String password;
	boolean flag = false;
	Client client;

	@FXML
	private TextField usernameInput;
	@FXML
	private PasswordField passwordInput;
	@FXML
	private TextField portnumberInput;
	@FXML
	private TextField ipAddressInput;
	@FXML
	private Button loginButton;
	@FXML
	private Label forgotPassword;
	@FXML
	private Label goToRegisterPage;
	@FXML
	private Label invalidFeedback;
	
	
	
	public void loginProcess(ActionEvent event) {

		connectToServer();

		if (checkUsernameAndPassordValidation()) {
			sendUsernameAndPasswordToServer();
			if (receiveCheckResultFromSever()) {
				try {	
					goToClientside(event);	
					sendClientNameToServer(userName);
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}
		}

	}
	
	

	public void connectToServer() {

		int port = Integer.parseInt(portnumberInput.getText());
		String ip = ipAddressInput.getText();
		
		try {
			clientSocket = new Socket(ip, port);
			isConnect = true;		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendUsernameAndPasswordToServer() {

		if (isConnect) {

			userName = usernameInput.getText();
			password = passwordInput.getText();

			client = new Client(userName, password, clientSocket);				
			String userInfo = userName + "," + password;
			
			try {
				OutputStream osOutputStream = clientSocket.getOutputStream();
				PrintWriter pw = new PrintWriter(osOutputStream);
				pw.println(userInfo);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			invalidFeedback.setText("not connect server!");
		}
	}
	
	public void sendClientNameToServer(String name) {

		if (isConnect) {
			try {
				dos = new DataOutputStream(clientSocket.getOutputStream());
				dos.writeUTF(userName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("not connect server!");
		}
	}

	private boolean receiveCheckResultFromSever() {

		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			String resultStr = dis.readUTF();
			if (resultStr.equals("success")) {
				flag = true;
			} else {
				invalidFeedback.setText("Your username or password is invalid!");
				flag = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private boolean checkUsernameAndPassordValidation() {

		if (userName == "" || password == "") {
			invalidFeedback.setText("Invalid input!");
			return false;
		}
		return true;
	}
	
	//Reference: pass information between scenes 
	//https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8#:~:text=Get%20the%20instance%20of%20the,pass%20it%20to%20the%20stage
	private void goToClientside(ActionEvent event) throws IOException {

		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("chat01/Client.fxml"));
			stage.setUserData(client);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			System.err.println(String.format("Error: %s", e.getMessage()));
		}


	}

	public void goToRegisterPage(MouseEvent event) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle("Register");
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}


	private void forgotPassword() {

		
		
	}


}
