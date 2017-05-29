package ru.ilonich.igps.model;


import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.*;
import org.springframework.data.domain.Persistable;
import org.springframework.util.CollectionUtils;
import ru.ilonich.igps.model.enumerations.Authority;
import ru.ilonich.igps.model.enumerations.Location;
import ru.ilonich.igps.model.enumerations.Role;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Persistable<Integer> {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Access(value = AccessType.PROPERTY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @SafeHtml
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Пароль должен содержать не менее 5 символов")
    @Length(min = 5, max = 100, message = "Пароль должен содержать не менее 5 символов")
    @SafeHtml
    private String password;

    @Column(name = "enabled", insertable = false)
    private boolean enabled;

    @Column(name = "decent", insertable = false)
    private boolean decent;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Role> roles;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    @NotBlank
    @Length(max = 30)
    @SafeHtml
    private String name;

    @Column(name = "bio")
    @Length(max = 1000)
    @SafeHtml
    private String userBio;

    //Вся логика с рэйтингом в бд, на триггерах
    @Column(name = "rating", updatable = false, insertable = false)
    private int rating;

    @Column(name = "registered", updatable = false, insertable = false)
    private long registered;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    private Location location;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Comment> comments;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "members")
    private List<Dialog> dialogs;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Item> items;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "owner")
    private List<MarketItem> marketItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voter")
    private Set<PostVote> votesForPosts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voter")
    private Set<CommentVote> votesForComments;

    public User(int id, String email, String password,
                boolean enabled, boolean decent,
                String name, String userBio,
                int rating, long registered,
                Authority authority, Location location,
                Role role, Role... roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.decent = decent;
        this.name = name;
        this.userBio = userBio;
        this.rating = rating;
        this.registered = registered;
        this.authority = authority;
        this.location = location;
        this.roles = EnumSet.of(role, roles);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return (getId() == null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDecent() {
        return decent;
    }

    public void setDecent(boolean decent) {
        this.decent = decent;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? Collections.emptySet() : EnumSet.copyOf(roles);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getRegistered() {
        return registered;
    }

    public void setRegistered(long registered) {
        this.registered = registered;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public List<Item> getItems() {
        return items;
    }

    public Set<PostVote> getVotesForPosts() {
        return votesForPosts;
    }

    public Set<CommentVote> getVotesForComments() {
        return votesForComments;
    }

    public List<MarketItem> getMarketItems() {
        return marketItems;
    }


    public User() {
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }

        User that = (User) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId() : 0;
    }

/*
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "comments", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @OrderBy("created DESC")
    @BatchSize(size=5)
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "posts", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @OrderBy("created DESC")
    @BatchSize(size=3)
    private List<PostPreview> postPreviews;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "items", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @OrderBy("published DESC")
    @BatchSize(size = 2)
    private List<Item> items;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "market_items", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @OrderBy("published DESC")
    @BatchSize(size = 2)
    private List<MarketItem> marketItemsitems;
*/

}
