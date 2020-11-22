package view.planning.modelview;

import java.util.Objects;

public class ContributionView {
    private final String name;

    public ContributionView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributionView contributionView = (ContributionView) o;
        return Objects.equals(name, contributionView.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
