package com.hak.wymi.persistance.pojos.post;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.trial.Juror;
import com.hak.wymi.persistance.pojos.user.User;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "posttrialjuror")
public class PostTrialJuror extends PersistentObject implements Juror, SecureToSend {
    private static final long serialVersionUID = -5680636463620637739L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postTrialJurorId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "postId")
    private PostTrial postTrial;

    private Boolean violatedSiteRuleVote;

    private Boolean isIllegalVote;

    @Transient
    private DateTime expires;

    public Integer getPostTrialJurorId() {
        return postTrialJurorId;
    }

    public void setPostTrialJurorId(Integer postTrialJurorId) {
        this.postTrialJurorId = postTrialJurorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PostTrial getPostTrial() {
        return postTrial;
    }

    public void setPostTrial(PostTrial postTrial) {
        this.postTrial = postTrial;
    }

    public Boolean getViolatedSiteRuleVote() {
        return violatedSiteRuleVote;
    }

    public void setViolatedSiteRuleVote(Boolean violatedSiteRuleVote) {
        this.violatedSiteRuleVote = violatedSiteRuleVote;
    }

    public Boolean getIsIllegalVote() {
        return isIllegalVote;
    }

    public void setIsIllegalVote(Boolean isIllegalVote) {
        this.isIllegalVote = isIllegalVote;
    }

    @JsonValue
    public Map<String, Object> jsonValue() {
        final Map<String, Object> jsonValue = new HashMap<>();
        jsonValue.put("expires", this.expires);
        jsonValue.put("postTrialJurorId", this.postTrialJurorId);
        jsonValue.put("post", this.postTrial);
        jsonValue.put("violatedSiteRuleVote", this.violatedSiteRuleVote);
        jsonValue.put("isIllegalVote", this.isIllegalVote);
        return jsonValue;
    }

    public void setExpires(DateTime expires) {
        this.expires = expires;
    }
}
