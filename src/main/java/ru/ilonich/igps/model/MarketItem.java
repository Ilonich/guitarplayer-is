package ru.ilonich.igps.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.SafeHtml;
import ru.ilonich.igps.model.enumerations.Location;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "market_items")
public class MarketItem extends ItemsBaseEntity {

    @Column(name = "cost")
    @Digits(fraction = 0, integer = 7)
    @NotNull
    @Range(min = 100, max = 9999999)
    private int cost;

    @Column(name = "contacts", nullable = false)
    @NotEmpty
    @Length(min = 2, max = 500)
    @SafeHtml
    private String contacts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    protected Location location;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public MarketItem() {
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

}
