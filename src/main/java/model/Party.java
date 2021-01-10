package model;

import java.time.LocalDateTime;

public class Party {
    private Long id;
    private String name;
    private LocalDateTime date;

    public Party() {
    }

    public Party(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
