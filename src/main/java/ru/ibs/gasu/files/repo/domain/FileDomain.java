package ru.ibs.gasu.files.repo.domain;

import java.io.Serializable;

/**
 * Created by AGalyan on 27.07.2018.
 */
public class FileDomain implements Serializable {

    private Long fileId;
    private Long fileVersionId;
    private String fileName;
    private String statusMsg;
    private Long fileSize;
    private String mimeType;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getFileVersionId() {
        return fileVersionId;
    }

    public void setFileVersionId(Long fileVersionId) {
        this.fileVersionId = fileVersionId;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
