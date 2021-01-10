package view.planning.modelview;

import java.util.List;

public class GuestView {
    private final String name;
    private final List<ContributionView> contributions;

    public GuestView(String name, List<ContributionView> contributions) {
        this.name = name;
        this.contributions = contributions;
    }

    public String getName() {
        return name;
    }

    public List<ContributionView> getContributions() {
        return contributions;
    }
}
