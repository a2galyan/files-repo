package ru.ibs.gasu.files.repo.entity;

import javax.persistence.*;

/**
 * Created by AGalyan on 27.07.2018.
 */
@Entity
@Table(name = "FILES_VERSION")
public class FileVersionEntity extends BaseEntity {

    @Column
    private Float fileVersion;

    @Column
    private Long fileSize;

    @Transient
    private byte[] data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    private FileEntity file;

    public Float getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Float fileVersion) {
        this.fileVersion = fileVersion;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }
}
