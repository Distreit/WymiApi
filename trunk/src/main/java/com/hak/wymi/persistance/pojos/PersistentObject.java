package com.hak.wymi.persistance.pojos;

import com.hak.wymi.validations.groups.Creation;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.Null;
import java.io.Serializable;

@MappedSuperclass
public class PersistentObject implements Serializable {
    private static final long serialVersionUID = 8585086635066620410L;

    @Version
    @Null(groups = Creation.class)
    private Integer version;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Null(groups = Creation.class)
    private DateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Null(groups = Creation.class)
    private DateTime updated;

    protected PersistentObject() {
        // Disable instantiation.
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }
}
