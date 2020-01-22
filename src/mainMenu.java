///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 12th, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This class provides a start menu for the first-person shooter game.
 * The menu is made using JavaFX. The start button loads the game.
 */
import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.nio.file.Paths;

public class mainMenu extends Application{

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
        
        //display a background for the start menu
        ImageView background=loadBackground("Resource/Images/Wall.jpg",scene_x,scene_y);
        background.setX(0);
        background.setY(0);
        
        //display title
        Text title = new Text("Quake ICS4U");
        lfont = 50; sfont  = 25;
        title.setFont(Font.font ("Verdana", FontWeight.BOLD,lfont));
        title.setFill(Color.WHITE);
        int title_x = 275, title_y = 100;
        title.setX(title_x);
        title.setY(title_y);
        
        //provides a text box to get username
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
        
        //stores the username after the user presses enter
        enterUser.setOnAction(arg0 -> {
            name = enterUser.getText();
        });

        
        //this button launches the first-person shooter game
        Button button = new Button("Start Game");
        int button_x = scene_x/2 - 100, button_y = scene_y/2 + 25;
        int button_w = 200, button_h = 75;
        button.setLayoutX(button_x);
        button.setLayoutY(button_y);
        button.setPrefWidth(button_w);
        button.setPrefHeight(button_h);
        button.setOnAction(arg0 -> {
            try{
                legacyGL GL = new legacyGL();
                GL.run();
                
                //prints the score after the player dies
                scene_container.getChildren().clear();
                scene_container.getChildren().add(background);
                
                //prints username
                Text endUser = new Text("User: " + name);
                int endUserX = 200;
                int endUserY = 200;
                endUser.setTranslateX(endUserX);
                endUser.setTranslateY(endUserY);
                endUser.setFont(Font.font ("Verdana", FontWeight.BOLD,sfont));
                endUser.setFill(Color.WHITE);
                
                //prints time survived
                Text endTime = new Text("Time: " + Integer.toString(GL.gettime()));
                int endTimeX = 200;
                int endTimeY = 350;
                endTime.setTranslateX(endTimeX);
                endTime.setTranslateY(endTimeY);
                endTime.setFont(Font.font ("Verdana", FontWeight.BOLD,sfont));
                endTime.setFill(Color.WHITE);
                
                //prints number of enemies killed
                Text endKill = new Text("Kills: " + Integer.toString(GL.getElimination()));
                int endKillX = 200;
                int endKillY = 500;
                endKill.setTranslateX(endKillX);
                endKill.setTranslateY(endKillY);
                endKill.setFont(Font.font ("Verdana", FontWeight.BOLD,sfont));
                endKill.setFill(Color.WHITE);
                scene_container.getChildren().addAll(endUser, endTime, endKill);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        scene_container.getChildren().addAll(background,button,title,user, enterUser);
        primaryStage.setTitle("Culminating Project");		//display title
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //this method loads an image based on the file location
    //rescales the image based on the user input - width and height
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