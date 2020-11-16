package model;

public class Thing {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Thing(){}
    public Thing(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Thing) {
            Thing otherThing = (Thing) obj;
            return name.equals(otherThing.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * name.hashCode();
    }
}
