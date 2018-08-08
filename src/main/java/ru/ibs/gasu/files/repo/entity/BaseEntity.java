package ru.ibs.gasu.files.repo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by AGalyan on 27.07.2018.
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "idSeq", sequenceName = "ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq")
    private long id;

    @Column(name = "CREATE_DATE")
    private Date createDate = new Date();

    @Column(name = "UPDATE_DATE")
    private Date updateDate = new Date();

    @Column
    private Boolean obsolete = false;

    @Column(name = "CREATE_USER_ID")
    private Long createUserId;

    @Column(name = "UPDATE_USER_ID")
    private Long updateUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getObsolete() {
        return obsolete;
    }

    public void setObsolete(Boolean obsolete) {
        this.obsolete = obsolete;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity baseEntity = (BaseEntity) o;

        return getId() == baseEntity.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

}
