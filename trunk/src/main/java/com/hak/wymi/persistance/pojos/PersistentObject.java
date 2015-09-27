package com.hak.wymi.persistance.pojos;

import com.hak.wymi.validations.groups.Creation;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.Null;
import java.util.Date;

@MappedSuperclass
public class PersistentObject {

    @Version
    @Null(groups = Creation.class)
    private Integer version;

    @Null(groups = Creation.class)
    private Date created;

    @Null(groups = Creation.class)
    private Date updated;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
