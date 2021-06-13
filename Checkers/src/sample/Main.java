package sample;

//import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.stage.EmbeddedWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.w3c.dom.css.Rect;

import javax.naming.TimeLimitExceededException;
import javax.print.DocFlavor;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root1 = new BorderPane();
        root1.setStyle("-fx-background-color: yellow");

        Scene scene = new Scene(root1);
        root1.prefHeightProperty().bind(scene.heightProperty());
        root1.prefWidthProperty().bind(scene.widthProperty());

        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        AnchorPane pane = new AnchorPane();
        Button button1 = new Button("AI VS AI");
        button1.setStyle("-fx-font-size:25");
        pane.getChildren().add(button1);
        button1.setLayoutX(50);
        button1.setLayoutY(300);
       button1.setMinSize(300,100);

        Button button2 = new Button("HUMAN VS AI");
        pane.getChildren().add(button2);
        button2.setStyle("-fx-font-size:25");
        button2.setLayoutX(50);
        button2.setLayoutY(500);
        button2.setMinSize(300,100);

        Button button3 = new Button("HUMAN VS HUMAN");
        pane.getChildren().add(button3);
        button3.setStyle("-fx-font-size:25");
        button3.setLayoutX(50);
        button3.setLayoutY(700);
        button3.setMinSize(300,100);

        root1.setLeft(pane);

        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Environment obj = new Environment(root1 , 1);
                Vector <Pair<Integer, Integer>> emp = new Vector<>();

                Timeline timeline = new Timeline(

                        new KeyFrame(Duration.millis(1000),f -> {
                            obj.ai_move(emp,1 , obj.black,obj.white,obj.black_king,obj.white_king);
                            obj.ai_move(emp,2 , obj.black,obj.white,obj.black_king,obj.white_king);
                        }
                        )
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        });

        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Environment obj = new Environment(root1 , 2);
                obj.highlight(1);
            }
        });

        button3.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Environment obj = new Environment(root1 , 3);
                obj.highlight(1);
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}
