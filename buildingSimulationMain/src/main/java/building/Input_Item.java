package building;

/**
 
 * The class {@code building.drawable.DrawableItem} models any item that can be drawn on the GUI object
 * provided.
 */
public abstract class Input_Item  {
    // ID generator for all the subclasses created by inheretence
    private static long id = 0;
    protected final long serialVersionUID; // version ID used for controlling deserialization

    /**
     *  Drawable item constructor.
     */
    protected Input_Item(){
        // increment the id by 1 
        id++;

        // then  assign the serialVersionUID
        serialVersionUID = id;
    }

    /**
     *  Subclasses should implement this method in order to render themselves
     *  on using the GUI object supplied.
     */
    public abstract void draw(Building_GUI buildingGUI);

}
