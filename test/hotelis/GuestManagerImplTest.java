/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import java.util.Locale;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.After;

/**
 *
 * @author Vaculík Karel, 357619
 */
public class GuestManagerImplTest {

    private GuestManagerImpl manager;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:memory:GuestManagerImplTest;create=true");
        connection.prepareStatement("CREATE TABLE GUEST("
                + "id int primary key generated always as identity,"
                + "name varchar(80) not null,"
                + "address varchar(255) not null,"
                + "phone varchar(20) not null,"
                + "birthdate timestamp not null)").executeUpdate();
        manager = new GuestManagerImpl(connection);
    }
    
    @After
    public void tearDown() throws SQLException {
        connection.prepareStatement("DROP TABLE GUEST").executeUpdate();        
        connection.close();
    }
    

    @Test
    public void createGuest() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);
        manager.createGuest(guest);

        Integer guestId = guest.getId();
        assertNotNull(guestId);
        Guest result = manager.findGuestById(guestId);
        assertEquals(guest, result);
        assertNotSame(guest, result);
        assertDeepEquals(guest, result);
    }

    @Test
    public void getGuestById() {

        assertNull(manager.findGuestById(1));

        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        Guest result = manager.findGuestById(guestId);
        assertEquals(guest, result);
        assertDeepEquals(guest, result);
    }

    @Test
    public void getAllGuests() {

        assertTrue(manager.findAllGuests().isEmpty());

        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);

        Guest guest1 = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);

        manager.createGuest(guest1);
        manager.createGuest(guest2);

        List<Guest> expected = Arrays.asList(guest1, guest2);
        List<Guest> actual = manager.findAllGuests();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullValue() {
        manager.createGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullName() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest(null, "Praha, Slezska 33", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidName() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest(" \t\n", "Praha, Slezska 33", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullAddress() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Martin Dvorak", null, "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidAddress() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Martin Dvorak", " \t\n", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullPhone() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", null, birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidPhone() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", " \t\n", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullBirthdate() {
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", null);
        manager.createGuest(guest);
    }

    @Test
    public void addGuestWithValidAttributes() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest;

        // these variants should be ok
        guest = newGuest(" M ", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Guest result = manager.findGuestById(guest.getId());
        assertNotNull(result);

        guest = newGuest("Martin Dvorak", ", ", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        result = manager.findGuestById(guest.getId());
        assertNotNull(result);

        guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+", birthdate);
        manager.createGuest(guest);
        result = manager.findGuestById(guest.getId());
        assertNotNull(result);

    }

    @Test
    public void updateGuestName() {
        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest);
        manager.createGuest(guest2);
        Integer guestId = guest.getId();

        guest.setName("Sandra Doubravova");
        manager.updateGuest(guest);
        guest = manager.findGuestById(guestId);
        assertEquals("Sandra Doubravova", guest.getName());
        assertEquals("Brno, Zahradni 9", guest.getAddress());
        assertEquals("+420 605 889 775", guest.getPhone());
        assertEquals(birthdate1, guest.getBirthdate());

        // Check if updates didn't affected other records
        assertDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestAddress() {
        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest);
        manager.createGuest(guest2);
        Integer guestId = guest.getId();

        guest.setAddress("Plzen, Vaclavova 68");
        manager.updateGuest(guest);
        guest = manager.findGuestById(guestId);
        assertEquals("Ondrej Novy", guest.getName());
        assertEquals("Plzen, Vaclavova 68", guest.getAddress());
        assertEquals("+420 605 889 775", guest.getPhone());
        assertEquals(birthdate1, guest.getBirthdate());

        // Check if updates didn't affected other records
        assertDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestPhone() {
        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest);
        manager.createGuest(guest2);
        Integer guestId = guest.getId();

        guest.setPhone("+420 737 942 516");
        manager.updateGuest(guest);
        guest = manager.findGuestById(guestId);
        assertEquals("Ondrej Novy", guest.getName());
        assertEquals("Brno, Zahradni 9", guest.getAddress());
        assertEquals("+420 737 942 516", guest.getPhone());
        assertEquals(birthdate1, guest.getBirthdate());

        // Check if updates didn't affected other records
        assertDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestBirthdate() {
        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest);
        manager.createGuest(guest2);
        Integer guestId = guest.getId();
        
        Date newBirthdate = getDateFromCalendar(1989,6,2);
        guest.setBirthdate(newBirthdate);
        manager.updateGuest(guest);
        guest = manager.findGuestById(guestId);
        assertEquals("Ondrej Novy", guest.getName());
        assertEquals("Brno, Zahradni 9", guest.getAddress());
        assertEquals("+420 605 889 775", guest.getPhone());
        assertEquals(newBirthdate, guest.getBirthdate());

        // Check if updates didn't affected other records
        assertDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullValue() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);

        manager.updateGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullId() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setId(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidId() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setId(guestId - 1);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullName() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setName(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidName() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setName("\t \n");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullAddress() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setAddress(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidAddress() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setAddress("\n \t");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullPhone() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setPhone(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidPhone() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setPhone(" \t\n");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullDate() {
        Date birthdate = getDateFromCalendar(1974,12,18);
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setBirthdate(null);
        manager.updateGuest(guest);
    }

    @Test
    public void deleteGuest() {
        Date birthdate1 = getDateFromCalendar(1985,5,27);
        Date birthdate2 = getDateFromCalendar(1974,12,18);
        Guest guest1 = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest1);
        manager.createGuest(guest2);

        assertNotNull(manager.findGuestById(guest1.getId()));
        assertNotNull(manager.findGuestById(guest2.getId()));

        manager.deleteGuest(guest1);

        assertNull(manager.findGuestById(guest1.getId()));
        assertNotNull(manager.findGuestById(guest2.getId()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithNullValue() {
        manager.deleteGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithNullId() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);

        guest.setId(null);
        manager.deleteGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithWrongId() {
        Date birthdate = getDateFromCalendar(1985,5,27);
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);

        guest.setId(1);
        manager.deleteGuest(guest);
    }

    private static Guest newGuest(String name, String address, String phone, Date birthdate) {
        Guest guest = new Guest();
        guest.setName(name);
        guest.setAddress(address);
        guest.setPhone(phone);
        guest.setBirthdate(birthdate);
        return guest;
    }

    private void assertDeepEquals(List<Guest> expectedList, List<Guest> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Guest expected = expectedList.get(i);
            Guest actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Guest expected, Guest actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getBirthdate(), actual.getBirthdate());
    }
    
    private Date getDateFromCalendar(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "Europe/London"), Locale.UK);
        calendar.set(year, month - 1, day, 0, 0, 0);
        return calendar.getTime();
    }

    private static final Comparator<Guest> idComparator = new Comparator<Guest>() {

        @Override
        public int compare(Guest o1, Guest o2) {
            return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
        }
    };

}
