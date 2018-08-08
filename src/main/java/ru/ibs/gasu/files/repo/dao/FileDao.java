package ru.ibs.gasu.files.repo.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ibs.gasu.files.repo.entity.FileEntity;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by EKazeev on 02.03.2017.
 */
@Repository
@Transactional
public class FileDao extends BaseDao<FileEntity, Long> {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void post() {
        setEntityManager(em);
    }

    public FileDao() {
        super(FileEntity.class);
    }

    public List<FileEntity> getFolders(FileEntity parent) {
        Query q;
        if (parent == null || parent.getId() == 0) {
            q = em.createQuery("select data from FileEntity data where parent is null and isFolder = true order by title");
        } else {
            q = em.createQuery("select data from FileEntity data where parent = :parent  and isFolder = true order by title");
            q.setParameter("parent", parent);
        }
        List<FileEntity> res = q.getResultList();
        return res;
    }

    public List<FileEntity> getFiles(FileEntity parent) {
        Query q;
        if (parent == null || parent.getId() == 0) {
            q = em.createQuery("select data from FileEntity data where parent is null order by isFolder desc, title asc");
        } else {
            q = em.createQuery("select data from FileEntity data where parent = :parent order by isFolder desc, title asc ");
            q.setParameter("parent", parent);
        }
        List<FileEntity> res = q.getResultList();
        return res;
    }

    public List<FileEntity> getFilesOnly(FileEntity parent) {
        Query q;
        if (parent == null || parent.getId() == 0) {
            q = em.createQuery("select data from FileEntity data where parent is null and isFolder = false order by isFolder desc, title asc");
        } else {
            q = em.createQuery("select data from FileEntity data where parent = :parent and isFolder = false order by isFolder desc, title asc ");
            q.setParameter("parent", parent);
        }
        List<FileEntity> res = q.getResultList();
        return res;
    }

    public Long getVersionId(long fileId) {
        Object res = em.createNativeQuery("select id from files_version where file_id = "
                + fileId + " and fileversion = (select max(fileversion) from files_version where file_id = "
                + fileId + ")").getSingleResult();

        return ((BigDecimal) res).longValue();
    }

    @Override
    public FileEntity persist(FileEntity data) {
        return super.persist(data);
    }

    @Override
    public FileEntity save(FileEntity data) {
        return super.save(data);
    }

    @Override
    public void remove(FileEntity data) {
        super.remove(data);
    }


    public List<FileEntity> getFilesPlain(FileEntity parent) {
        List<FileEntity> res = null;
        Query q;
        if (parent != null) {
            q = em.createQuery("select data from FileEntity data where parent = :parent order by title asc ");
            q.setParameter("parent", parent);
            res = q.getResultList();
        }
        return res;
    }
}
