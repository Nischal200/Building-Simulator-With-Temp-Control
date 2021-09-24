package MouseInputHandling;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import building.Building_GUI;
import building.Room;
import building.Person;

import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Event handler to listen for mouse events when a room is either being added or removed.
 */
public class EditRoomMouseEventHandler implements EventHandler<MouseEvent> {
    // GUI object
    private Building_GUI buildingGUI;

    /**
     * Constructor.
     *
     * @param buildingGUI GUI object
     */
    public EditRoomMouseEventHandler(Building_GUI buildingGUI){
        this.buildingGUI = buildingGUI;
    }

    /**
     *  Handle mouse events.
     * @param event mouse event
     */
    @Override
    public void handle(MouseEvent event) {
        // get the current mouse co-ordinates.
        int x = (int) event.getX();
        int y = (int) event.getY();

        // find any room that has these co-ordinates
        Optional<Room> roomOptional = buildingGUI.getTheBuilding()
                .getAllRooms()
                .stream()
                .filter(it -> it.isInRoom(new Point(x, y)))
                .findAny();


        if(roomOptional.isPresent() && event.getClickCount() > 0){
            // the room was found, and the person clicked on the room.
            Room room = roomOptional.get();
            buildingGUI.getTheBuilding().getAllRooms().remove(roomOptional.get());

            // get the list of people in the room to be removed.
            List<Person> personList = buildingGUI.getTheBuilding()
                    .getOccupants()
                    .stream()
                    .filter(person -> room.isInRoom(person.getXY()))
                    .collect(Collectors.toList());

            // move these people to the next available room (if there is one),
            // otherwise delete them as well
            if(buildingGUI.getTheBuilding().getAllRooms().isEmpty()){
                buildingGUI.getTheBuilding().getOccupants().clear();
            } else {
               personList.forEach(person -> buildingGUI.getTheBuilding().setNewRoom(person));
            }

            // redraw the building to reflect any changes.
            buildingGUI.drawBuilding();

            // reset the building canvas listeners to default behaviour.
            buildingGUI.resetCanvasListeners();
        } else if(roomOptional.isPresent() && event.getClickCount() == 0){
            // the co-ordinates point to a room but the user is only hovering over the room.
            buildingGUI.drawBuilding();

            // draw the building in RED in order to outline it.
            roomOptional.get().draw(buildingGUI, Color.RED);
        } else if(!roomOptional.isPresent() && event.getClickCount() > 0){
            // the user clicked on an area outside the building. Reset the canvas listeners and redraw the building
            // to remove any outlines.
            buildingGUI.drawBuilding();
            buildingGUI.resetCanvasListeners();
        }

    }
}