package ru.ibs.gasu.files.repo.ws.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ibs.gasu.files.repo.domain.FileDomain;
import ru.ibs.gasu.files.repo.service.FileService;
import ru.ibs.gasu.files.repo.ws.FileWebService;

import javax.activation.DataHandler;
import javax.jws.WebService;
import java.io.InputStream;

/**
 * Created by AGalyan on 27.07.2018.
 */
@WebService(endpointInterface = "ru.ibs.gasu.files.repo.ws.FileWebService", serviceName = "FieSoapService")
public class FileWebServiceImpl implements FileWebService {

    @Autowired
    private FileService fileService;

    @Override
    public FileDomain uploadFile(DataHandler fileDataHandler, String fileName) {
        return fileService.uploadFile(fileDataHandler, fileName);
    }

    @Override
    public FileDomain getFileVersion(Long fileVersionId) {
        return fileService.getFileVersion(fileVersionId);
    }

    @Override
    public DataHandler getFileStream(Long fileVersionId) {
        return fileService.getFileStream(fileVersionId);
    }
}
