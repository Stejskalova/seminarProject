package hotelis.backend;

import hotelis.common.ServiceFailureException;
import hotelis.common.IllegalEntityException;
import java.util.List;

/**
 *  
 * 
 * @author Karel Vaculik
 */
public interface HotelManager {
    
    
    Room findRoomWithGuest(Guest guest) throws ServiceFailureException, IllegalEntityException;
    
   
    List<Guest> getGuestsInRoom(Room room) throws ServiceFailureException, IllegalEntityException;

    
    void lodgeGuestToRoom(Guest guest, Room room) throws ServiceFailureException, IllegalEntityException;
    

    void dislodgeGuestFromRoom(Guest guest, Room room) throws ServiceFailureException, IllegalEntityException;    
    
}
