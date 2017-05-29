package ru.ilonich.igps.model;

import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CommentVotePK implements Serializable {

    private Integer voter;

    private Integer comment;

    public CommentVotePK(int user, int comment) {
        this.voter = user;
        this.comment = comment;
    }

    public CommentVotePK() {
    }

    @Override
    public int hashCode() {
        return voter + comment;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !this.getClass().equals(Hibernate.getClass(obj))) {
            return false;
        }

        CommentVotePK that = (CommentVotePK) obj;

        return this.getComment() == that.getComment() && this.getVoter() == that.getVoter();
    }

    public int getVoter() {
        return voter;
    }

    public void setVoter(int voter) {
        this.voter = voter;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }
}
