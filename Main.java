package sample;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    private static SubScene subScene;
    private static ChoiceBox<String> colorChoice;

    //Menu Items
    private static MenuItem saveItem;
    private static MenuItem importItem;
    private static MenuItem exitItem;

    //File Accessors
    private static File curFilePath;
    private static String shapename = "";
    private static Double shapeheight;
    private static Double shapewidth;
    private static Double shaperadius;
    private static Double shapex;
    private static Double shapey;
    private static Double shapedepth;
    private static double shapescalex;
    private static double shapescaley;
    private static double shapescalez;
    private static double shaperotate;
//    private Box box;
//    private Sphere sphere;

    private static PerspectiveCamera pCamera = new PerspectiveCamera(true);
    private static int pCameraX;
    private static int pCameraY;
    private static int pCameraZ = -60;

    private static BorderPane borderPane = new BorderPane();
    private static VBox sceneAndButton;

    private static String shapeType;
    private static Label shapeTypeLabel;
    private static ListView<String> shapeTypeList;

    //box measurements
    private static TextField BDepthField = new TextField();
    private static TextField BWidthField = new TextField();
    private static TextField BHeightField = new TextField();
    private static TextField BXField = new TextField();
    private static TextField BYField = new TextField();
    private static double BDepth;
    private static double BWidth;
    private static double BHeight;
    private static int BX;
    private static int BY;
    private static boolean BDepthValid;
    private static boolean BWidthValid;
    private static boolean BHeightValid;
    private static boolean BXValid;
    private static boolean BYValid;

    //sphere measurements
    private static TextField SRadiusField = new TextField();
    private static TextField SXField = new TextField();
    private static TextField SYField = new TextField();
    private static TextField xTranslateTextField;
    private static TextField yTranslateTextField;
    private static int SX;
    private static int SY;
    private static double SRadius;
    private static boolean SRadiusValid;
    private static boolean SXValid;
    private static boolean SYValid;


    //cylinder measurements
    private static TextField CHeightField = new TextField();
    private static TextField CRadiusField = new TextField();
    private static TextField CXField = new TextField();
    private static TextField CYField = new TextField();
    private static double CRadius;
    private static double CHeight;
    private static int CX;
    private static int CY;
    private static boolean CRadiusValid;
    private static boolean CHeightValid;
    private static boolean CXValid;
    private static boolean CYValid;

    private static Label newXLabel = new Label("x: ");
    private static Label newYLabel = new Label("y: ");


    private static Button translateSubmit = new Button("Apply Translation");
    private static Button deleteButton = new Button("Delete Shape");

    private static Button submitButton;
    private static VBox submit;

    private static Shape3D selectedShape = null;
    private static ArrayList<Shape3D> mainShapeList = new ArrayList<>();
    private static Group shapesGroup = new Group();

    private static TextField rotationAmount = new TextField();
    private static Boolean degreeValid = false;
    private static double check = 0;
    private static Button rotationAdd;
    private static Button rotationSub;
    private static Point3D axisOfRotation = Rotate.X_AXIS;
    private static ChoiceBox<String> chooseRotationAxis;
    private static String rotationAxis = "X";



    private static BorderPane addShapePane = new BorderPane();




    @Override
    public void start(Stage primaryStage) throws Exception{

        Label title = new Label("◩   3 D  S H A P E S   ◪");
        title.setStyle("-fx-font-family: serif");
        title.setStyle("-fx-font-size: 20 pt");

        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        menuBar.getMenus().add(file);
        saveItem = new MenuItem("Save");
        importItem = new MenuItem("Import");
        exitItem = new MenuItem("Exit");
        file.getItems().addAll(saveItem,new SeparatorMenuItem(),importItem,exitItem);
        menuHandler(primaryStage);

        borderPane.setTop(menuBar);

        shapeTypeLabel = new Label("Choose a shape to create");
        shapeTypeList = new ListView<>();
        submitButton = new Button("Submit");
        submit = new VBox(submitButton);
        submit.setAlignment(Pos.CENTER_RIGHT);
        submitButton.setDisable(true);

        TextFieldListener validListener = new TextFieldListener();
        BDepthField.textProperty().addListener(validListener);
        BWidthField.textProperty().addListener(validListener);
        BHeightField.textProperty().addListener(validListener);
        SRadiusField.textProperty().addListener(validListener);
        CHeightField.textProperty().addListener(validListener);
        CRadiusField.textProperty().addListener(validListener);
        BXField.textProperty().addListener(validListener);
        BYField.textProperty().addListener(validListener);
        SXField.textProperty().addListener(validListener);
        SYField.textProperty().addListener(validListener);
        CXField.textProperty().addListener(validListener);
        CYField.textProperty().addListener(validListener);


        shapeTypeList.getItems().addAll("Box", "Sphere", "Cylinder");
        shapeTypeList.setPrefHeight(75);
        shapeTypeList.getSelectionModel().selectedItemProperty().addListener(new ShapeTypeListener());

        addShapePane = new BorderPane();
        VBox shapeSelection = new VBox(10, shapeTypeLabel, shapeTypeList);
        addShapePane.setTop(shapeSelection);
        addShapePane.setPadding(new Insets(15));
        Button addShapeButton = new Button("Add New Shape");

        addShapeButton.setOnAction(event -> {
            borderPane.setCenter(addShapePane);
            resetFields();
        });


        createControls();  //sets borderPane



        subScene = new SubScene(shapesGroup, 340,340, true, SceneAntialiasing.DISABLED);
        subScene.setFill(Color.LIGHTGRAY);

        sceneAndButton = new VBox(20, title, subScene, addShapeButton);
        sceneAndButton.setAlignment(Pos.CENTER);
        sceneAndButton.setPadding(new Insets(20));

        borderPane.setCenter(sceneAndButton);


        subScene.setCamera(pCamera);
        pCamera.getTransforms().add(new Translate(0,0,pCameraZ));

        submitButton.setOnAction(new submitButtonHandler());

        VBox root = new VBox(borderPane);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root);

        scene.getStylesheets().add("mystyles.css");
        primaryStage.setTitle("3D Shapes");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void boxInput(){
        Label BDepthLabel = new Label("Depth");
        Label BWidthLabel = new Label("Width");
        Label BHeightLabel = new Label("Height");

        Label boxLabel = new Label("Enter box dimensions:");

        GridPane gridPaneInner = new GridPane();
        gridPaneInner.setVgap(10);
        gridPaneInner.setHgap(10);

        gridPaneInner.addRow(0, BDepthLabel, BDepthField );
        gridPaneInner.addRow(1, BWidthLabel, BWidthField );
        gridPaneInner.addRow(2, BHeightLabel, BHeightField );
        gridPaneInner.addRow(3, newXLabel, BXField );
        gridPaneInner.addRow(4, newYLabel, BYField );

        VBox boxInputPage = new VBox(10, boxLabel, gridPaneInner);
        boxInputPage.setPadding(new Insets(20));
        addShapePane.setCenter(boxInputPage);
        addShapePane.setBottom(submit);
    }

    public void sphereInput(){
        Label SRadiusLabel = new Label("Radius");

        Label sphereLabel = new Label("Enter sphere dimensions:");

        GridPane gridPaneInner = new GridPane();
        gridPaneInner.setVgap(10);
        gridPaneInner.setHgap(10);

        gridPaneInner.addRow(0, SRadiusLabel, SRadiusField );
        gridPaneInner.addRow(1, newXLabel, SXField );
        gridPaneInner.addRow(2, newYLabel, SYField );
        gridPaneInner.addRow(3, new Label("") );
        gridPaneInner.addRow(4, new Label("") );


        VBox sphereInputPage = new VBox(10, sphereLabel, gridPaneInner);
        sphereInputPage.setPadding(new Insets(20));
        addShapePane.setCenter(sphereInputPage);
        addShapePane.setBottom(submit);


    }

    public void cylinderInput(){
        Label CRadiusLabel = new Label("Radius");
        Label CHeightLabel = new Label("Height");

        Label sphereLabel = new Label("Enter cylinder dimensions:");

        GridPane gridPaneInner = new GridPane();
        gridPaneInner.setVgap(10);
        gridPaneInner.setHgap(10);

        gridPaneInner.addRow(0, CRadiusLabel, CRadiusField );
        gridPaneInner.addRow(1, CHeightLabel, CHeightField );
        gridPaneInner.addRow(2, newXLabel, CXField );
        gridPaneInner.addRow(3, newYLabel, CYField );
        gridPaneInner.addRow(4, new Label("") );



        VBox cylinderInputPage = new VBox(10,sphereLabel, gridPaneInner);
        cylinderInputPage.setPadding(new Insets(20));
        addShapePane.setCenter(cylinderInputPage);
        addShapePane.setBottom(submit);
    }


    private class ShapeTypeListener implements ChangeListener<String>
    {
        @Override
        public void changed(ObservableValue<? extends String> source, String oldValue, String newValue)
        {
            String selected = shapeTypeList.getSelectionModel().getSelectedItem();
            if (selected.equals("Box")){
                boxInput();
            }
            if (selected.equals("Sphere")){
                sphereInput();
            }
            if (selected.equals("Cylinder")){
                cylinderInput();
            }

        }
    }

    private class TextFieldListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> source, String oldValue, String newValue)
        {

            String selected = shapeTypeList.getSelectionModel().getSelectedItem();

            if (selected.equals("Box"))
            {
                try
                {
                    if (!BDepthField.getText().equals(""))
                    {
                        BDepth = Double.parseDouble(BDepthField.getText());
                    }
                    BDepthValid = true;
                } catch (Exception e) {
                    BDepthValid = false;
                }

                try
                {
                    if (!BWidthField.getText().equals(""))
                    {
                        BWidth = Integer.parseInt(BWidthField.getText());
                    }
                    BWidthValid = true;
                } catch (Exception e) {
                    BWidthValid = false;
                }

                try
                {
                    if (!BHeightField.getText().equals(""))
                    {
                        BHeight = Integer.parseInt(BHeightField.getText());
                    }
                    BHeightValid = true;
                } catch (Exception e) {
                    BHeightValid = false;
                }

                try
                {
                    if (!BXField.getText().equals("")) {
                        BX = Integer.parseInt(BXField.getText());
                    }
                    BXValid = true;
                } catch (Exception e) {
                    BXValid = false;
                }
                //getLayoutCrossX
                try
                {
                    if (!BYField.getText().equals("")) {
                        BY = Integer.parseInt(BYField.getText());
                    }
                    BYValid = true;
                } catch (Exception e) {
                    BYValid = false;
                }

                if (!BDepthValid|| !BWidthValid || !BHeightValid || !BXValid || !BYValid ||
                        BDepthField.getText().equals("") || BWidthField.getText().equals("") || BHeightField.getText().equals("") ||
                        BXField.getText().equals("") || BYField.getText().equals("")){
                    submitButton.setDisable(true);}
                else
                    submitButton.setDisable(false);
            }

            if (selected.equals("Sphere"))
            {
                try {
                    if (!SRadiusField.getText().equals("")) {
                        SRadius = Integer.parseInt(SRadiusField.getText());
                    }
                    SRadiusValid = true;
                } catch (Exception e) {
                    SRadiusValid = false;
                }

                try
                {
                    if (!SXField.getText().equals("")) {
                        SX = Integer.parseInt(SXField.getText());
                    }
                    SXValid = true;
                } catch (Exception e) {
                    SXValid = false;
                }
                try
                {
                    if (!SYField.getText().equals("")) {
                        SY = Integer.parseInt(SYField.getText());
                    }
                    SYValid = true;
                } catch (Exception e) {
                    SYValid = false;
                }

                if (!SRadiusValid || !SXValid || !SYValid ||
                        SRadiusField.getText().equals("") || SXField.getText().equals("") || SYField.getText().equals("")){
                    submitButton.setDisable(true);}
                else
                    submitButton.setDisable(false);
            }

            if (selected.equals("Cylinder"))
            {
                try
                {
                    if (!CHeightField.getText().equals("")) {
                        CHeight = Integer.parseInt(CHeightField.getText());
                    }
                    CHeightValid = true;
                } catch (Exception e) {
                    CHeightValid = false;
                }

                try
                {
                    if (!CRadiusField.getText().equals("")) {
                        CRadius = Integer.parseInt(CRadiusField.getText());
                    }
                    CRadiusValid = true;
                } catch (Exception e) {
                    CRadiusValid = false;
                }

                try
                {
                    if (!CXField.getText().equals("")) {
                        CX = Integer.parseInt(CXField.getText());
                    }
                    CXValid = true;
                } catch (Exception e) {
                    CXValid = false;
                }
                try
                {
                    if (!CYField.getText().equals("")) {
                        CY = Integer.parseInt(CYField.getText());
                    }
                    CYValid = true;
                } catch (Exception e) {
                    CYValid = false;
                }


                if (!CRadiusValid || !CHeightValid ||
                        CRadiusField.getText().equals("") || CHeightField.getText().equals("") ||
                        !CXValid || !CYValid ||
                        CXField.getText().equals("") || CYField.getText().equals(""))
                {
                    submitButton.setDisable(true);
                }
                else
                    submitButton.setDisable(false);
            }


        }
    }


    private class submitButtonHandler implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            String selected = shapeTypeList.getSelectionModel().getSelectedItem();
            if (selected.equals("Box") || shapename.equals("Box"))
            {
                Box box = new Box(BWidth,BHeight,BDepth);

                // box.getTransforms().addAll(new Translate(BX,BY,0));
                box.setTranslateX(BX);
                box.setTranslateY(BY);

                box.setMaterial(new PhongMaterial(Color.BLUE));
                box.setOnMouseClicked(sphereEvent -> {
                    selectedShape = box;

                    for (Shape3D index: mainShapeList){
                        PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKRED)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.RED));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.YELLOW));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.BLUE));
                        }
                    }
                    box.setMaterial(new PhongMaterial(Color.DARKBLUE));

                });
                mainShapeList.add(box);
                shapesGroup.getChildren().add(box);

                //box.getTransforms().add(new Translate(X,Y,Z));

            }
            if (selected.equals("Sphere") || shapename.equals("Sphere"))
            {
                Sphere sphere = new Sphere(SRadius);

                sphere.setTranslateX(SX);
                sphere.setTranslateY(SY);
                //sphere.getTransforms().addAll(new Translate(SX,SY,0));

                sphere.setMaterial(new PhongMaterial(Color.RED));
                sphere.setOnMouseClicked(sphereEvent -> {
                    selectedShape = sphere;

                    for (Shape3D index: mainShapeList){
                        PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKRED)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.RED));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.YELLOW));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.BLUE));
                        }
                    }
                    sphere.setMaterial(new PhongMaterial(Color.DARKRED));

                });
                mainShapeList.add(sphere);
                shapesGroup.getChildren().add(sphere);

            }
            if (selected.equals("Cylinder") || shapename.equals("Cylinder"))
            {
                Cylinder cylinder = new Cylinder(CRadius,CHeight);

                cylinder.setTranslateX(CX);
                cylinder.setTranslateY(CY);

                cylinder.setMaterial(new PhongMaterial(Color.YELLOW));
                cylinder.setOnMouseClicked(cylinderEvent -> {
                    selectedShape = cylinder;

                    for (Shape3D index: mainShapeList){
                        PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKRED)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.RED));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.YELLOW));
                        }
                        if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)){//getDiffuseColor
                            index.setMaterial(new PhongMaterial(Color.BLUE));
                        }
                    }
                    cylinder.setMaterial(new PhongMaterial(Color.GOLDENROD));

                });
                mainShapeList.add(cylinder);
                shapesGroup.getChildren().add(cylinder);
            }
            borderPane.setCenter(sceneAndButton);
        }
    }

    private void resetFields(){
        BDepthField.setText("");
        BWidthField.setText("");
        BHeightField.setText("");
        SRadiusField.setText("");
        CHeightField.setText("");
        CRadiusField.setText("");
        BXField.setText("");
        BYField.setText("");
        SXField.setText("");
        SYField.setText("");
        CXField.setText("");
        CYField.setText("");
    }



    private void createControls()
    {
        Label colorLabel = new Label("Choose background color:");
        colorChoice = new ChoiceBox<>();
        colorChoice.getItems().addAll("Blue", "Pink", "Green");
        colorChoice.getSelectionModel().selectedItemProperty().addListener((s,o,n) ->{
            String selectedColor = colorChoice.getSelectionModel().getSelectedItem();
            switch(selectedColor){
                case "Blue":
                    subScene.setFill(Color.SKYBLUE);
                    break;
                case "Pink":
                    subScene.setFill(Color.MISTYROSE);
                    break;
                case "Green":
                    subScene.setFill(Color.LIGHTGREEN);
                    break;
                default:
                    subScene.setFill(Color.LIGHTGRAY);
            }
        });
        Slider scaleSlider = new Slider(0.1, 5.0, 1.0);
        scaleSlider.setShowTickMarks(true);
        scaleSlider.setShowTickLabels(false);
        Label scaleTickMarks = new Label("0.1                                                     5");
        scaleTickMarks.setStyle("-fx-font-size: 8pt");
        scaleSlider.setPrefWidth(100.0);
        scaleSlider.valueProperty().addListener(new scaleListener());

        rotationAdd = new Button("+");
        rotationSub = new Button("-");

        rotationAdd.setOnAction(new rotationAdd());
        rotationSub.setOnAction(new rotationSub());

        Label rotationAmountLabel = new Label("Degrees: ");
        rotationAmount.setPrefWidth(50);

        Label rotationAxisLabel = new Label("Axis of Rotation: ");
        chooseRotationAxis = new ChoiceBox<>();
        chooseRotationAxis.getItems().addAll("X", "Y", "Z");
        chooseRotationAxis.getSelectionModel().select("X");
        chooseRotationAxis.addEventHandler(ActionEvent.ACTION, new AxisChoiceBoxListener());

        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(new zoomInEvent());

        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(new zoomOutEvent());

        Label xTranslateLabel = new Label("X: ");
        Label yTranslateLabel = new Label("Y: ");
        Label rotateLabel = new Label("Rotation");
        Label scaleLabel = new Label("Scale");

        xTranslateTextField = new TextField();
        xTranslateTextField.setPrefWidth(50);
        yTranslateTextField = new TextField();
        yTranslateTextField.setPrefWidth(50);

        translateSubmit.setOnAction(new translateSubmitEvent());
        deleteButton.setOnAction(event -> {
            mainShapeList.remove(selectedShape);
            shapesGroup.getChildren().remove(selectedShape);
            selectedShape = null;
        });

        HBox translateHBox = new HBox(5, xTranslateLabel, xTranslateTextField, yTranslateLabel,
                yTranslateTextField);
        VBox translateVBox = new VBox(5, translateHBox, translateSubmit);

        HBox zoomHBox = new HBox(5, zoomInButton, zoomOutButton);

        VBox scaleVBox = new VBox(scaleLabel, scaleSlider, scaleTickMarks);

        VBox colorVBox = new VBox(5, colorLabel, colorChoice);
        colorVBox.setAlignment(Pos.CENTER);
        HBox chooseRotationAxisHBox = new HBox(5, rotationAxisLabel, chooseRotationAxis);
        HBox rotationAmountHBox = new HBox(5, rotationAmountLabel, rotationAmount);
        HBox rotationButtons = new HBox(5, rotationAdd, rotationSub);
        VBox rotateVBox = new VBox(5, chooseRotationAxisHBox, rotationAmountHBox, rotationButtons);

        VBox deleteVBox = new VBox(5, deleteButton);
        deleteVBox.setAlignment(Pos.CENTER);
        VBox allControls = new VBox(20, colorVBox,scaleVBox, rotateVBox, zoomHBox, translateVBox, deleteVBox);

        translateHBox.setAlignment(Pos.CENTER);
        translateVBox.setAlignment(Pos.CENTER);
        zoomHBox.setAlignment(Pos.CENTER);
        zoomHBox.setPadding(new Insets(10));
        scaleVBox.setAlignment(Pos.CENTER);
        chooseRotationAxisHBox.setAlignment(Pos.CENTER);
        rotationAmountHBox.setAlignment(Pos.CENTER);
        rotationButtons.setAlignment(Pos.CENTER);
        rotateVBox.setAlignment(Pos.CENTER);
        allControls.setAlignment(Pos.CENTER);

        allControls.setPadding(new Insets(25));
        borderPane.setRight(allControls);
    }


    private class translateSubmitEvent implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            try
            {
                selectedShape.setTranslateX(Double.parseDouble(xTranslateTextField.getText()));
                selectedShape.setTranslateY(Double.parseDouble(yTranslateTextField.getText()));
            }
            catch(IllegalArgumentException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please enter in the format: 3, 12.5, etc.");
                alert.show();
            }
            catch(NullPointerException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please select a shape");
                alert.show();
            }
        }
    }

    private class scaleListener implements ChangeListener<Number>
    {
        @Override
        public void changed(ObservableValue<? extends Number> source, Number oldValue, Number newValue)
        {
            double scaler = newValue.doubleValue();
            try
            {
                selectedShape.setScaleX(scaler);
                selectedShape.setScaleY(scaler);
                selectedShape.setScaleZ(scaler);
            }
            catch(Exception e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please select a shape");
                alert.show();
            }
        }
    }

    private class AxisChoiceBoxListener implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            rotationAxis = chooseRotationAxis.getSelectionModel().getSelectedItem();
            switch (rotationAxis)
            {
                case ("X"): {
                    axisOfRotation = Rotate.X_AXIS;
                    break;
                }
                case ("Y"): {
                    axisOfRotation = Rotate.Y_AXIS;
                    break;
                }
                case ("Z"): {
                    axisOfRotation = Rotate.Z_AXIS;
                    break;
                }
                default: {
                    axisOfRotation = Rotate.X_AXIS;
                }
            }
        }
    }


    private class rotationAdd implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            try
            {
                selectedShape.getTransforms().add(new Rotate(Double.parseDouble(
                        rotationAmount.getText()), axisOfRotation));
            }
            catch(NullPointerException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please select a shape");
                alert.show();
            }
            catch(IllegalArgumentException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please enter a valid numeric input");
                alert.show();
            }
        }
    }

    private class rotationSub implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            try
            {
                selectedShape.getTransforms().add(new Rotate(-Double.parseDouble(
                        rotationAmount.getText()), axisOfRotation));
            }
            catch(NullPointerException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please select a shape");
                alert.show();
            }
            catch(IllegalArgumentException e)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Please enter a valid numeric input");
                alert.show();
            }
        }
    }

    private class zoomInEvent implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            pCameraZ += 5;
            pCamera.getTransforms().clear();
            pCamera.getTransforms().add(new Translate(0, 0, pCameraZ));
        }
    }

    private class zoomOutEvent implements EventHandler<ActionEvent>
    {
        @Override
        public void handle(ActionEvent event)
        {
            pCameraZ -= 5;
            pCamera.getTransforms().clear();
            pCamera.getTransforms().add(new Translate(0, 0, pCameraZ));
        }
    }

    private void menuHandler(Stage primaryStage){
        exitItem.setOnAction(event ->{
            primaryStage.close();
        });
        saveItem.setOnAction(event ->{
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            curFilePath = fc.showSaveDialog(primaryStage);
            try
            {
                PrintWriter printwriter = new PrintWriter(curFilePath);
                if (subScene.getFill().equals(Color.LIGHTGREEN))
                    printwriter.println("Green");
                else if(subScene.getFill().equals(Color.MISTYROSE))
                    printwriter.println("Pink");
                else if(subScene.getFill().equals(Color.SKYBLUE))
                    printwriter.println("Blue");
                else if(subScene.getFill().equals(Color.LIGHTGRAY))
                    printwriter.println("Gray");
                printwriter.close();
            }
            catch(FileNotFoundException e)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error Creating File");
                alert.show();
            }
            //saveFile(shapeTypeList.getSelectionModel().getSelectedItem());

            for (Shape3D index: mainShapeList){
                PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                if(phongMaterial.getDiffuseColor().equals(Color.DARKRED) || phongMaterial.getDiffuseColor().equals(Color.RED)){//Sphere
                    index = (Sphere)index;
                    saveSphereFile("Sphere", ((Sphere) index).getRadius(), index.getTranslateX(), index.getTranslateY(), index.getScaleX(), index.getScaleY(), index.getScaleZ(), index.getRotate());
                }
                if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD) || phongMaterial.getDiffuseColor().equals(Color.YELLOW)){//Cylinder
                    index = (Cylinder)index;
                    saveCylinderFile("Cylinder", ((Cylinder) index).getRadius(), ((Cylinder) index).getHeight(), index.getTranslateX(), index.getTranslateY(), index.getScaleX(), index.getScaleY(), index.getScaleZ(), index.getRotate());

                }
                if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE) || phongMaterial.getDiffuseColor().equals(Color.BLUE)){//Box
                    index = (Box)index;
                    saveBoxFile("Box", ((Box) index).getHeight(), ((Box) index).getWidth(), ((Box) index).getDepth(), ((Box)index).getTranslateX(), ((Box)index).getTranslateY(), ((Box)index).getScaleX(), ((Box)index).getScaleY(), ((Box)index).getScaleZ(), index.getRotate());
                }

            }
        });

        importItem.setOnAction(event -> {
            borderPane.setCenter(sceneAndButton);

            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            curFilePath = fc.showOpenDialog(primaryStage);
            try
            {
                Scanner inputFile = new Scanner(curFilePath);
                if (!curFilePath.equals(null)){
                    mainShapeList.clear();
                    shapesGroup.getChildren().clear();
                    selectedShape = null;
                }

                String sceneColor = inputFile.nextLine();
                sceneColor.trim();
                switch(sceneColor){
                    case "Blue":
                        colorChoice.getSelectionModel().select("Blue");
                        break;
                    case "Pink":
                        colorChoice.getSelectionModel().select("Pink");
                        break;
                    case "Green":
                        colorChoice.getSelectionModel().select("Green");
                        break;
                    default:
                        subScene.setFill(Color.LIGHTGRAY);
                }
                while(inputFile.hasNextLine())
                {

                    shapename = inputFile.nextLine();
                    shapename.trim();
                    if(shapename.equals("Box"))
                    {
                        shapeTypeList.getSelectionModel().select(0);
                        shapeheight = inputFile.nextDouble();
                        shapewidth = inputFile.nextDouble();
                        shapedepth = inputFile.nextDouble();
                        shapex = inputFile.nextDouble();
                        shapey = inputFile.nextDouble();
                        shapescalex = inputFile.nextDouble();
                        shapescaley = inputFile.nextDouble();
                        shapescalez = inputFile.nextDouble();
                        shaperotate = inputFile.nextDouble();

                        BHeightField.setText("" + shapeheight);
                        BWidthField.setText("" + shapewidth);
                        BDepthField.setText("" + shapedepth);
                        BXField.setText("" + shapex);
                        BYField.setText("" + shapey);
//                        submitButton.setDisable(false);
//                        submitButton.fire();
//                        submitButton.setDisable(true);
                        Box box = new Box(shapewidth,shapeheight,shapedepth);

                        // box.getTransforms().addAll(new Translate(BX,BY,0));
                        box.setTranslateX(shapex);
                        box.setTranslateY(shapey);
                        box.getTransforms().add(new Scale(shapescalex, shapescaley, shapescalez));

                        box.setMaterial(new PhongMaterial(Color.BLUE));
                        box.setOnMouseClicked(sphereEvent -> {
                            selectedShape = box;

                            for (Shape3D index: mainShapeList){
                                PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                                if(phongMaterial.getDiffuseColor().equals(Color.DARKRED)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.RED));
                                }
                                if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.YELLOW));
                                }
                                if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.BLUE));
                                }
                            }
                            box.setMaterial(new PhongMaterial(Color.DARKBLUE));

                        });
                        mainShapeList.add(box);
                        shapesGroup.getChildren().add(box);
                    }else
                    if(shapename.equals("Sphere"))
                    {
                        shapeTypeList.getSelectionModel().select(1);
                        shaperadius = inputFile.nextDouble();
                        shapex = inputFile.nextDouble();
                        shapey = inputFile.nextDouble();
                        shapescalex = inputFile.nextDouble();
                        shapescaley = inputFile.nextDouble();
                        shapescalez = inputFile.nextDouble();
                        shaperotate = inputFile.nextDouble();

                        SRadiusField.setText("" + shaperadius);
                        SXField.setText("" + shapex);
                        SYField.setText("" + shapey);
                        Sphere sphere = new Sphere(shaperadius);

                        sphere.setTranslateX(shapex);
                        sphere.setTranslateY(shapey);
                        sphere.getTransforms().add(new Scale(shapescalex, shapescaley, shapescalez));
                        //sphere.getTransforms().addAll(new Translate(SX,SY,0));

                        sphere.setMaterial(new PhongMaterial(Color.RED));
                        sphere.setOnMouseClicked(sphereEvent -> {
                            selectedShape = sphere;

                            for (Shape3D index : mainShapeList) {
                                PhongMaterial phongMaterial = (PhongMaterial) index.getMaterial();
                                if (phongMaterial.getDiffuseColor().equals(Color.DARKRED)) {//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.RED));
                                }
                                if (phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)) {//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.YELLOW));
                                }
                                if (phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)) {//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.BLUE));
                                }
                            }
                            sphere.setMaterial(new PhongMaterial(Color.DARKRED));

                        });
                        mainShapeList.add(sphere);
                        shapesGroup.getChildren().add(sphere);
                    } else
                    if(shapename.equals("Cylinder"))
                    {
                        shapeTypeList.getSelectionModel().select(2);
                        shaperadius = inputFile.nextDouble();
                        shapeheight = inputFile.nextDouble();
                        shapex = inputFile.nextDouble();
                        shapey = inputFile.nextDouble();
                        shapescalex = inputFile.nextDouble();
                        shapescaley = inputFile.nextDouble();
                        shapescalez = inputFile.nextDouble();
                        shaperotate = inputFile.nextDouble();
                        CRadiusField.setText("" + shaperadius);
                        CHeightField.setText("" + shapeheight);
                        CXField.setText("" + shapex);
                        CYField.setText("" + shapey);
                        Cylinder cylinder = new Cylinder(shaperadius, shapeheight);
                        cylinder.getTransforms().addAll(new Translate(shapex,shapey,0));

                        cylinder.setTranslateX(CX);
                        cylinder.setTranslateY(CY);
                        cylinder.getTransforms().add(new Scale(shapescalex, shapescaley, shapescalez));

                        cylinder.setMaterial(new PhongMaterial(Color.YELLOW));
                        cylinder.setOnMouseClicked(cylinderEvent -> {
                            selectedShape = cylinder;

                            for (Shape3D index: mainShapeList){
                                PhongMaterial phongMaterial = (PhongMaterial)index.getMaterial();
                                if(phongMaterial.getDiffuseColor().equals(Color.DARKRED)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.RED));
                                }
                                if(phongMaterial.getDiffuseColor().equals(Color.GOLDENROD)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.YELLOW));
                                }
                                if(phongMaterial.getDiffuseColor().equals(Color.DARKBLUE)){//getDiffuseColor
                                    index.setMaterial(new PhongMaterial(Color.BLUE));
                                }
                            }
                            cylinder.setMaterial(new PhongMaterial(Color.GOLDENROD));

                        });
                        mainShapeList.add(cylinder);
                        shapesGroup.getChildren().add(cylinder);
                    }

                }
            }
            catch (Exception e)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
            }
        });
    }

    private void saveCylinderFile(String shape, double radius, double height, double x, double y, double scalex, double scaley, double scalez, double angle)
    {
        try
        {
            FileWriter filewrite = new FileWriter(curFilePath.getPath(), true);
            PrintWriter outputfile = new PrintWriter(filewrite);
            outputfile.println(shape);
            outputfile.println(radius);
            outputfile.println(height);
            outputfile.println(x);
            outputfile.println(y);
            outputfile.println(scalex);
            outputfile.println(scaley);
            outputfile.println(scalez);
            outputfile.println(angle);
            outputfile.close();
            //shapeTypeList.getItems().add(shape);
        }
        catch(Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Error Saving Files!");
            alert.show();
        }
    }
    private void saveBoxFile(String shape, double height, double width, double depth, double x, double y, double scalex, double scaley, double scalez, double angle)
    {
        try
        {
            FileWriter filewrite = new FileWriter(curFilePath.getPath(), true);
            PrintWriter outputfile = new PrintWriter(filewrite);
            outputfile.println(shape);
            outputfile.println(height);
            outputfile.println(width);
            outputfile.println(depth);
            outputfile.println(x);
            outputfile.println(y);
            outputfile.println(scalex);
            outputfile.println(scaley);
            outputfile.println(scalez);
            outputfile.println(angle);

            outputfile.close();
            //shapeTypeList.getItems().add(shape);
        }
        catch(Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Error Saving Files!");
            alert.show();
        }
    }
    private void saveSphereFile(String shape, double radius, double x, double y, double scalex, double scaley , double scalez, double angle)
    {
        try
        {
            FileWriter filewrite = new FileWriter(curFilePath.getPath(), true);
            PrintWriter outputfile = new PrintWriter(filewrite);
            outputfile.println(shape);
            outputfile.println(radius);
            outputfile.println(x);
            outputfile.println(y);
            outputfile.println(scalex);
            outputfile.println(scaley);
            outputfile.println(scalez);
            outputfile.println(angle);


            outputfile.close();
            //shapeTypeList.getItems().add(shape);
        }
        catch(Exception e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Error Saving Files!");
            alert.show();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}