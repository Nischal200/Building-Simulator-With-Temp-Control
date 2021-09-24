package building;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import MouseInputHandling.EditRoomMouseEventHandler;
import MouseInputHandling.EditPersonMouseEventHandler;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class Building_GUI extends Application {

    private Stage primaryStage;

   
    private Building theBuilding;


    private VBox rightPane;

    private GraphicsContext gc;


    private AnimationTimer timer;

    private Label positionLabel = new Label();

    private Alert colorPickerAlert;

    private ColorPicker personColorPicker;

    private Canvas canvas;

    private Person_Manager simulationMode;

    private Thermometer thermometer;

    private Air_Con airConditioner;

    private LineChart<Number, Number> temperatureChart;

    private double time = 0;


 
    public static void showMessage(String TStr, String CStr, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(TStr);
        alert.setHeaderText(null);
        alert.setContentText(CStr);

        alert.showAndWait();
    }


    private void showWelcome() {
        showMessage("Welcome", "Welcome to building.Building Simulator!", Alert.AlertType.INFORMATION);
    }


      private LineChart<Number, Number> createChart() {
  
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (Seconds)");

        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature (Degrees Celcius)");

   
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Temperature Graph");
        chart.setPrefSize(500, 500);

  
        chart.getData().add(new XYChart.Series<>());


        chart.getData().get(0).setData(FXCollections.observableArrayList());


        chart.setCreateSymbols(false);

        return chart;
    }

  
    private void updateChart(double time, double temperature) {
        this.temperatureChart.getData().get(0).getData().add(new XYChart.Data<>(time, temperature));
    }


  
    private MenuBar getMenuBar() {
       
        MenuBar menuBar = new MenuBar();

        //Menu bars
        Menu mFile = new Menu("File");

      
        Menu mEdit = new Menu("Options");

  
        Menu mHelp = new Menu("Help");


        MenuItem mNew = new MenuItem("New");


        MenuItem mOpen = new MenuItem("Open");

        
        MenuItem mSave = new MenuItem("Save");


        MenuItem mExit = new MenuItem("Exit");


        MenuItem mAddPerson = new MenuItem("Add person");


        MenuItem mAddRoom = new MenuItem("Add room");


        MenuItem mDeletePerson = new MenuItem("Remove person");


        MenuItem mDeleteRoom = new MenuItem("Remove room");


      
        mDeleteRoom.setOnAction(event -> {
            
            EditRoomMouseEventHandler eventHandler = new EditRoomMouseEventHandler(this);

            // attach the 'EditRoomMouseEventHandler' to the canvas, so that it is invoked
          
            canvas.setOnMouseMoved(eventHandler);
            canvas.setOnMouseClicked(eventHandler);
        });


        // 'mAddRoom' is clicked.
        mAddRoom.setOnAction(event -> {
      
            Room room = getNewRoom();

            if (room != null) { 
                timer.stop();

      
                theBuilding.getAllRooms().add(room);

   
                theBuilding.update();

              
                drawBuilding();
            }
        });



        mNew.setOnAction(event -> {

   
            Dimension buildingDimensions = getBuildingDimensions();

            if (null != buildingDimensions) { 
                clearCanvas();


                theBuilding = new Building(String.format("%d %d;", buildingDimensions.width, buildingDimensions.height));

                // draw the building.
                drawBuilding();
            }
        });



        mAddPerson.setOnAction(event -> {
   
            timer.stop();
            simulationMode = Person_Manager.ADD_PERSON;
           
            Optional<ButtonType> buttonTypeOptional = this.colorPickerAlert.showAndWait();
            buttonTypeOptional.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
      
                    Color color = personColorPicker.getValue();

                    if (Color.WHITE.equals(color)) {
                        showMessage(" Error", "Sorry that colour is not permited", Alert.AlertType.ERROR);
                    } else {

                        EditPersonMouseEventHandler editPersonMouseEventHandler =
                                new EditPersonMouseEventHandler(this.personColorPicker.getValue(), this);

                        // set the 'EditPersonMouseEventHandler' event handler to react to mouse motions
                        canvas.setOnMouseMoved(editPersonMouseEventHandler);
                        canvas.setOnMouseClicked(editPersonMouseEventHandler);

                        simulationMode = Person_Manager.ADD_PERSON;
                    }
                }
            });

        });


      
        mSave.setOnAction(event -> Building_Tool_Bar.saveBuilding(this.theBuilding));


        mOpen.setOnAction(event -> {
      
            Building loadedBuilding = Building_Tool_Bar.retreiveBuilding();

            if (null != loadedBuilding) {
  
                this.theBuilding = loadedBuilding;
                drawBuilding();
            }
        });


 
        mExit.setOnAction(t -> {
 
            timer.stop();

    
            System.exit(0);
        });



        mDeletePerson.setOnAction(event -> {


            timer.stop();

 
            EditPersonMouseEventHandler editPersonMouseEventHandler = new EditPersonMouseEventHandler(Color.WHITE, this);

    
      
            canvas.setOnMouseMoved(editPersonMouseEventHandler);
            canvas.setOnMouseClicked(editPersonMouseEventHandler);

       
            simulationMode = Person_Manager.DELETE_PERSON;
        });

     
        mFile.getItems().addAll(mNew, mOpen, mSave, mExit);


        mEdit.getItems().addAll(mAddPerson, mAddRoom, mDeletePerson, mDeleteRoom);


        MenuItem mWelcome = new MenuItem("For Help Vist our website");

        mWelcome.setOnAction(actionEvent -> showWelcome());


        mHelp.getItems().addAll(mWelcome);

        // set main menu with File, Config, Run, Help
        menuBar.getMenus().addAll(mFile, mEdit, mHelp);

        // return the menu
        return menuBar;
    }


    /**
     * Draw the building outline to guide the placing of rooms.
     */
    private void drawBuildingBorders() {
        // create a room string configuration from the current building dimensions.
        String roomStr = String.format("%d %d %d %d %d %d %d", 0, 0, theBuilding.getXSize(), theBuilding.getYSize(), 0, 0, 0);

        // instantiate the room (as an empty room) using the room factory.
        Room room = Room_Manager.getRoom(roomStr, Room_Types.EMPTY);

        // draw the room to show the building outline.
        room.draw(this, Color.LIGHTYELLOW);
    }

    /**
     * @return the current building.
     */
    public Building getTheBuilding() {
        return theBuilding;
    }


    /**
     * Initialize the colour picker alert.
     */
    private void initPersonColorPicker() {
        // The starting colour of the person object is set as red
        this.personColorPicker = new ColorPicker(Color.RED);

        // create a new alert and configure it.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to add another person?");
        this.colorPickerAlert = alert;
    }

    /**
     * set up the horizontal box for the bottom with relevant buttons
     *
     * @return the horizontal box with the control buttons.
     */
    private HBox setButtons() {
        // button to display graph
        Button btnGraphAlert = new Button("View Temperature  Graph");

        // set the event handler to be invoked when it is clicked.
        btnGraphAlert.setOnAction(it -> {
            // create, configure and display an alert that will display the temperature/time graph
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setGraphic(this.temperatureChart);
            alert.getDialogPane().setPrefSize(530, 480);
            alert.initModality(Modality.NONE);
            alert.show();
        });

        // button to start timer.
        Button btnStart = new Button("Play");

        // start the timer when clicked.
        btnStart.setOnAction(event -> timer.start());


        // button to stop the timer when clicked.
        Button btnStop = new Button("Pause");
        btnStop.setOnAction(event -> timer.stop());

        // now add these buttons + labels to a HBox
        HBox hbox = new HBox(btnStart, btnStop, btnGraphAlert);
        hbox.setSpacing(30);

        return hbox;
    }

    /**
     * Set a new simulation mode of the program.
     *
     * @param simulationMode the new simulation mode.
     */
    public void setSimulationMode(Person_Manager simulationMode) {
        this.simulationMode = simulationMode;
    }


    
    
    public void showWall(double x1, double y1, double x2, double y2, Color color, int wallSize) {
        // set the stroke colour
        gc.setStroke(color);

        // set line width.
        gc.setLineWidth(wallSize);

        // now draw line.
        gc.strokeLine(x1, y1, x2, y2);
    }

    // reset the canvas TO DEFULT.
    public void resetCanvasListeners() {
        canvas.setOnMouseClicked(null);
        canvas.setOnMouseMoved(locationMouseEventHandler);
    }

    /**
     * @param person the person to be added to the building.
     */
    public void addPerson(Person person) {
        // the index for the room to the person is
        int roomIndex = theBuilding.whichRoom(person.getXY());

        if (-1 != roomIndex) { // ensure the person was placed in one the rooms in the building.
            Room room = theBuilding.getAllRooms().get(roomIndex);

            // set the point to which the person should navigate to if they start moving.
            person.setTarget(room.getByDoor(0));

            // add the person to the collection of occupants maintained by the current building.
            theBuilding.getOccupants().add(person);

            // now draw the building.
            drawBuilding();
        } else {

            // show an error message.
            showMessage("Error", "The room selected was invalid", Alert.AlertType.ERROR);
        }
    }


    /**
     * @return the building dimensions of the new building the user wishes to create.
     */
    private Dimension getBuildingDimensions() {
        // wrap the dimension to be returned in the reference continer.
        // this is to allow the dimension to be be reset.
        AtomicReference<Dimension> dimension = new AtomicReference<>();

        // create and configure UI controls to that collect the width of the building.
        TextField wField = new TextField();
        Label wLabel = new Label("Width");
        Label warningLabel1 = new Label("* Valid range is 100 - 450");
        warningLabel1.setVisible(false);
        warningLabel1.setTextFill(Color.ORANGE);


        // create and configure the UI components to that collect the length of the building.
        TextField hField = new TextField();
        Label hLabel = new Label("Length");
        Label warningLabel2 = new Label("* Valid range is 100 - 450");
        warningLabel2.setVisible(false);
        warningLabel2.setTextFill(Color.ORANGE);

        // create the horizontal  box 
        HBox wBox = new HBox(wLabel, wField, warningLabel1);
        wBox.setPadding(new Insets(0, 0, 10, 50));
        wBox.setSpacing(10);

        // create the horizontal box 
        HBox hBox = new HBox(hLabel, hField, warningLabel2);
        hBox.setPadding(new Insets(0, 0, 10, 50));
        hBox.setSpacing(5);

        // the vertical box to hold the two horizontal boxes.
        VBox parent = new VBox(wBox, hBox);

        // This listner gives a warning if invalid width is input.
        wField.setOnKeyReleased(event -> {
            if (!isValidBuildingDimension(wField.getText())) {
                warningLabel1.setVisible(true);
            } else {
                warningLabel1.setVisible(false);
            }
        });

     // This listner gives a warning if invalid HEIGHT is input.
        hField.setOnKeyReleased(event -> {
            if (!isValidBuildingDimension(hField.getText())) {
                warningLabel2.setVisible(true);
            } else {
                warningLabel2.setVisible(false);
            }
        });

        // create and configure an alert to be displayed to
        // collect the width and height.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(false);
        alert.getDialogPane().setPrefWidth(450);
        alert.setHeaderText(null);
        alert.setGraphic(parent);

        // show the alert.
        Optional<ButtonType> btnOptional = alert.showAndWait();


        btnOptional.ifPresent(btnType -> { // did the user press any of the alert buttons?

            // get the width and heights, and remove any trailing white spaces.
            String width = wField.getText().trim();
            String height = hField.getText().trim();


            if (btnType == ButtonType.OK && isValidBuildingDimension(width)
                    && isValidBuildingDimension(width)) { // was the button pressed that of OK and the values valid?

                // set a new dimension on the atomic reference.
                dimension.set(new Dimension(Integer.parseInt(width), Integer.parseInt(height)));
            }
        });

        // get the dimension from the atomic reference. If it wasn't set in the first place, return null.
        return dimension.get();
    }


    /**
     * @param buildingDimens the string representation of the building dimension. (height or width).
     * @return whether the dimension supplied is valid.
     */
    private boolean isValidBuildingDimension(String buildingDimens) {
        if (buildingDimens.isEmpty()) { // is the building empty?
            return false;
        } else {
            // check if the supplied value is in range.
            int intDimens = Integer.parseInt(buildingDimens);
            return intDimens >= 100 && intDimens <= 450;
        }
    }


    /**
     * Draw a person of given size in the interface at position x,y
     * Do so by drawing a circle in the colour.
     *
     * @param x           the X co-ord of the position of the person.
     * @param y           the Y co-ord of the position of the person.
     * @param size        the size of the person.
     * @param personColor person colour.
     */
    public void drawPerson(int x, int y, int size, Color personColor) {
        drawCircle(x, y, size, personColor, false);
    }

    /**
     * Draw a person of given size in the interface at position x,y
     * Do so by drawing a circle in the colour.
     *
     * @param x        the X co-ord of the position of the person.
     * @param y        the Y co-ord of the position of the person.
     * @param size     size the size of the person.
     * @param color    personColor person colour.
     * @param anywhere whether to draw it within the building rooms or anywhere on the canvas.
     */
    public void drawCircle(int x, int y, int size, Color color, boolean anywhere) {
        if (!anywhere) {
            if (x < this.theBuilding.getXSize() - size && y < theBuilding.getYSize() - size) {
                // set the fill colour
                gc.setFill(color);

                // fill 360 degree arc
                gc.fillArc(x - size, y - size, size * 2, size * 2, 0, 360, ArcType.ROUND);
            }
        } else {
            // set the fill colour
            gc.setFill(color);

            // fill 360 degree arc
            gc.fillArc(x - size, y - size, size * 2, size * 2, 0, 360, ArcType.ROUND);
        }
    }

    /**
     * Delete the person from the specified position.
     *
     * @param currentPosition the position of the person to be deleted.
     */
    public void deletePerson(Point currentPosition) {
        // the person to be returned.
        Person targetPerson = null;

        // look for the person with the specified co-ordinates.
        for (Person p : theBuilding.getOccupants()) {
            if (currentPosition.equals(p.getXY())) {
                // the person at the specified co-ordinates has been found.
                targetPerson = p;
                break;
            }
        }

        if (null != targetPerson) { // ensure a person has been found before removing the person.
            // remove the person from the collection of people.
            theBuilding.getOccupants().remove(targetPerson);

            // re-draw the building to reflect the removed person.
            drawBuilding();
        } else {
            showMessage("Location Error", "No person found at that point. Please try again.", Alert.AlertType.ERROR);
        }
    }


    /**
     * @return the current simulation mode.
     */
    public Person_Manager getSimulationMode() {
        return simulationMode;
    }

    /**
     * Clears the canvas and readies it to have a new building drawn.
     */
    Image Fan = new Image ( getClass().getResourceAsStream("deskfan.gif"));
    Image TV = new Image ( getClass().getResourceAsStream("TV.png"));
    Image Bed = new Image ( getClass().getResourceAsStream("Bedicon.png"));
    Image Garden = new Image ( getClass().getResourceAsStream("FlowerIcon.png"));
    private void clearCanvas() {
        // use the BEIGE colour as background.
        gc.setFill(Color.WHITE);

        // clear the canvas
        gc.fillRect(0, 0, 500, 500);
       
    	// adds the gif
    	 ;
    	 //images of objects
    	        gc.drawImage(Fan, 200, 80, 120,120);
    	        gc.drawImage(TV, 360, 80, 60,60);
    	        gc.drawImage(Bed, 160, 245, 60,60);
    	        gc.drawImage(Garden, 32, 245, 60,60);
    }


    /**
     * Obtains room configuration data from a user, then uses the data to create a room.
     *
     * @return a new room.
     */
    private Room getNewRoom() {
        // the room to be returned is wrapped in an atomic reference to allow updates to
        // it inside a lambda.
        AtomicReference<Room> room = new AtomicReference<>();

        // Create and configure UI control to collect the room type.
        Label roomType = new Label("building.Room Type: ");
        ComboBox<Room_Types> roomTypeCBox = new ComboBox<>();
        FXCollections.observableArrayList(Room_Types.values());
        roomTypeCBox.setItems(FXCollections.observableArrayList(Room_Types.values()));
        roomTypeCBox.getSelectionModel().select(Room_Types.EMPTY);

        // Create and configure UI control to collect the room's top left X and Y co-ordinates.
        Label tlXLabel = new Label("Top Left Corner X: ");
        TextField tlXField = new TextField();
        Label tlYLabel = new Label("Top Left Corner Y: ");
        TextField tlYField = new TextField();

        // Create and configure UI control to collect the room's bottom right X and Y co-ordinates.
        Label brXLabel = new Label("Bottom Right Corner X: ");
        TextField brXField = new TextField();
        Label brYLabel = new Label("Bottom Right Corner Y: ");
        TextField brYField = new TextField();

        // Create and configure UI control to collect the room's door X and Y co-ordinates.
        Label doorXLabel = new Label("Door X: ");
        TextField doorXField = new TextField();
        Label doorYLabel = new Label("Door Y: ");
        TextField doorYField = new TextField();

        // Create and configure UI control to collect the room's door size.
        Label doorSizeLabel = new Label("Door Size: ");
        TextField doorSizeField = new TextField();

        // horizontal box to align the room type UI controls.
        HBox roomTypeBox = new HBox(roomType, roomTypeCBox);
        roomTypeBox.setSpacing(70);

        // horizontal box to align the room top left corner X co-ordinate UI controls
        HBox tlXBox = new HBox(tlXLabel, tlXField);
        tlXBox.setSpacing(34);

        // horizontal box to align the room top left corner Y co-ordinate UI controls
        HBox tlYBox = new HBox(tlYLabel, tlYField);
        tlYBox.setSpacing(34);

        // horizontal box to align the room bottom right corner X co-ordinate UI controls
        HBox brXBox = new HBox(brXLabel, brXField);
        brXBox.setSpacing(8);

        // horizontal box to align the room bottom right corner Y co-ordinate UI controls
        HBox brYBox = new HBox(brYLabel, brYField);
        brYBox.setSpacing(8);

        // horizontal box to align the room's door X co-ordinate UI controls
        HBox doorXBox = new HBox(doorXLabel, doorXField);
        doorXBox.setSpacing(98);

        // horizontal box to align the room's door Y co-ordinate UI controls
        HBox doorYBox = new HBox(doorYLabel, doorYField);
        doorYBox.setSpacing(98);

        // horizontal box to align the room's door size UI controls
        HBox doorSizeBox = new HBox(doorSizeLabel, doorSizeField);
        doorSizeBox.setSpacing(82);

        // create the padding to be used by the horizontal boxes.
        Insets insets = new Insets(0, 0, 10, 0);

        // group the horizontal boxes into a list
        List<HBox> hBoxes = Arrays.asList(roomTypeBox, tlXBox, tlYBox, brXBox, brYBox, doorXBox, doorYBox, doorSizeBox);

        // set the padding of each of the boxes.
        hBoxes.forEach(hbox -> hbox.setPadding(insets));

        // create a parent vertical box and add all the horizontal boxes.
        VBox parentBox = new VBox(hBoxes.toArray(new Node[0]));

        // create and configure an alert that will display the form to create a new room.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Add a room");
        alert.setGraphic(parentBox);
        alert.getDialogPane().setPrefWidth(300);

        // show the alert and await the user action.
        Optional<ButtonType> btnOptional = alert.showAndWait();

        btnOptional.ifPresent(btnType -> { // the user clicked a button.
            if (btnType == ButtonType.OK) { // the button clicked was 'Ok'

                // create a room string configuration from the values provided.
                String roomStr = String.format("%s %s %s %s %s %s %s",
                        tlXField.getText().trim(),
                        tlYField.getText().trim(),
                        brXField.getText().trim(),
                        brYField.getText().trim(),
                        doorXField.getText().trim(),
                        doorYField.getText().trim(),
                        doorSizeField.getText().trim());

                // ask the room factory to create room given the room string configuration and
                // the type of room selected. The atomic reference is then updated with this new room.
                room.set(Room_Manager.getRoom(roomStr, roomTypeCBox.getSelectionModel().getSelectedItem()));
            }
        });

        // return a room if it was created, otherwise null.
        return room.get();
    }

    /**
     * draw the arena and its contents
     */
    public void drawBuilding() {
        // clear the canvas.
        clearCanvas();

        // draw the building borders
        drawBuildingBorders();

        // get the building's string representation.
        String s = theBuilding.toString();

        // remove any children of the right pane.
        rightPane.getChildren().clear();

        // create a new label with the building string representation
        Label l = new Label(s);

        // create label for the current temperature
        Label tempLabel = new Label("Current temperature: " + (int) this.thermometer.getCurrentTemperature() + " Degrees");

        // add building, position and temperature labels to the right pane.
        rightPane.getChildren().addAll(l, this.positionLabel, tempLabel);

        // draw the thermometer
        thermometer.draw(this);

        // now draw the building.
        theBuilding.draw(this);

    }

    /**
     * @return the current building's thermometer.
     */
    public Thermometer getThermometer() {
        return thermometer;
    }



    // event handler to track and update the location of the mouse pointer in the building.
    private EventHandler<MouseEvent> locationMouseEventHandler = event -> {
        // ensure the pointer is in the building before updating the position label.
        if (event.getX() <= theBuilding.getXSize() && event.getY() <= theBuilding.getYSize()) {
            positionLabel.setText("Pointer Position " + "X: " + event.getX() + " Y: " + event.getY());
        }
    };

    /**
     * Method to draw an image given the parameters.
     *
     * @param image the image to be drawn.
     * @param x     the top left X co-ordinate.
     * @param y     the top left Y co-ordinate.
     * @param w     the width of the image.
     * @param h     the height of the image.
     */
    public void drawImage(Image image, int x, int y, int w, int h) {
        gc.drawImage(image, x, y, w, h);
    }

    /**
     * Lifecycle method called when the JavaFX app is starting.
     *
     * @param primaryStage the primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        // save the primary stage
        this.primaryStage = primaryStage;

        // display the welcome message.
        this.primaryStage.setTitle(" building.Building Simulator");

        // initialize the colour picker for a person.
        initPersonColorPicker();

        // create and configure new border pane
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20, 20, 20, 20));

        // put menu at the top
        bp.setTop(getMenuBar());

        // create group with canvas
        Group root = new Group();
        canvas = new Canvas(500, 500);

        // set the event handler to be called when the mouse is moved over the canvas.
        canvas.setOnMouseMoved(locationMouseEventHandler);

        // add canvas to the group
        root.getChildren().add(canvas);

        // load canvas to left area
        bp.setCenter(root);

        // initialize the context for drawing
        gc = canvas.getGraphicsContext2D();

        // instantiate animation timer.
        timer = new AnimationTimer() {                                            // set up timer
            public void handle(long currentNanoTime) {
                // update and redraw the building.
                theBuilding.update();
                drawBuilding();

                // update and redraw the thermometer
                thermometer.update();
                thermometer.draw(Building_GUI.this);

                // update the air conditioner.
                airConditioner.update();

                // update the time passed
                time += 1;

                // update the temperature time graph.
                updateChart(time, getThermometer().getCurrentTemperature());
            }
        };

        // set vBox on right to list items
        rightPane = new VBox();
        rightPane.setAlignment(Pos.TOP_LEFT);
        rightPane.setPadding(new Insets(5, 75, 75, 5));

        // place right pane on the right side
        bp.setRight(rightPane);

        // initialize the time-temperature chart.
        temperatureChart = createChart();

        // set bottom pane with buttons
        bp.setBottom(setButtons());

        // set overall scene
        Scene scene = new Scene(bp, 720, 720);
        bp.prefHeightProperty().bind(scene.heightProperty());
        bp.prefWidthProperty().bind(scene.widthProperty());

        // configure and show the primary stage.
        primaryStage.setScene(scene);
        primaryStage.show();

        // create a new default building.
        theBuilding = new Building();

        // create thermometer.
        thermometer = new Thermometer(theBuilding);

        // create air conditioner
        airConditioner = new Air_Con(thermometer);

        // set welcome message
        showWelcome();

        // now draw the building.
        drawBuilding();

        // now draw the thermometer
        thermometer.draw(this);
    }

    /**
     * Main method  required inorder to compile the program app.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
