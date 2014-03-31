package hotelis.backend;

import hotelis.common.ServiceFailureException;
import java.util.List;

/**
 *  
 * 
 * @author Karel Vaculik
 */
public interface HotelManager {
    
    
    Room findRoomWithGuest(Guest guest) throws ServiceFailureException, IllegalArgumentException;
    
   
    List<Guest> getGuestsInRoom(Room room) throws ServiceFailureException, IllegalArgumentException;

    
    void lodgeGuestToRoom(Guest guest, Room room) throws ServiceFailureException, IllegalArgumentException;
    

    void dislodgeGuestFromRoom(Guest guest, Room room) throws ServiceFailureException, IllegalArgumentException;    
    
}
