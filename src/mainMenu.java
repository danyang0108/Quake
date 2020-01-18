import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class mainMenu extends Application{

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        //Initializing variable
        int scene_x = 900;
        int scene_y = 600;
        Group root= new Group();
        Scene scene=new Scene(root,scene_x,scene_y);
        ImageView background=loadBackground("Resource/Images/Wall.jpg",scene_x,scene_y);
        background.setX(0);
        background.setY(0);
        Text text = new Text("Quake ICS4U");
        text.setFont(Font.font ("Verdana", FontWeight.BOLD,50));
        text.setFill(Color.WHITE);
        text.setX(300);
        text.setY(100);
        Button button = new Button("Start Game");
        button.setLayoutX(scene_x/2-100);
        button.setLayoutY(scene_y/2-25);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {

            }
        });
        Button instruct = new Button("Instructions");
        instruct.setLayoutX(scene_x/2-100);
        instruct.setLayoutY(scene_y/2+30);
        instruct.setPrefWidth(200);
        instruct.setPrefHeight(50);
        instruct.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {

            }
        });
        root.getChildren().addAll(background, button, instruct, text);

        primaryStage.setTitle("Culminating Project");		//display title
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ImageView loadBackground(String FileLocation, int width, int height){
        String url="";
        try {
            url = Paths.get(FileLocation).toUri().toURL().toString();
        } catch (MalformedURLException e) {
            System.out.println("Invalid File");
            e.printStackTrace();
        }
        Image image=new Image(url,width,height,true,false);
        ImageView map = new ImageView(image);
        return map;
    }
}