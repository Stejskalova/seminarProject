/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelis.backend;

import hotelis.common.ServiceFailureException;
import java.util.List;

/**
 *
 * @author Vaculik
 */
public class HotelManagerImpl implements HotelManager {

    @Override
    public Room findRoomWithGuest(Guest guest) throws ServiceFailureException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Guest> getGuestsInRoom(Room room) throws ServiceFailureException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lodgeGuestToRoom(Guest guest, Room room) throws ServiceFailureException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dislodgeGuestFromRoom(Guest guest, Room room) throws ServiceFailureException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
