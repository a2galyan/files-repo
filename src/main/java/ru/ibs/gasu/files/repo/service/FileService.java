package ru.ibs.gasu.files.repo.service;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.gasu.files.repo.dao.FileDao;
import ru.ibs.gasu.files.repo.dao.FileVersionDao;
import ru.ibs.gasu.files.repo.domain.FileDomain;
import ru.ibs.gasu.files.repo.entity.FileEntity;
import ru.ibs.gasu.files.repo.entity.FileVersionEntity;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by AGalyan on 27.07.2018.
 */
@Service
public class FileService {

    private static final Long PARENT_FOLDER_ID = 17556202L;

    @Autowired
    private FileDao fileDao;
    @Autowired
    private FileVersionDao fileVersionDao;

    public FileDomain getFileVersion(Long fileVersionId) {
        FileDomain fileDomain = new FileDomain();
        FileVersionEntity fileVersionEntity = fileVersionDao.get(fileVersionId);
        if (fileVersionEntity != null) {
            fileDomain.setFileVersionId(fileVersionEntity.getId());
            fileDomain.setFileSize(fileVersionEntity.getFileSize());
            if (fileVersionEntity.getFile() != null) {
                fileDomain.setFileId(fileVersionEntity.getFile().getId());
                fileDomain.setFileName(fileVersionEntity.getFile().getFileName());
                fileDomain.setMimeType(fileVersionEntity.getFile().getMimeType());
            }
        }
        return fileDomain;
    }

    public DataHandler getFileStream(Long fileVersionId) {
        return new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return fileVersionDao.getStream(fileVersionId);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        });
    }

    public FileDomain uploadFile(DataHandler fileDataHandler, String fileName) {
        FileDomain fileDomain = new FileDomain();
        try {
            FileVersionEntity fileVersionEntity = uploadVersion(fileDataHandler, fileName);
            fileDomain.setFileVersionId(fileVersionEntity.getId());
            if (fileVersionEntity.getFile() != null) {
                fileDomain.setFileId(fileVersionEntity.getFile().getId());
                fileDomain.setFileName(fileVersionEntity.getFile().getFileName());
            }
        } catch (Exception ex) {
            fileDomain.setFileId(null);
            fileDomain.setFileVersionId(null);
            fileDomain.setStatusMsg("Failed upload file to repo.");
        }
        return fileDomain;
    }

    private FileVersionEntity uploadVersion(DataHandler fileDataHandler, String fileName) throws IOException, SQLException {

        String contentType = fileDataHandler.getContentType();
        InputStream stream = fileDataHandler.getInputStream();

        UniversalDetector detector = new UniversalDetector(null);

        ByteArrayInputStream is = new ByteArrayInputStream(fileName.getBytes());
        int nRead;
        byte[] buf = new byte[4096];
        while ((nRead = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nRead);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            encoding = "UTF-8";
        }

        detector.reset();
        //fileName = new String(fileName.getBytes(), encoding);
        String[] fileType = fileName.split("\\.");

        FileEntity toFind = new FileEntity();
        toFind.setParent(fileDao.get(PARENT_FOLDER_ID));
        toFind.setFileName(fileName);
        List<FileEntity> files = fileDao.getMatching(toFind);
        if (files.size() > 0) {
            toFind = files.get(0);
        } else {
            toFind.setFileName(fileName);
            toFind.setMimeType(contentType);
            int lastIndex = fileName.lastIndexOf('.');
            String name = fileName;
            if (lastIndex > 0) {
                name = fileName.substring(0, lastIndex);
                toFind.setExtension(fileName.substring(lastIndex + 1));
            }
            toFind.setTitle(name);
            toFind.setDescription(name);
            toFind = fileDao.persist(toFind);
        }

        FileVersionEntity version = new FileVersionEntity();
        version.setFile(toFind);
        List<FileVersionEntity> versions = fileVersionDao.getMatching(version, "fileVersion", "desc");
        Float currentVersion = new Float(1);
        if (versions.size() > 0) {
            currentVersion = versions.get(0).getFileVersion() + 0.1f;
        }

        version = new FileVersionEntity();
        version.setFileVersion(currentVersion);
        version.setFileSize(new Long(stream.available()));
        version.setFile(toFind);
        version = fileVersionDao.persist(version);
        fileVersionDao.saveStream(version.getId(), stream);
        return version;
    }
}
