
package hotelis;

import java.util.List;

/**
 * Interface Room Manager
 * @author Natália Stejskalova, 410457
 */
public interface RoomManager {
    
    /**
     * Creates a room in database
     * @param room
     * @return room
     */
    public Room createRoom(Room room);
    
    /**
     * Deletes room from database
     * @param room 
     */
    public void deleteRoom(Room room);
    
    /**
     * Finds all rooms in Hotel
     * @return list od rooms
     */
    public List<Room> findAllRooms();
    
    /**
     * Finds specified room by its id
     * @param id
     * @return room
     */
    public Room findRoomById(Integer id);
    
    /**
     * Updates room
     * @param room 
     */
    public void updateRoom(Room room);
}
