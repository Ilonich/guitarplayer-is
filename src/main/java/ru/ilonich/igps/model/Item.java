package ru.ilonich.igps.model;

import javax.persistence.*;

@Entity
@Table(name = "items")
public class Item extends ItemsBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Item() {
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
