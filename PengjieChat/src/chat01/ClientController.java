
package chat01;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import DES.DES;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



public class ClientController implements Initializable{ //
	
	private String userName = null;
	private Socket clientSocket = null;
	private String strSend;
	private DataOutputStream dos = null;
	private boolean isConnect;
	private Client client;
	private Client selectedClient;
	private DES des = new DES();
	private ObservableList<String> onlineNames;
	private ArrayList<String> onlineClientList = new ArrayList<String>();
	//private Message message;
	
	@FXML
	private Button sendMessageButton;
	@FXML
	private TextArea messageDisplay;
	@FXML
	private Label welcomInfo;
	@FXML
	private TextField messageText;
	@FXML
	private Pane frameWindowPane;
	@FXML
	private Button privateChatButton;
	@FXML
	private ListView<String> clientListView = new ListView<String>();


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		frameWindowPane.setOnMouseMoved(new EventHandler<MouseEvent>() {		
			@Override
			public void handle(MouseEvent event) {
				
				if(!isConnect)
				receiveDataFromLoginPage(event);
						
			}
		});
	}
	

	public void checkOnlineClientsButtonPressed(ActionEvent event) throws IOException {
		
		
		System.out.println(onlineClientList.toString());
	
		for(int i = 0; i < onlineClientList.size(); i++) {
			String name = onlineClientList.get(i);
			onlineNames.add(name);
		}
				
		onlineNames = FXCollections.observableArrayList();		
		clientListView.setItems(onlineNames);
		
	}
			
	//https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8#:~:text=Get%20the%20instance%20of%20the,pass%20it%20to%20the%20stage
	private void receiveDataFromLoginPage(Event event) {
		
		//Step 1
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		
		//Step 2
		client = (Client) stage.getUserData();
		//onlineClientList.add(client);

		userName = client.getUserName();
		stage.setTitle(userName);
		
		clientSocket = client.getClientSocket();	
		// Step 3		
		isConnect = true; 
		new Thread(new Receive()).start();
		welcomInfo.setText("Welcome! " + userName + "!");
		messageDisplay.setEditable(false);
		
	}
				

	public void setSendMessageButtonAction() throws Exception {
		
		strSend = messageText.getText();
		//encrypt message
		strSend = des.encrypt(strSend);
		if (strSend.trim().length() == 0) {
			return;
		}
		sendMessageToServer(strSend + "\n");
		messageText.setText("");
	}
	
	public void setSendMessagekeyEnterLister(KeyEvent e) throws Exception {

		if (e.getCode() == KeyCode.ENTER) {
			strSend = messageText.getText();
			strSend = des.encrypt(strSend);
			if (strSend.trim().length() == 0) {
				return;
			}
			sendMessageToServer(strSend + "\n");
			messageText.setText("");
		}

	}
	

	// send message to server
	public void sendMessageToServer(String strSend) {
		
		if (isConnect) {			
			try {

				dos = new DataOutputStream(clientSocket.getOutputStream());															
				dos.writeUTF(userName + " said: " + strSend + "\n");				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {		
			System.out.println("not connect server!");
		}

	}

	class Receive implements Runnable {		
	    
		@Override
		public void run() {
			
			try {
				while (isConnect) {
					InputStream inputStream = clientSocket.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					//step1: read before decrypt
					String receivedStr = bufferedReader.readLine();
					if(receivedStr.contains("Broadcast news:")) {
						messageDisplay.appendText(receivedStr + "\n");
					}else 
						if(receivedStr.contains("onlineClientName:")){	
						onlineClientList.add(receivedStr.substring(2).split(":")[1]);		
					}else{			
					String senderName = receivedStr.split(" said: ")[0];
					String messageContent = receivedStr.split(" said: ")[1];					
					//step2: decrypt message
					messageContent = des.decrypt(messageContent);
					//step3: append to messageDisplay area				
					messageDisplay.appendText(senderName + " said: " + messageContent + "\n");
					}
				}
			} catch (SocketException e) {
				System.out.println("Server has suddently broken off");
				messageDisplay.appendText("Server has suddently broken off");
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
