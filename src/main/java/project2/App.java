/*UMGC CMSC 3135
   Description: GUI driven interface that allows user to select a shape to be drawn. 
* Author: Tyler Marino
* Date: Feb 6th, 2024
  File Name: App.java
* Java 11
 */
package project2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    private static Scene scene1;
    private static Scene shapeScene;
    private Button buttonDraw;
    static RotationalGroup group;
    ComboBox<String> dimensions;
    static Stage shapewindow;
    String lastPressed;
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 800;
    Label shapeViewerInstr;
 
    
    @SuppressWarnings("exports")
    @Override
    public void start(Stage window) throws IOException {
        window.setTitle("Shapes Application");
        Label mainLabel = new Label("Select shape paramaters: ");
        Label dimensionsLabel = new Label("Dimensions: ");
        Label shapesLabel = new Label("Shape: ");
        shapeViewerInstr = new Label("For 3D shape, use arrow keys to rotate 'z' to zoom in and 'x' to zoom out");
        shapeViewerInstr.setStyle("-fx-font-weight: bolder");

    //Components initialization
        //Dimensions ComboBox
        ComboBox<String> dimensions = new ComboBox<String>(); 
        dimensions.getItems().addAll("2D", "3D");
        dimensions.setValue("");
    
        //Shapes ComboBox
        ComboBox<String> shapes= new ComboBox<String>(FXCollections.observableArrayList(
                "Square", "Circle", "Triangle")); 
        shapes.setDisable(true);
        dimensions.valueProperty().addListener((obs, oldVal, newVal)-> {
            lastPressed = dimensions.getValue();
            if (lastPressed  == "3D"){
                shapes.getItems().setAll("Sphere", "Cube", "Cylinder");
                shapes.setDisable(false);
            } else if(lastPressed == "2D"){
                shapes.getItems().setAll("Square", "Circle", "Triangle");
                shapes.setDisable(false);
            }
        });

        //Draw shapes button
        buttonDraw= new Button("Draw Shape");
        buttonDraw.setDisable(true);
        shapes.valueProperty().addListener((obs, oldVal, newVal)->{
            if(!shapes.isDisabled())
            buttonDraw.setDisable(false);
        });
        buttonDraw.setOnAction(e -> drawShape(shapes.getValue()));
      
        //Layout 1
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(mainLabel, dimensionsLabel, dimensions, shapesLabel, shapes, buttonDraw, shapeViewerInstr);

        //Initialize window and scenes
        scene1 = new Scene(layout, 600, 600);
        scene1.getStylesheets().add("style.css");
        window.setScene(scene1);
        window.show();
    }
    
    private void drawShape(String shapeName) {
        //Set up shape window
        shapewindow = new Stage();
        try{
            group = new RotationalGroup();
            group.setAutoSizeChildren(false);
            shapeScene = new Scene(group, WIDTH, HEIGHT);
            shapeScene.setFill(Color.WHITESMOKE);

            group.translateXProperty().set(WIDTH/2);
            group.translateYProperty().set(HEIGHT/2);
            group.translateZProperty().set(-1000);

            shapewindow.setScene(shapeScene);
            shapewindow.show();

            loadShape(shapeName);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /***
     * Takes user choice String and draws the shape in new window
     * @param shapeName
     */
    private void loadShape(String shapeName) {
       //Create Scene

        PerspectiveCamera cam = new PerspectiveCamera();
        PhongMaterial material = new PhongMaterial();
        shapeScene.setCamera(cam);
        //Set rotation handler
        shapewindow.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(lastPressed != "2D"){
                switch (e.getCode()) {
                    case Z: 
                        group.translateZProperty().set(group.getTranslateZ() + 100);
                        break;
                    case X: 
                        group.translateZProperty().set(group.getTranslateZ() -100);
                        break;
                    case RIGHT: 
                        group.rotateY(10);
                        break;
                    case LEFT: 
                        group.rotateY(-10);
                        break;
                    case UP: 
                        group.rotateX(10);
                        break;
                    case DOWN: 
                        group.rotateX(-10);
                        break;
                }
            }
        });
        
        //2D shape options
        switch (shapeName.toLowerCase()){
            case "circle":
                Circle circle = new Circle(100, Color.AQUAMARINE);
                group.getChildren().add(circle);
                break;
            case "square":
                Rectangle sqr = new Rectangle(100, 100, Color.GREEN);
                sqr.translateXProperty().set(-50.0);
                sqr.translateYProperty().set(-50.0);
                group.getChildren().add(sqr);  
                break;
            case "triangle":
                Polygon tri = new Polygon();
                tri.getPoints().setAll(new Double[]{
                    0.0, -50.0,
                    -50.0, 50.0,
                    50.0, 50.0
                });
                
                tri.setFill(Color.CORAL);
                group.getChildren().add(tri);                
                break;
            //3D Shape options
            case "sphere":
                Sphere sphere = new Sphere(100);
                
                material.setDiffuseColor(Color.valueOf("#85ff7c"));
                sphere.setMaterial(material);
                group.getChildren().add(sphere);
                
                break;
            case "cube":
                Box box = new Box(100.0, 100.0, 100.0);  
                material.setDiffuseColor(Color.valueOf("#d5e127"));
                box.setMaterial(material);
                group.getChildren().add(box);
                break;
            case "cylinder":
                Cylinder cylinder = new Cylinder(50.0, 100.0);
                material.setDiffuseColor(Color.valueOf("#7cc3ff"));
                cylinder.setMaterial(material);
                group.getChildren().add(cylinder);
                break;
            default:
                throw new IllegalArgumentException("How did you get to this point? lol");
            }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    class RotationalGroup extends Group {
        Rotate r;
        Transform t= new Rotate();

        void rotateX(float angle){
            r = new Rotate(angle, Rotate.X_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }
        void rotateY(float angle){
            r = new Rotate(angle, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }
    }
    
}