package model;

public class Thing {
    private Long id;
    private String name;
    private Long participation_id;

    public Thing(){}

    public Thing(String name, Long participation_id) {
        this.name = name;
        this.participation_id = participation_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipation_id() {
        return participation_id;
    }

    public void setParticipation_id(Long participation_id) {
        this.participation_id = participation_id;
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
