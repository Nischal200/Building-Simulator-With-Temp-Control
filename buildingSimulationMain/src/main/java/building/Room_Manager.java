package building;

/**

 * The host to the room factory.
 */
public class Room_Manager {

    /**
     * Factory method to instantiate a room.
     * @param roomConfiguration room string configuration data.
     * @param roomType the room type.
     * @return the instantiated room.
     */
    public static Room getRoom(String roomConfiguration, Room_Types roomType) {
        switch (roomType) {
            case EMPTY:
                return new Room(roomConfiguration);
           
            default:
                return null;
        }
    }
}
