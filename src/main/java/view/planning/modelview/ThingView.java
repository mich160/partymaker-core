package view.planning.modelview;

import java.util.Objects;

public class ThingView {
    private final String name;

    public ThingView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingView thingView = (ThingView) o;
        return Objects.equals(name, thingView.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
