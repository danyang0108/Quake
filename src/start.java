import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class start extends Application{
	
	public static void main(String args[]) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		//Initializing variables 
		int scene_x = 600;
		int scene_y = 600;
		HBox root= new HBox();
		Scene scene=new Scene(root,scene_x,scene_y);
		
		
		primaryStage.setTitle("Culminating Project");		//display title
		primaryStage.setScene(scene);	
		primaryStage.show(); 	
	}
	
	
}