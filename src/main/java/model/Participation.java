package model;

public class Participation {
    private Long id;
    private Long party_id;
    private Long guest_id;

    public Participation() {
    }

    public Participation(Long party_id, Long guest_id) {
        this.party_id = party_id;
        this.guest_id = guest_id;
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

    public Long getGuestID() {
        return guest_id;
    }

    public void setGuestID(Long guest_id) {
        this.guest_id = guest_id;
    }
}
