
package chat01;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class ServerController implements Observable{

	Server server;
	int port; 
	@FXML
	private TextField portInput;
	@FXML
	private Label portLabel;
	@FXML
	private TextArea serverDisplayText;
	@FXML
	private Button serverStartButton;
	@FXML
	private Button serverStopButton;
	@FXML
	private Label feedback;
	
	//serverDisplayText.setEditable(false);
	
	public ServerController() {
		super();
	}	

	public int getPort() {
		return port;
	}


	public void startServer() throws IOException {
		
		serverDisplayText.setEditable(false);
		port = Integer.parseInt(portInput.getText());
		//port range1024～ 65535
		if(port>1024 && port<65535) {
			server = new Server(port, this);
			new Thread(server).start();
			feedback.setText("PORT: " + port + " is ready, waiting for clients to connect!");
		}else {
			feedback.setText("Invalide input, please input again!");
		}
	}
	

	public void stopServer() {

		if (server.serverSocket != null) {
			try {
				server.serverSocket.close();
				server.serverSocket = null;
				serverDisplayText.appendText("Server disconnect!");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void append(String txt) {

		String currentContent = serverDisplayText.getText();
		serverDisplayText.setText(currentContent + "\n" + txt);

	}

}
