package ru.ilonich.igps.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Access(AccessType.FIELD)
public class Post implements Persistable<Integer> {

    public static final int START_SEQ = 1;

    @Id
    @SequenceGenerator(name = "posts_seq", sequenceName = "posts_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
    @Access(value = AccessType.PROPERTY)
    private Integer id;

    @Column(name = "title", nullable = false)
    @NotBlank
    @Length(min = 2, max = 150)
    @SafeHtml
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "created", updatable = false, insertable = false)
    private long created;

    @Column(name = "content", nullable = false)
    @NotBlank
    @SafeHtml
    private String content;

    @Column(name = "tags", nullable = false)
    @SafeHtml
    private String tags;

    @Column(name = "rating", updatable = false, insertable = false)
    private int rating;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "post")
    @OrderBy("created DESC")
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Set<PostVote> vote;

    public Post() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<PostVote> getVote() {
        return vote;
    }

    public void setVote(Set<PostVote> vote) {
        this.vote = vote;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }

        Post that = (Post) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId() : 0;
    }
}
