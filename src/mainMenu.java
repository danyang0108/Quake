import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class mainMenu extends Application{
    private Button button;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        //Initializing variable
        int scene_x = 600;
        int scene_y = 600;
        HBox root= new HBox();
        Scene scene=new Scene(root,scene_x,scene_y);
        button = new Button("Start Game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {

            }
        });
        root.getChildren().addAll(button);

        primaryStage.setTitle("Culminating Project");		//display title
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}