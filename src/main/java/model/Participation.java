package model;

public class Participation {
    private Long id;
    private Long party_id;
    private Long user_id;

    public Participation() {
    }

    public Participation(Long party_id, Long user_id) {
        this.party_id = party_id;
        this.user_id = user_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartyID() {
        return party_id;
    }

    public void setPartyID(Long party_id) {
        this.party_id = party_id;
    }

    public Long getUserID() {
        return user_id;
    }

    public void setUserID(Long user_id) {
        this.user_id = user_id;
    }
}
