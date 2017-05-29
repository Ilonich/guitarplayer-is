package ru.ilonich.igps.model;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "comments_votes")
public class CommentVote implements Serializable {

    @EmbeddedId
    private CommentVotePK id;

    @MapsId("comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    private Comment comment;

    @MapsId("voter")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    private User voter;

    @Column(name = "liked", nullable = false)
    private boolean like;

    public CommentVote() {
    }

    public CommentVote(Comment comment, User voter, boolean like) {
        this.comment = comment;
        this.voter = voter;
        this.like = like;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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

    public CommentVotePK getId() {
        return id;
    }

    public void setId(CommentVotePK id) {
        this.id = id;
    }
}
