package model;

public class UserBean
{
    private String name;
    private String surname;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public UserBean()
    {

    }
    public UserBean(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    @Override
    public String toString()
    {
        return name + " " + surname + " ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if(obj instanceof UserBean){
            UserBean otherUserBean = (UserBean) obj;
            return name.equals(otherUserBean.name) && surname.equals(otherUserBean.surname);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * name.hashCode() + 31 * surname.hashCode();
    }
}
