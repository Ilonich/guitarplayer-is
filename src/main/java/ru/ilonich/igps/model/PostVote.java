package ru.ilonich.igps.model;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "posts_votes")
public class PostVote implements Serializable {

    @EmbeddedId
    private PostVotePK id;

    @MapsId("post")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    private Post post;

    @MapsId("voter")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    private User voter;

    @Column(name = "liked", nullable = false)
    private boolean like;

    public PostVote() {
    }

    public PostVote(Post post, User voter, boolean like) {
        this.post = post;
        this.voter = voter;
        this.like = like;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public PostVotePK getId() {
        return id;
    }

    public void setId(PostVotePK id) {
        this.id = id;
    }
}
