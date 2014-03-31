package hotelis.backend;

import java.util.Objects;

/**
 * Class Room represents a chamber in the Hotel
 * 
 * @author Natália Stejskalova, 410457
 */
public class Room {
    private Integer id;
    private int capacity;
    private String roomNumber;
    private boolean doubleBed;
    private String note;

    public Room() {
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + 
                ", capacity=" + capacity + 
                ", roomNumber=" + roomNumber +
                ", doubleBed=" + doubleBed + 
                ", note=" + note + '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isDoubleBed() {
        return doubleBed;
    }

    public void setDoubleBed(boolean doubleBed) {
        this.doubleBed = doubleBed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
}
