package model;

public class ThingBean {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThingBean(){}
    public ThingBean(String name) {
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

        if (obj instanceof ThingBean) {
            ThingBean otherThingBean = (ThingBean) obj;
            return name.equals(otherThingBean.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * name.hashCode();
    }
}
