package view.planning.modelview;

import java.util.List;

public class UserView {
    private final String name;
    private final List<ThingView> things;

    public UserView(String name, List<ThingView> things) {
        this.name = name;
        this.things = things;
    }

    public String getName() {
        return name;
    }

    public List<ThingView> getThings() {
        return things;
    }
}
