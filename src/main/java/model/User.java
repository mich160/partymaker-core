package model;

public class User {
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

    public User() {

    }

    public User(String name) {
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
        if (obj instanceof User) {
            User otherUser = (User) obj;
            return id.equals(otherUser.id) && name.equals(otherUser.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 47 * id.hashCode() + 17 * name.hashCode();
    }
}
