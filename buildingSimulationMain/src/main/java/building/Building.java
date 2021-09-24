package building;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 
 * This class represents the over all building which has room in it
 */
public class Building extends Updatable_Input_Item  {

   
    // size of building
    private int xSize = 50;
    private int ySize = 50;

    // collection of  all rooms
    private List<Room> allRooms;

    // the collection of people in the building
    private List<Person> occupants;

    // random number generator.
    private Random ranGen;


    /**
     * Construct a building
     */
    public Building(String bs) {
        // create space for rooms that can be added
        allRooms = new ArrayList<>();

        // create object for generating random numbers
        ranGen = new Random();

        // create space for the occupants
        occupants = new ArrayList<>();

        // now set building using setbuilding(bs)
        setBuilding(bs);
    }

    /**
     * Constructor for the default building.
     */
    public Building() {
        this("450 450;90 10 320 70 220 70 10;" +// 450 to 450 is the building size and the other are the room size
                "10 180 100 380 60 180 15;100 180 320 380 200 180 20;" +
                "320 10 430 380 320 110 25;");

        // add a person in room number 0 for the default room
        addPerson(0);
    }



    public void addPerson(int roomNumber) {
        Person person = new Person(allRooms.get(roomNumber).getRandom(ranGen));
        person.setTarget(allRooms.get(roomNumber).getByDoor(0));
        this.occupants.add(person);
    }


    private void setBuilding(String bS) {
     
        allRooms.clear();

    
        occupants.clear();

    
        StringSplitter bSME = new StringSplitter(bS, ";");

    
        StringSplitter bSz = new StringSplitter(bSME.getNth(0, "10 10"), " ");

       
        xSize = bSz.getNthInt(0, 5);
        ySize = bSz.getNthInt(0, 5);

      
        for (int ct = 1; ct < bSME.numElement(); ct++){
      
            allRooms.add(new Room(bSME.getNth(ct, "")));
        }
    }


  
    public List<Room> getAllRooms() {
        return allRooms;
    }

  
    public List<Person> getOccupants() {
        return occupants;
    }

  
    public int getXSize() {
        return xSize;
    }

   
    public int getYSize() {
        return ySize;
    }


 
    public void setNewRoom(Person occupant) {
        // at this stage all this does is
        int cRoom = whichRoom(occupant.getXY());
        int dRoom = cRoom;
        while (dRoom == cRoom)
            dRoom = ranGen.nextInt(allRooms.size());    // get another room randomly

        occupant.setXY(allRooms.get(dRoom).getRandom(ranGen));
        occupant.setTarget(allRooms.get(dRoom).getByDoor(0));        // position by door

        occupant.setStopped(false);                                // say person can move
    }

  
    public int randRoom() {
        return ranGen.nextInt(allRooms.size());
    }

    /**
     * method to update the building
     *  Update the occupants first, then update the bed room
     */
    public void update() {

        // update the occupants
        occupants.forEach(occupant -> {
            if (occupant.getStopped())
                setNewRoom(occupant);
            else
                occupant.update();
        });}


    /**
     * method to determine which room position x,y is in
     *
     * @param xy
     * @return n, the number of the room or -1 if in corridor
     */
    public int whichRoom(Point xy) {
        int ans = -1;
        for (int ct = 0; ct < allRooms.size(); ct++)
            if (allRooms.get(ct).isInRoom(xy))
                ans = ct;
        return ans;
    }


    /**
     * method to return information bout the building as a string
     */
    public String toString() {
        StringBuilder s = new StringBuilder("building.Building size " + getXSize() + "," + getYSize() + "\n");
        for (Room r : allRooms)
            s.append(r.toString()).append("\n");

        for (Person p : occupants)
            s.append(p.toString()).append("\n");
        return s.toString();
    }


    @Override
    public void draw(Building_GUI buildingGUI) {
        //loop through array of all rooms, displaying each
        for (Room r : allRooms)
            r.draw(buildingGUI);

        // display any people in the building
        this.occupants.forEach(person -> person.draw(buildingGUI));
    }
}
