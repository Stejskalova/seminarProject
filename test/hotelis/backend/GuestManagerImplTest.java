
package hotelis.backend;

import hotelis.common.DBUtils;

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
import java.sql.Date;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;

/**
 *
 * @author Vaculík Karel, 357619
 */
public class GuestManagerImplTest {

    private GuestManagerImpl manager;
    private DataSource dataSource;
    
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        //we will use in memory database
        dataSource.setUrl("jdbc:derby:memory:guestmanager-test;create=true");
        return dataSource;
    }

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource,GuestManager.class.getResource("createTables.sql"));
        manager = new GuestManagerImpl();
        manager.setDataSource(dataSource);
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource,GuestManager.class.getResource("dropTables.sql"));
    }
    

    @Test
    public void createGuest() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);
        manager.createGuest(guest);

        Integer guestId = guest.getId();
        assertNotNull(guestId);
        Guest result = manager.findGuestById(guestId);
        assertEquals(guest, result);
        assertNotSame(guest, result);
        assertGuestDeepEquals(guest, result);
    }

    @Test
    public void getGuestById() {

        assertNull(manager.findGuestById(1));

        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        Guest result = manager.findGuestById(guestId);
        assertEquals(guest, result);
        assertGuestDeepEquals(guest, result);
    }

    @Test
    public void getAllGuests() {

        assertTrue(manager.findAllGuests().isEmpty());

        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");

        Guest guest1 = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);

        manager.createGuest(guest1);
        manager.createGuest(guest2);

        List<Guest> expected = Arrays.asList(guest1, guest2);
        List<Guest> actual = manager.findAllGuests();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertGuestDeepEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullValue() {
        manager.createGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullName() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest(null, "Praha, Slezska 33", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidName() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest(" \t\n", "Praha, Slezska 33", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullAddress() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Martin Dvorak", null, "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidAddress() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Martin Dvorak", " \t\n", "+420 723 544 689", birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithNullPhone() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", null, birthdate);

        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGuestWithInvalidPhone() {
        Date birthdate = getDate("1985-05-27");
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
        Date birthdate = getDate("1985-05-27");
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
        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");
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
        assertGuestDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestAddress() {
        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");
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
        assertGuestDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestPhone() {
        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");
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
        assertGuestDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test
    public void updateGuestBirthdate() {
        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate1);
        Guest guest2 = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate2);
        manager.createGuest(guest);
        manager.createGuest(guest2);
        Integer guestId = guest.getId();
        
        Date newBirthdate = getDate("1989-06-02");
        guest.setBirthdate(newBirthdate);
        manager.updateGuest(guest);
        guest = manager.findGuestById(guestId);
        assertEquals("Ondrej Novy", guest.getName());
        assertEquals("Brno, Zahradni 9", guest.getAddress());
        assertEquals("+420 605 889 775", guest.getPhone());
        assertEquals(newBirthdate, guest.getBirthdate());

        // Check if updates didn't affected other records
        assertGuestDeepEquals(guest2, manager.findGuestById(guest2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullValue() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);

        manager.updateGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullId() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setId(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidId() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setId(guestId - 1);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullName() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setName(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidName() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setName("\t \n");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullAddress() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setAddress(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidAddress() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setAddress("\n \t");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullPhone() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setPhone(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithInvalidPhone() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setPhone(" \t\n");
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNullDate() {
        Date birthdate = getDate("1974-12-18");
        Guest guest = newGuest("Martin Dvorak", "Praha, Slezska 33", "+420 723 544 689", birthdate);
        manager.createGuest(guest);
        Integer guestId = guest.getId();

        guest = manager.findGuestById(guestId);
        guest.setBirthdate(null);
        manager.updateGuest(guest);
    }

    @Test
    public void deleteGuest() {
        Date birthdate1 = getDate("1985-05-27");
        Date birthdate2 = getDate("1974-12-18");
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
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);

        guest.setId(null);
        manager.deleteGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithWrongId() {
        Date birthdate = getDate("1985-05-27");
        Guest guest = newGuest("Ondrej Novy", "Brno, Zahradni 9", "+420 605 889 775", birthdate);

        guest.setId(1);
        manager.deleteGuest(guest);
    }

    static Guest newGuest(String name, String address, String phone, Date birthdate) {
        Guest guest = new Guest();
        guest.setName(name);
        guest.setAddress(address);
        guest.setPhone(phone);
        guest.setBirthdate(birthdate);
        return guest;
    }

    static void assertGuestDeepEquals(List<Guest> expectedList, List<Guest> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Guest expected = expectedList.get(i);
            Guest actual = actualList.get(i);
            assertGuestDeepEquals(expected, actual);
        }
    }

    static void assertGuestDeepEquals(Guest expected, Guest actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getBirthdate(), actual.getBirthdate());
    }
    
    static Date getDate(String date) {
                return Date.valueOf(date);
    }

    private static final Comparator<Guest> idComparator = new Comparator<Guest>() {

        @Override
        public int compare(Guest g1, Guest g2) {
            Integer Id1 = g1.getId();
            Integer Id2 = g2.getId();
            if (Id1 == null && Id2 == null) {
                return 0;
            } else if (Id1 == null && Id2 != null) {
                return -1;
            } else if (Id1 != null && Id2 == null) {
                return 1;
            } else {
                return Id1.compareTo(Id2);
            }
        }
    };

}
