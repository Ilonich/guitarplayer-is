package ru.ilonich.igps.model;

import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PostVotePK implements Serializable {

    private Integer voter;

    private Integer post;

    public PostVotePK(int voter, int post) {
        this.voter = voter;
        this.post = post;
    }

    public PostVotePK() {
    }

    @Override
    public int hashCode() {
        return voter + post;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !this.getClass().equals(Hibernate.getClass(obj))) {
            return false;
        }

        PostVotePK that = (PostVotePK) obj;

        return this.getPost() == that.getPost() && this.getVoter() == that.getVoter();
    }

    public int getVoter() {
        return voter;
    }

    public void setVoter(int voter) {
        this.voter = voter;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
