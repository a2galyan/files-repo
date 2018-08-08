package ru.ibs.gasu.files.repo.ws;

import ru.ibs.gasu.files.repo.domain.FileDomain;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.InputStream;

/**
 * Created by AGalyan on 27.07.2018.
 */
@WebService
public interface FileWebService {

    @WebMethod
    FileDomain uploadFile(@WebParam(name = "file") DataHandler file, String fileName);

    @WebMethod
    FileDomain getFileVersion(Long fileVersionId);

    DataHandler getFileStream(Long fileVersionId);
}
