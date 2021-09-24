package building;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

/**
 * @author shsmchlr
 * Defines a rooom in a building
 */
public class Room extends Input_Item {
    private int x1, y1, x2, y2, xd, yd, ds;     // x1,y1 and x2,y2 are opp corners of room,
    // xd,yd are door location ds is door size
    /**
     * construct room, dummy parameters
     */

    /**
     * construct room
     * @param rStr	has xcoords of opposite corners and door as numbers separated by spaces
     */
    public Room (String rStr) {
        StringSplitter m = new StringSplitter(rStr, " ");
        x1 = m.getNthInt(0, 0);
        y1 = m.getNthInt(1, 0);
        x2 = m.getNthInt(2, 5);
        y2 = m.getNthInt(3, 5);
        xd = m.getNthInt(4, 4);
        yd = m.getNthInt(5, 2);
        ds = m.getNthInt(6, 1);
    }

    /**
     * return size of door
     * @return
     */
    public int getDoorSize() {
        return ds;
    }

    /**
     * doWall			draw wall, but check if door there
     * @param bi		interface into which it is shown
     * @param xa		start x coord of wall
     * @param ya		and y
     * @param xb		end x coord of wall
     * @param yb		and y
     */
    private void doWall (Building_GUI bi, int xa, int ya, int xb, int yb) {
       //doWall(bi, xa, ya, xb, yb, Color.BLACK);
    }


    // draw a wall with the specified color
    private void doWall (Building_GUI bi, int xa, int ya, int xb, int yb, Color color, int wallSize){
        if ( (xa == xb) && (xd == xa) ) {			// if vert wall, check if door in it
            bi.showWall(xa, ya, xb, yd-1, color, wallSize);
            bi.showWall(xa, yd+ds, xb, yb, color, wallSize);
        }
        else if ( (ya == yb) && (yd == ya) ) {		// similar for horiz wall
            bi.showWall(xa, ya, xd-1, yd, color, wallSize);
            bi.showWall(xd+ds, yd, xb, yb, color, wallSize);
        }
        else bi.showWall(xa, ya, xb, yb, color, wallSize);
    }

    /**
     * show draw the room using the GUI object
     * @param bi
     */
    @Override
    public void draw (Building_GUI bi) {
        draw(bi, Color.BROWN);
    }

    public void draw(Building_GUI bi, Color color, int wallSize){
        doWall(bi, x1, y1, x1, y2, color, wallSize);			// show each of its walls
        doWall(bi, x2, y1, x2, y2, color, wallSize);
        doWall(bi, x1, y2, x2, y2, color, wallSize);
        doWall(bi, x1, y1, x2, y1, color, wallSize);
    }


    public void draw(Building_GUI bi, Color color){
        doWall(bi, x1, y1, x1, y2, color, 3);			// show each of its walls
        doWall(bi, x2, y1, x2, y2, color, 3);
        doWall(bi, x1, y2, x2, y2, color, 3);
        doWall(bi, x1, y1, x2, y1, color, 3);
    }

    /**
     * get random xy position within rooom
     * @param r		random generator to use
     * @return		coordinate
     */
    public Point getRandom(Random r) {
        return new Point (x1 + 1 + r.nextInt(x2-x1-2),y1 + 1 + r.nextInt(y2-y1-2) );
    }
    /**
     * return x,y coordinate just inside or just outside door
     * @param forinside     being 1 if inside 0 if at door and -1 if outside
     * @return
     */
    public Point getByDoor(int forinside) {
        int x = xd;					// x,y position calculated
        int y = yd;
        // need to find which wall door is on, and act accordingly
        if (yd == y2) { y = yd + forinside; x = xd + ds / 2; }
        else if (yd == y1) { y = yd - forinside; x = xd + ds / 2; }
        else if (xd == x1) { x = xd - forinside; y = yd + ds / 2; }
        else { x = xd + forinside; y = yd + ds / 2; }
        return new Point(x,y);
    }
    /**
     * report whether xy position is within room
     * @param xy
     * @return
     */
    public boolean isInRoom(Point xy) {
        return (xy.getX()>x1 && xy.getX()<x2 && xy.getY()>y1 && xy.getY()<y2);
    }


    /**
     * return String describing room
     */
    public String toString() {
        // get the class name and remove the word "building.drawable.room.building.Room" from it in order to reveal what type of
        // room it is.
        String className = this.getClass().getSimpleName().replace("building.Room", "");

        // get the room details as a string
        String roomDetails = " building.Room from " + x1 + "," + y1 + " to " + x2 + "," + y2 + " door at " + xd + "," + yd;

        // concat the class name and the room details, removing any trailing white space characters.
        return (className + roomDetails).trim();
    }

    /**
     *
     * @return the top left corner X co-ordinate
     */
    public int getX1() {
        return x1;
    }

    /**
     *
     * @return the top left corner Y co-ordinate
     */
    public int getY1() {
        return y1;
    }

    /**
     *
     * @return the bottom right corner X co-ordinate
     */
    public int getX2() {
        return x2;
    }

    /**
     *
     * @return the bottom right corner Y co-ordinate
     */
    public int getY2() {
        return y2;
    }


}