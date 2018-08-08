package ru.ibs.gasu.files.repo.entity;

import javax.persistence.*;

/**
 * Created by AGalyan on 27.07.2018.
 */
@Entity
@Table(name = "FILES")
public class FileEntity extends BaseEntity {

    @Column
    private String title;

    @Column(length = 2000)
    private String description;

    @Column
    private boolean isFolder;

    @Column
    private String fileName;

    @Column
    private String mimeType;

    @Column
    private String extension;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private FileEntity parent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public FileEntity getParent() {
        return parent;
    }

    public void setParent(FileEntity parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return title + " (" + (isFolder ? "папка" : "файл") + ")";
    }
}
