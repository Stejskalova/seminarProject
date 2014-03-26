package hotelis;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.sql.SQLException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Natália Stejskalova, 410457
 */
public class RoomManagerImplTest {

    private RoomManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new RoomManagerImpl();
    }

    /**
     * Test of createRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testCreateRoom() {
        Room room = newRoom(2, "A1", false, "First room");
        manager.createRoom(room);

        Integer roomId = room.getId();
        assertNotNull(roomId);
        Room result = manager.findRoomById(roomId);
        assertEquals(room, result);
        assertNotSame(room, result);
        assertDeepEquals(room, result);
    }

    /**
     * Test of findRoomById method, of class RoomManagerImpl.
     */
    @Test
    public void testFindRoomById() {
        System.out.println("findRoomById");

        assertNull(manager.findRoomById(1));

        Room r1 = newRoom(1, "A1", false, "First room");
        manager.createRoom(r1);
        Integer roomId = r1.getId();

        Room result = manager.findRoomById(roomId);
        assertEquals(r1, result);
        assertDeepEquals(r1, result);

    }

    /**
     * Test of findAllRooms method, of class RoomManagerImpl.
     */
    @Test
    public void testFindAllRooms() {
        System.out.println("findAllRooms");
        assertTrue(manager.findAllRooms().isEmpty());

        Room r1 = newRoom(2, "A1", false, "First room");
        Room r2 = newRoom(3, "A2", true, "Second room");

        manager.createRoom(r1);
        manager.createRoom(r2);

        List<Room> expected = Arrays.asList(r1, r2);
        List<Room> actual = manager.findAllRooms();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);

    }
    
    /**
     * Tests of createRoom method with wrong attributes, of class RoomManagerImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addRoomWithNullValue() {
        manager.createRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRoomWithZeroCapacity() {
        Room room = newRoom(0, "A1", false, "First room");
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRoomWithNegativeCapacity() {
        Room room = newRoom(-2, "A1", false, "First room");
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRoomWithNullRoomNumber() {
        Room room = newRoom(2, null, false, "First room");
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRoomWithInvalidRoomNumber() {
        Room room = newRoom(2, " \t\n", false, "First room");
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithValidAttributes() {
        Room room;

        // these variants should be ok
        room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Room result = manager.findRoomById(room.getId());
        assertNotNull(result);

        room = newRoom(2, "   1   ", true, "Under reconstruction");
        manager.createRoom(room);
        result = manager.findRoomById(room.getId());
        assertNotNull(result);

        room = newRoom(6, "\n1\t", false, null);
        manager.createRoom(room);
        result = manager.findRoomById(room.getId());
        assertNotNull(result);

    }

    /**
     * Test of deleteRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testDeleteRoom() {
        Room r1 = newRoom(2, "A1", false, "First room");
        Room r2 = newRoom(2, "A2", false, "Second room");
        manager.createRoom(r1);
        manager.createRoom(r2);

        assertNotNull(manager.findRoomById(r1.getId()));
        assertNotNull(manager.findRoomById(r2.getId()));

        manager.deleteRoom(r1);

        assertNull(manager.findRoomById(r1.getId()));
        assertNotNull(manager.findRoomById(r2.getId()));
    }

    //@Test(expected = IllegalArgumentException.class)
    
    /**
     * Tests of deleteRoom method with wrong attributes, of class RoomManagerImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithNullValue() {
        manager.deleteRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithNullId() {
        Room room = newRoom(2, "A1", false, "First room");

        manager.deleteRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithWrongId() {
        Room room = newRoom(2, "A1", false, "First room");

        manager.deleteRoom(room);
    }

    /**
     * Tests of updateRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testUpdateRoomCapacity() {
        Room r1 = newRoom(1, "A1", false, "First room");
        Room r2 = newRoom(2, "A2", true, "Second room");

        manager.createRoom(r1);
        manager.createRoom(r2);
        Integer roomId = r1.getId();

        r1 = manager.findRoomById(roomId);
        r1.setCapacity(5);
        manager.updateRoom(r1);
        assertEquals("A1", r1.getRoomNumber());
        assertEquals(false, r1.isDoubleBed());
        assertEquals(5, r1.getCapacity());
        assertEquals("First room", r1.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.findRoomById(r2.getId()));
    }

    @Test
    public void testUpdateRoomNumber() {
        Room r1 = newRoom(1, "A1", false, "First room");
        Room r2 = newRoom(2, "A2", true, "Second room");

        manager.createRoom(r1);
        manager.createRoom(r2);
        Integer roomId = r1.getId();

        r1 = manager.findRoomById(roomId);
        r1.setRoomNumber("B1");
        manager.updateRoom(r1);
        assertEquals("B1", r1.getRoomNumber());
        assertEquals(false, r1.isDoubleBed());
        assertEquals(1, r1.getCapacity());
        assertEquals("First room", r1.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.findRoomById(r2.getId()));
    }

    @Test
    public void testUpdateRoomDoublebBed() {
        Room r1 = newRoom(1, "A1", false, "First room");
        Room r2 = newRoom(2, "A2", true, "Second room");

        manager.createRoom(r1);
        manager.createRoom(r2);
        Integer roomId = r1.getId();

        r1 = manager.findRoomById(roomId);
        r1.setDoubleBed(true);
        manager.updateRoom(r1);
        assertEquals("A1", r1.getRoomNumber());
        assertEquals(true, r1.isDoubleBed());
        assertEquals(1, r1.getCapacity());
        assertEquals("First room", r1.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.findRoomById(r2.getId()));
    }

    @Test
    public void testUpdateRoomNote() {
        Room r1 = newRoom(1, "A1", false, "First room");
        Room r2 = newRoom(2, "A2", true, "Second room");

        manager.createRoom(r1);
        manager.createRoom(r2);
        Integer roomId = r1.getId();

        r1 = manager.findRoomById(roomId);
        r1.setNote("BEST room");
        manager.updateRoom(r1);
        assertEquals("A1", r1.getRoomNumber());
        assertEquals(false, r1.isDoubleBed());
        assertEquals(1, r1.getCapacity());
        assertEquals("BEST room", r1.getNote());

        r1 = manager.findRoomById(roomId);
        r1.setNote(null);
        manager.updateRoom(r1);
        assertEquals("A1", r1.getRoomNumber());
        assertEquals(false, r1.isDoubleBed());
        assertEquals(1, r1.getCapacity());
        assertNull(r1.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.findRoomById(r2.getId()));
    }

    /**
     * Tests of updateRoom method with wrong attributes, of class RoomManagerImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNullValue() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);

        manager.updateRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNullId() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setId(null);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithInvalidId() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setId(roomId - 1);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithZeroCapacity() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setCapacity(0);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNegativeCapacity() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setCapacity(-3);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNullRoomNumber() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setRoomNumber(null);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithInvalidRoomNumber() {
        Room room = newRoom(1, "A1", false, "First room");
        manager.createRoom(room);
        Integer roomId = room.getId();

        room = manager.findRoomById(roomId);
        room.setRoomNumber(" \t\n");
        manager.updateRoom(room);
    }

    private static Room newRoom(int capacity, String roomNumber, boolean doubleBed, String note) {
        Room room = new Room();
        room.setCapacity(capacity);
        room.setRoomNumber(roomNumber);
        room.setDoubleBed(doubleBed);
        room.setNote(note);
        return room;
    }

    private void assertDeepEquals(List<Room> expectedList, List<Room> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Room expected = expectedList.get(i);
            Room actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Room expected, Room actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getRoomNumber(), actual.getRoomNumber());
        assertEquals(expected.isDoubleBed(), actual.isDoubleBed());
        assertEquals(expected.getNote(), actual.getNote());
    }

    private static Comparator<Room> idComparator = new Comparator<Room>() {

        @Override
        public int compare(Room r1, Room r2) {
            return Integer.valueOf(r1.getId()).compareTo(Integer.valueOf(r2.getId()));
        }
    };
}
