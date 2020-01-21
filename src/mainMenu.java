///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 12th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class provides a start menu for the first-person shooter game.
 * The menu is made using JavaFX. The start button loads the game.
 */
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    private int scene_x, scene_y;
    private int lfont, sfont;
    private String name;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        //Initializing variable
        int scene_x = 900;
        int scene_y = 600;
        Group root= new Group();
        Group scene_container = new Group();
        HBox control = new HBox();
        root.getChildren().addAll(scene_container,control);
        Scene scene=new Scene(root,scene_x,scene_y);
        ImageView background=loadBackground("Resource/Images/Wall.jpg",scene_x,scene_y);
        background.setX(0);
        background.setY(0);
        Text title = new Text("Quake ICS4U");
        lfont = 50; sfont  = 25;
        title.setFont(Font.font ("Verdana", FontWeight.BOLD,lfont));
        title.setFill(Color.WHITE);
        int title_x = 275, title_y = 100;
        title.setX(title_x);
        title.setY(title_y);
        Text user = new Text("Username: ");
        user.setFont(Font.font ("Verdana", FontWeight.BOLD,sfont));
        user.setFill(Color.WHITE);
        int user_x = 225, user_y = 200;
        user.setX(user_x);
        user.setY(user_y);
        TextField enterUser = new TextField("enter username");
        int field_x = 400, field_y = 165, field_w = 300, field_h = 50;
        enterUser.setLayoutX(field_x);
        enterUser.setLayoutY(field_y);
        enterUser.setPrefWidth(field_w);
        enterUser.setPrefHeight(field_h);
        enterUser.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                name = enterUser.getText();
            }
        });

        Button button = new Button("Start Game");
        int button_x = scene_x/2 - 100, button_y = scene_y/2 + 25;
        int button_w = 200, button_h = 75;
        button.setLayoutX(button_x);
        button.setLayoutY(button_y);
        button.setPrefWidth(button_w);
        button.setPrefHeight(button_h);
        button.setOnAction(arg0 -> {
            try{
                new legacyGL().run();
                System.out.println("CHECK");
            }catch (Exception e){
                e.printStackTrace();
            }
        });
       
        scene_container.getChildren().addAll(background,button,title,user, enterUser);
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
        ImageView background = new ImageView(image);
        return background;
    }
}