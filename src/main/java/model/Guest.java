package model;

public class Guest {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Guest() {

    }

    public Guest(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Guest) {
            Guest otherGuest = (Guest) obj;
            return id.equals(otherGuest.id) && name.equals(otherGuest.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 47 * id.hashCode() + 17 * name.hashCode();
    }
}
