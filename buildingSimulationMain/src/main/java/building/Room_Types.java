package building;

/**
 *
 *  class to represent the room type.
 */
//Currenly only contains one type of room but more room can be added 
public enum Room_Types {
   EMPTY,RoomType2;


    /**
     * Render a RoomType as a string.
     * @return a room type as a string.
     */
    @Override
    public String toString(){
        return super.toString().replace("_", " ");
    }
}
