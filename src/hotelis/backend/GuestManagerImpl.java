/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hotelis.backend;

import hotelis.common.DBUtils;
import hotelis.common.ServiceFailureException;
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
import javax.sql.DataSource;

/**
 *
 * @author Vaculik
 */
public class GuestManagerImpl implements GuestManager {
    
    public static final Logger logger = Logger.getLogger(GuestManagerImpl.class.getName());
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    @Override
    public void createGuest(Guest guest) throws ServiceFailureException {
        checkDataSource();
        validateGuest(guest);
        if (guest.getId() != null) {
            throw new IllegalArgumentException("guest id cannot be already set");            
        }
        
        Connection connection = null;
        PreparedStatement prepStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            prepStatement = connection.prepareStatement(
                    "INSERT INTO GUEST (name,address,phone,birthdate) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1, guest.getName());
            prepStatement.setString(2, guest.getAddress());
            prepStatement.setString(3, guest.getPhone());
            prepStatement.setDate(4, guest.getBirthdate());
            int updatesCount = prepStatement.executeUpdate();
            DBUtils.checkUpdatesCount(updatesCount, guest, true);
            
            Integer id = DBUtils.getId(prepStatement.getGeneratedKeys());
            guest.setId(id);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Failure occur when creating new guest " + guest;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, prepStatement);
        }
        
    }
    

    @Override
    public Guest findGuestById(Integer id) throws ServiceFailureException {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        
        Connection connection = null;
        PreparedStatement prepStatement = null;
        try {
            connection = dataSource.getConnection();
            prepStatement = connection.prepareStatement("SELECT id, name, address, phone, birthdate "
                    + "FROM guest WHERE id = ?");
            prepStatement.setInt(1, id);
            ResultSet resultSet = prepStatement.executeQuery();
            
            if (resultSet.next()) {
                Guest guest =  getGuestFromResultSet(resultSet);
                if (resultSet.next()) {
                    throw new ServiceFailureException("Failure occur when finding guest with id " 
                        + id + " - more then one guest was found");
                }
                return guest;
            } else {
                return null;
            }
        } catch(SQLException ex) {
            String msg = "Failure occur when finding guest with id " + id;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(connection, prepStatement);
        }
    }
    
   
    @Override
    public void deleteGuest(Guest guest) throws UnsupportedOperationException {
        checkDataSource();
        validateGuest(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("Guest id cannot be null");
        }
        
        Connection connection = null;
        PreparedStatement prepStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            prepStatement = connection.prepareStatement("DELETE FROM GUEST WHERE id = ? ");
            prepStatement.setInt(1, guest.getId());

            int deletedRowsCount = prepStatement.executeUpdate();
            DBUtils.checkUpdatesCount(deletedRowsCount, guest, false);
            connection.commit();
        } catch(SQLException ex) {
            String msg = "Failure occur when deleting guest" + guest;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, prepStatement);
        }
    }

    @Override
    public List<Guest> findAllGuests() throws ServiceFailureException {
        checkDataSource();
        Connection connection = null;
        PreparedStatement prepStatement = null;
        try {
            connection = dataSource.getConnection();
            prepStatement = connection.prepareStatement("SELECT id, name, address, phone, birthdate "
                    + "FROM guest");
            ResultSet resultSet = prepStatement.executeQuery();
            List<Guest> allGuests = new ArrayList<>();
            while(resultSet.next()) {
                allGuests.add(getGuestFromResultSet(resultSet));
            }
            return allGuests;
        } catch(SQLException ex) {
            String msg = "Failure occur when finding all guests";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg , ex);
        } finally {
            DBUtils.closeQuietly(connection, prepStatement);
        }
    }

    @Override
    public void updateGuest(Guest guest) throws UnsupportedOperationException {
        checkDataSource();
        validateGuest(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("Guest id cannot be null");            
        }
        
        Connection connection = null;
        PreparedStatement prepStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            prepStatement = connection.prepareStatement(
                    "UPDATE GUEST SET name = ?,address = ?,phone = ?,birthdate = ? WHERE id = ?");
            prepStatement.setString(1, guest.getName());
            prepStatement.setString(2, guest.getAddress());
            prepStatement.setString(3, guest.getPhone());
            prepStatement.setDate(4, guest.getBirthdate());
            prepStatement.setInt(5, guest.getId());
            int updatesCount = prepStatement.executeUpdate();
            DBUtils.checkUpdatesCount(updatesCount, guest, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Failure occur when updating guest " + guest;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, prepStatement);
        }
    }
    
    private Guest getGuestFromResultSet(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setId(rs.getInt("id"));
        g.setName(rs.getString("name"));
        g.setAddress(rs.getString("address"));
        g.setPhone(rs.getString("phone"));
        g.setBirthdate(rs.getDate("birthdate"));
        return g;
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
}
