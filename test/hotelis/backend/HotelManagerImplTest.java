/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelis.backend;

import hotelis.common.DBUtils;
import hotelis.common.IllegalEntityException;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.*;

import static org.junit.Assert.*;
import static hotelis.backend.GuestManagerImplTest.newGuest;
import static hotelis.backend.GuestManagerImplTest.assertGuestDeepEquals;
import static hotelis.backend.GuestManagerImplTest.getDate;
import static hotelis.backend.RoomManagerImplTest.newRoom;
import static hotelis.backend.RoomManagerImplTest.assertRoomDeepEquals;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Karel Vaculik
 */
public class HotelManagerImplTest {

    private HotelManagerImpl manager;
    private GuestManagerImpl guestManager;
    private RoomManagerImpl roomManager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:gravemgr-test;create=true");
        //ds.setUrl("jdbc:derby://localhost:1527/test");
        return ds;
    }

    private Room room1, room2, room3, roomWithNullId, roomNotInDB;
    private Guest guest1, guest2, guest3, guest4, guest5, guestWithNullId, guestNotInDB;
    
    private void prepareTestData() {

        room1 = newRoom(2, "A20", true, "Room 1");
        room2 = newRoom(5, "B34", true, "Room 2");
        room3 = newRoom(3, "56", false, "Room 3");
        
        guest1 = newGuest("Guest 1", "Brno 1", "732 111 111", getDate(1980, 1, 1));
        guest2 = newGuest("Guest 2", "Brno 2", "732 222 222", getDate(1981, 2, 2));
        guest3 = newGuest("Guest 3", "Brno 3", "732 333 333", getDate(1982, 3, 3));
        guest4 = newGuest("Guest 4", "Brno 4", "732 444 444", getDate(1983, 4, 4));
        guest5 = newGuest("Guest 5", "Brno 5", "732 555 555", getDate(1984, 5, 5));
        
        guestManager.createGuest(guest1);
        guestManager.createGuest(guest2);
        guestManager.createGuest(guest3);
        guestManager.createGuest(guest4);
        guestManager.createGuest(guest5);
        
        roomManager.createRoom(room1);
        roomManager.createRoom(room2);
        roomManager.createRoom(room3);

        roomWithNullId = newRoom(1, "A1", false, "Room with null id");
        roomNotInDB = newRoom(1, "A1", false, "Room not in DB");
        roomNotInDB.setId(room3.getId() + 100);
        guestWithNullId = newGuest("Guest with null id", "Brno", "732 000 000", getDate(1980, 1, 1));
        guestNotInDB = newGuest("Guest not in DB", "Brno", "732 000 000", getDate(1980, 1, 1));
        guestNotInDB.setId(guest5.getId() + 100);
        
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, RoomManager.class.getResource("createTables.sql"));
        manager = new HotelManagerImpl();
        manager.setDataSource(dataSource);
        guestManager = new GuestManagerImpl();
        guestManager.setDataSource(dataSource);
        roomManager = new RoomManagerImpl();
        roomManager.setDataSource(dataSource);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, RoomManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void findRoomWithGuest() {
        
        assertNull(manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertNull(manager.findRoomWithGuest(guest3));
        assertNull(manager.findRoomWithGuest(guest4));
        assertNull(manager.findRoomWithGuest(guest5));
        
        manager.lodgeGuestToRoom(guest1, room3);

        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertRoomDeepEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertNull(manager.findRoomWithGuest(guest3));
        assertNull(manager.findRoomWithGuest(guest4));
        assertNull(manager.findRoomWithGuest(guest5));
        
        try {
            manager.findRoomWithGuest(null);
            fail();
        } catch (IllegalArgumentException ex) {}
        
        try {
            manager.findRoomWithGuest(guestWithNullId);
            fail();
        } catch (IllegalEntityException ex) {}
        
    }

    @Test
    public void getGuestsInRoom() {

        assertTrue(manager.getGuestsInRoom(room1).isEmpty());
        assertTrue(manager.getGuestsInRoom(room2).isEmpty());
        assertTrue(manager.getGuestsInRoom(room3).isEmpty());

        manager.lodgeGuestToRoom(guest2, room3);
        manager.lodgeGuestToRoom(guest3, room2);
        manager.lodgeGuestToRoom(guest4, room3);
        manager.lodgeGuestToRoom(guest5, room2);
        
        List<Guest> guestsInRoom2 = Arrays.asList(guest3,guest5);
        List<Guest> guestsInRoom3 = Arrays.asList(guest2,guest4);
        
        assertTrue(manager.getGuestsInRoom(room1).isEmpty());
        assertGuestDeepEquals(guestsInRoom2, manager.getGuestsInRoom(room2));
        assertGuestDeepEquals(guestsInRoom3, manager.getGuestsInRoom(room3));
        
        try {
            manager.getGuestsInRoom(null);
            fail();
        } catch (IllegalArgumentException ex) {}
        
        try {
            manager.getGuestsInRoom(roomWithNullId);
            fail();
        } catch (IllegalEntityException ex) {}

    }

        
    @Test
    public void lodgeGuestToRoom() {

        assertNull(manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertNull(manager.findRoomWithGuest(guest3));
        assertNull(manager.findRoomWithGuest(guest4));
        assertNull(manager.findRoomWithGuest(guest5));
        
        manager.lodgeGuestToRoom(guest1, room3);
        manager.lodgeGuestToRoom(guest5, room1);
        manager.lodgeGuestToRoom(guest3, room3);

        List<Guest> guestsInRoom1 = Arrays.asList(guest5);
        List<Guest> guestsInRoom2 = Collections.emptyList();
        List<Guest> guestsInRoom3 = Arrays.asList(guest1,guest3);
        
        assertGuestDeepEquals(guestsInRoom1, manager.getGuestsInRoom(room1));
        assertGuestDeepEquals(guestsInRoom2, manager.getGuestsInRoom(room2));
        assertGuestDeepEquals(guestsInRoom3, manager.getGuestsInRoom(room3));
        
        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertRoomDeepEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertEquals(room3, manager.findRoomWithGuest(guest3));
        assertRoomDeepEquals(room3, manager.findRoomWithGuest(guest3));
        assertNull(manager.findRoomWithGuest(guest4));
        assertEquals(room1, manager.findRoomWithGuest(guest5));
        assertRoomDeepEquals(room1, manager.findRoomWithGuest(guest5));
    
        try {
            manager.lodgeGuestToRoom(guest1, room3);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.lodgeGuestToRoom(guest1, room2);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.lodgeGuestToRoom(null, room2);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            manager.lodgeGuestToRoom(guestWithNullId, room2);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.lodgeGuestToRoom(guestNotInDB, room2);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.lodgeGuestToRoom(guest2, null);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            manager.lodgeGuestToRoom(guest2, roomWithNullId);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.lodgeGuestToRoom(guest2, roomNotInDB);
            fail();
        } catch (IllegalEntityException ex) {}

        // Try to add body to grave that is already full
        try {
            manager.lodgeGuestToRoom(guest2, room1);
            fail();
        } catch (IllegalEntityException ex) {}

        // Check that previous tests didn't affect data in database
        assertGuestDeepEquals(guestsInRoom1, manager.getGuestsInRoom(room1));
        assertGuestDeepEquals(guestsInRoom2, manager.getGuestsInRoom(room2));
        assertGuestDeepEquals(guestsInRoom3, manager.getGuestsInRoom(room3));

        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertEquals(room3, manager.findRoomWithGuest(guest3));
        assertNull(manager.findRoomWithGuest(guest4));
        assertEquals(room1, manager.findRoomWithGuest(guest5));        
    }

    @Test
    public void dislodgeGuestFromRoom() {

        manager.lodgeGuestToRoom(guest1, room3);
        manager.lodgeGuestToRoom(guest3, room3);
        manager.lodgeGuestToRoom(guest4, room3);
        manager.lodgeGuestToRoom(guest5, room1);
        
        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertEquals(room3, manager.findRoomWithGuest(guest3));
        assertEquals(room3, manager.findRoomWithGuest(guest4));
        assertEquals(room1, manager.findRoomWithGuest(guest5));

        manager.dislodgeGuestFromRoom(guest3, room3);

        List<Guest> guestsInRoom1 = Arrays.asList(guest5);
        List<Guest> guestsInRoom2 = Collections.emptyList();
        List<Guest> guestsInRoom3 = Arrays.asList(guest1,guest4);
        
        assertGuestDeepEquals(guestsInRoom1, manager.getGuestsInRoom(room1));
        assertGuestDeepEquals(guestsInRoom2, manager.getGuestsInRoom(room2));
        assertGuestDeepEquals(guestsInRoom3, manager.getGuestsInRoom(room3));

        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertNull(manager.findRoomWithGuest(guest3));
        assertEquals(room3, manager.findRoomWithGuest(guest4));
        assertEquals(room1, manager.findRoomWithGuest(guest5));
                
        try {
            manager.dislodgeGuestFromRoom(guest3, room1);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guest1, room1);
            fail();
        } catch (IllegalEntityException ex) {}
        
        try {
            manager.dislodgeGuestFromRoom(null, room2);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guestWithNullId, room2);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guestNotInDB, room2);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guest2, null);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guest2, roomWithNullId);
            fail();
        } catch (IllegalEntityException ex) {}

        try {
            manager.dislodgeGuestFromRoom(guest2, roomNotInDB);
            fail();
        } catch (IllegalEntityException ex) {}
    
        // Check that previous tests didn't affect data in database
        assertGuestDeepEquals(guestsInRoom1, manager.getGuestsInRoom(room1));
        assertGuestDeepEquals(guestsInRoom2, manager.getGuestsInRoom(room2));
        assertGuestDeepEquals(guestsInRoom3, manager.getGuestsInRoom(room3));

        assertEquals(room3, manager.findRoomWithGuest(guest1));
        assertNull(manager.findRoomWithGuest(guest2));
        assertNull(manager.findRoomWithGuest(guest3));
        assertEquals(room3, manager.findRoomWithGuest(guest4));
        assertEquals(room1, manager.findRoomWithGuest(guest5));

    }
}
