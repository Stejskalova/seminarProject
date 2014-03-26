/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelis;

import java.util.List;

/**
 *
 * @author Vaculik
 */
public interface GuestManager {

    void createGuest(Guest guest) throws UnsupportedOperationException;

    Guest findGuestById(Integer id) throws UnsupportedOperationException;

    void deleteGuest(Guest guest) throws UnsupportedOperationException;

    List<Guest> findAllGuests() throws UnsupportedOperationException;

    void updateGuest(Guest guest) throws UnsupportedOperationException;

}
