package model;

public class User {
    private Long id;
    private String name;
    private String surname;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User() {

    }

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    @Override
    public String toString() {
        return name + " " + surname + " ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof User) {
            User otherUser = (User) obj;
            return id.equals(otherUser.id) && name.equals(otherUser.name) && surname.equals(otherUser.surname);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 47 * id.hashCode() + 17 * name.hashCode() + 31 * surname.hashCode();
    }
}
