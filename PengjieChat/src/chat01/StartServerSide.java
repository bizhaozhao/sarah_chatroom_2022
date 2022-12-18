
package chat01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartServerSide extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Server.fxml"));
			primaryStage.setResizable(false);
			Scene scene = new Scene(root);	
			primaryStage.setTitle("Server Side");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

	
	public static void main(String[] args) {
			launch(args);
		}

}
