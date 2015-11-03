package com.hak.wymi.persistance.pojos.post;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.trial.Juror;
import com.hak.wymi.persistance.pojos.trial.Trial;
import com.hak.wymi.persistance.pojos.trial.TrialState;
import com.hak.wymi.persistance.pojos.user.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "posttrial")
public class PostTrial extends PersistentObject implements Trial {
    private static final long serialVersionUID = -449771238466716661L;

    @Id
    private Integer postId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Post post;

    @ManyToOne
    @JoinColumn(name = "reporterId")
    private User reporter;

    private Integer totalVotes = 0;
    private Integer violatedSiteRuleVotes = 0;
    private Integer isIllegalVotes = 0;

    @OneToMany(mappedBy = "postTrial", fetch = FetchType.LAZY)
    private List<PostTrialJuror> jurors;

    @Enumerated(EnumType.STRING)
    private TrialState state = TrialState.ON_TRIAL;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Integer getViolatedSiteRuleVotes() {
        return violatedSiteRuleVotes;
    }

    public void setViolatedSiteRuleVotes(Integer violatedSiteRulesVotes) {
        this.violatedSiteRuleVotes = violatedSiteRulesVotes;
    }

    public Integer getIsIllegalVotes() {
        return isIllegalVotes;
    }

    public void setIsIllegalVotes(Integer isIllegalVotes) {
        this.isIllegalVotes = isIllegalVotes;
    }

    public TrialState getState() {
        return state;
    }

    public void setState(TrialState state) {
        this.state = state;
    }

    @Override
    public List<? extends Juror> getJurors() {
        return jurors;
    }

    public void setJurors(List<PostTrialJuror> jurors) {
        this.jurors = jurors;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    @JsonValue
    public Map<String, Object> secureTrial() {
        final Map<String, Object> result = new HashMap<>();
        result.put("type", "post");
        result.put("title", post.getTitle());

        if (post.getHref() != null && !"".equals(post.getHref())) {
            result.put("href", post.getHref());
        } else {
            result.put("text", post.getText());
        }
        return result;
    }
}
