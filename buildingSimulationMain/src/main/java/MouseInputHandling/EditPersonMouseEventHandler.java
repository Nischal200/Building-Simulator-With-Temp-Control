package MouseInputHandling;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import building.Building_GUI;
import building.Person_Manager;


import java.awt.Point;

/**

 lol this is quite
 */
public class EditPersonMouseEventHandler implements EventHandler<MouseEvent> {
    // the colour to be used when editing a person.
    private Color personColor;

    // GUI object.
    private Building_GUI buildingGUI;


    public EditPersonMouseEventHandler(Color personColor, Building_GUI buildingGUI){
        this.personColor = personColor;
        this.buildingGUI = buildingGUI;
    }

    /**
     * Handle mouse events.
     * @param event mouse event.
     */
    @Override
    public void handle(MouseEvent event) {
        // redraw building.
        buildingGUI.drawBuilding();

        // get the x and y co-ordinates of the mouse.
        int x = (int) event.getX();
        int y = (int) event.getY();

        // create a point from the co-ordinates.
        Point currentPosition = new Point(x, y);

        // create a pseudo-person at the specified point. This person will be used to add or remove a real person.
        Person person = new Person(currentPosition, personColor);
        if (event.getClickCount() > 0){
            // the person clicked in an area in the room.
            if(buildingGUI.getSimulationMode() == Person_Manager.ADD_PERSON){
                // add the person since the program is in ADD_PERSON mode.
                buildingGUI.addPerson(person);

                // now allow the thermometer to pick temperatures from the people within.
                buildingGUI.getThermometer().setShouldUpdate(true);
            } else if( buildingGUI.getSimulationMode() == Person_Manager.DELETE_PERSON){
                // delete the person since the program is in DELETE_PERSON mode.
                buildingGUI.deletePerson(currentPosition);

                // get the current temp rise.
                int currentTempRise = buildingGUI.getTheBuilding().getOccupants().size() * buildingGUI.getThermometer().getTempRisePerPerson();
                if(((currentTempRise + 10) < 20)){
                    // if the temp rise is less than 20, then the thermometer should resume updates
                    // from occupants.
                    buildingGUI.getThermometer().setShouldUpdate(true);
                }
            }

            // reset the canvas listeners.
            buildingGUI.resetCanvasListeners();
            buildingGUI.setSimulationMode(Person_Manager.NORMAL);
        } else {
            // just draw the pseudo-person if the mouse hasn't been clicked.
            person.draw(buildingGUI);
        }

    }



}
