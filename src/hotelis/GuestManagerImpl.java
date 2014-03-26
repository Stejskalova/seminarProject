/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hotelis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

/**
 *
 * @author Vaculik
 */
public class GuestManagerImpl implements GuestManager {
    
    private Connection connection;
    public static final Logger logger = Logger.getLogger(GuestManagerImpl.class.getName());
    
    public GuestManagerImpl(Connection conn) {
        connection = conn;
    }

    @Override
    public void createGuest(Guest guest) throws ServiceFailureException {
        validateGuest(guest);
        if (guest.getId() != null) {
            throw new IllegalArgumentException("guest id cannot be already set");            
        }
        
        PreparedStatement prepStatement = null;
        try {
            prepStatement = connection.prepareStatement(
                    "INSERT INTO GUEST (name,address,phone,birthdate) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1, guest.getName());
            prepStatement.setString(2, guest.getAddress());
            prepStatement.setString(3, guest.getPhone());
            prepStatement.setTimestamp(4, getTimestamp(guest.getBirthdate()));
            prepStatement.executeUpdate();
            
            ResultSet generatedKeys = prepStatement.getGeneratedKeys();
            guest.setId(getGeneratedKey(generatedKeys,guest));
        } catch (SQLException ex) {
            throw new ServiceFailureException("Failure occur when creating new guest " + guest, ex);
        } finally {
            if (prepStatement != null) {
                try {
                    prepStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    private Integer getGeneratedKey(ResultSet keys, Guest g) throws SQLException, ServiceFailureException {
        if (keys.next()) {
            Integer key = keys.getInt(1);
            if (keys.next()) {
                throw new ServiceFailureException("Failure occur when setting id of guest "
                        + g + " - more then one key was generated");
            }
            int keyFieldsCount = keys.getMetaData().getColumnCount();
            if (keyFieldsCount != 1) {
                throw new ServiceFailureException("Failure occur when setting id of guest "
                + g + " - invalid key fields count " + keyFieldsCount);
            }
           
            return key;
        } else {
            throw new ServiceFailureException("Failure occur when setting id of guest "
                    + g + " - no key was generated");
        }
    }
    
    private java.sql.Date getSqlDate(Date date) {
        java.sql.Date   sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }
    
    private Timestamp getTimestamp(Date date) {
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }
    
    private void validateGuest(Guest g) {
        if (g == null) {
            throw new IllegalArgumentException("guest cannot be null");            
        }
        if (g.getName() == null) {
            throw new IllegalArgumentException("guest name cannot be null");            
        }
        if (g.getName().trim().equals("")) {
            throw new IllegalArgumentException("guest name cannot be only whitespace");
        }
        if (g.getAddress() == null) {
            throw new IllegalArgumentException("guest address cannot be null");            
        }
        if (g.getAddress().trim().equals("")) {
            throw new IllegalArgumentException("guest address cannot be only whitespace");
        }
        if (g.getPhone() == null) {
            throw new IllegalArgumentException("guest phone cannot be null");
        }
        if (g.getPhone().trim().equals("")) {
            throw new IllegalArgumentException("guest phone cannot be only whitespace");
        }
        if (g.getBirthdate() == null) {
            throw new IllegalArgumentException("guest birthdate cannot be null");
        }
    }

    @Override
    public Guest findGuestById(Integer id) throws ServiceFailureException {
        PreparedStatement prepStatement = null;
        try {
            prepStatement = connection.prepareStatement("SELECT id, name, address, phone, birthdate "
                    + "FROM guest WHERE id = ?");
            prepStatement.setInt(1, id);
            ResultSet resultSet = prepStatement.executeQuery();
            
            if (resultSet.next()) {
                Guest guest =  createGuestFromResultSet(resultSet);
                if (resultSet.next()) {
                    throw new ServiceFailureException("Failure occur when finding guest with id " 
                        + id + " - more then one guest was found");
                }
                return guest;
            } else {
                return null;
            }
        } catch(SQLException ex) {
            throw new ServiceFailureException("Failure occur when finding guest with id " + id, ex);
        } finally {
            if (prepStatement != null) {
                try {
                    prepStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private Guest createGuestFromResultSet(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setId(rs.getInt("id"));
        g.setName(rs.getString("name"));
        g.setAddress(rs.getString("address"));
        g.setPhone(rs.getString("phone"));
        java.util.Date date = new java.util.Date(rs.getTimestamp("birthdate").getTime());
        g.setBirthdate(date);
        return g;
    }

    @Override
    public void deleteGuest(Guest guest) throws UnsupportedOperationException {
        validateGuest(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("Guest id cannot be null");
        }
        PreparedStatement prepStatement = null;
        
        try {
            prepStatement = connection.prepareStatement("DELETE FROM GUEST WHERE "
                    + "id = ? AND name = ? AND address = ? AND phone = ?");
            prepStatement.setInt(1, guest.getId());
            prepStatement.setString(2, guest.getName());
            prepStatement.setString(3, guest.getAddress());
            prepStatement.setString(4, guest.getPhone());
            int deletedRowsCount = prepStatement.executeUpdate();
            if (deletedRowsCount == 0) {
                throw new IllegalArgumentException("Guest " + guest + " doesn't exist");
            }
        } catch(SQLException ex) {
            throw new ServiceFailureException("Failure occur when deleting guest" + guest, ex);
        } finally {
            if (prepStatement != null) {
                try {
                    prepStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public List<Guest> findAllGuests() throws ServiceFailureException {
        PreparedStatement prepStatement = null;
        try {
            prepStatement = connection.prepareStatement("SELECT id, name, address, phone, birthdate "
                    + "FROM guest");
            ResultSet resultSet = prepStatement.executeQuery();
            List<Guest> allGuests = new ArrayList<>();
            while(resultSet.next()) {
                allGuests.add(createGuestFromResultSet(resultSet));
            }
            return allGuests;
        } catch(SQLException ex) {
            throw new ServiceFailureException("Failure occur when finding all guests" , ex);
        } finally {
            if (prepStatement != null) {
                try {
                    prepStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void updateGuest(Guest guest) throws UnsupportedOperationException {
        validateGuest(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("Guest id cannot be null");            
        }
        
        PreparedStatement prepStatement = null;
        try {
            prepStatement = connection.prepareStatement(
                    "UPDATE GUEST SET name = ?,address = ?,phone = ?,birthdate = ? WHERE id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1, guest.getName());
            prepStatement.setString(2, guest.getAddress());
            prepStatement.setString(3, guest.getPhone());
            prepStatement.setTimestamp(4, getTimestamp(guest.getBirthdate()));
            prepStatement.setInt(5, guest.getId());
            int updatedRowsCount = prepStatement.executeUpdate();
            if (updatedRowsCount == 0) {
                throw new IllegalArgumentException("Guest with id " + guest.getId() + " doesn't exist");
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Failure occur when creating new guest " + guest, ex);
        } finally {
            if (prepStatement != null) {
                try {
                    prepStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
