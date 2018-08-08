package ru.ibs.gasu.files.repo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ibs.gasu.files.repo.entity.FileVersionEntity;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;

/**
 * Created by EKazeev on 02.03.2017.
 */
@Repository
@Transactional
public class FileVersionDao extends BaseDao<FileVersionEntity, Long> {

    @Autowired
    private JdbcTemplate jdbcTemplateFiles;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void post() {
        setEntityManager(em);
    }

    public FileVersionDao() {
        super(FileVersionEntity.class);
    }

    @Override
    public void remove(FileVersionEntity data) {
        super.remove(data);
    }

    @Override
    public void removeAll(List<FileVersionEntity> data) {
        super.removeAll(data);
    }

    @Override
    public FileVersionEntity persist(FileVersionEntity data) {
        return super.persist(data);
    }

    public int saveStream(long versionId, InputStream stream) throws IOException, SQLException {
        Connection connection = null;
        try {
            String sql = "update files_version set data=? where id=?";
            connection = jdbcTemplateFiles.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            int length = stream.available();
            statement.setBinaryStream(1, stream, stream.available());
            statement.setLong(2, versionId);
            int result = statement.executeUpdate();
            return result;
        } catch (Exception e) {
            logger.error("error in save stream for id=" + versionId, e);
            throw e;
        } finally {
            connection.close();
        }
    }

    public InputStream getStream(long id) {
        Connection connection = null;
        try {
            connection = jdbcTemplateFiles.getDataSource().getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select data from files_version where id = " + id);
            if (rs.next()) {
                return rs.getBinaryStream("data");
            }
        } catch (Exception ex) {
            logger.error("getFileStream error", ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("getStream finally id: " + id + " exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}
