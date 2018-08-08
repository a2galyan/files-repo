package ru.ibs.gasu.files.repo.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class BaseDao<T, K> {

    Logger logger = LoggerFactory.getLogger(BaseDao.class);

    protected Class<T> clazz;
    protected String tableName;
    protected String idField;

    protected EntityManager em;

    public BaseDao(Class<T> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.idField = findIdField(clazz);
    }

    public BaseDao(Class<T> clazz) {
        this(clazz, clazz.getSimpleName());
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public boolean containsKey(K key) {
        if (key == null)
            return false;
        return (get(key) != null);
    }

    public T persist(T data) {
        em.persist(data);
        return data;
    }

    public K getId(T data) {
        return (K) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(data);
    }

    public T save(T data) {
        T d = em.merge(data);
        return d;
    }

    public T get(K key) {
        return em.find(clazz, key);
    }

    public void remove(T data) {
        em.remove(em.merge(data));
    }

    public void saveAll(List<T> data) {
        for (T d : data)
            save(d);
    }

    public void persistAll(List<T> data) {
        for (T d : data)
            persist(d);
    }

    public List<T> saveAllData(List<T> data) {
        List<T> t = new ArrayList<T>();
        for (T d : data)
            t.add(save(d));
        return t;
    }

    public List<T> persistAllData(List<T> data) {
        List<T> t = new ArrayList<T>();
        for (T d : data) {
            t.add(persist(d));
        }
        return t;
    }

    public void removeAll(List<T> data) {
        for (T d : data)
            remove(d);
    }

    public void removeAll() {
        em.createQuery("delete from " + tableName).executeUpdate();
    }

    public List<T> getAll() {
        Query q = getSelectAllQuery();
        return (List<T>) q.getResultList();
    }

    public List<T> paginate(int offset, int limit) {
        return paginate(offset, limit, null, null);
    }

    public List<T> paginate(int offset, int limit, String sortField, String sortDir) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> c = cb.createQuery(clazz);
        Root<T> r = c.from(clazz);

        if (sortField == null) {
            if (idField != null) {
                c.orderBy(cb.desc(r.get(idField)));
            }
        } else {
            if (sortDir.equalsIgnoreCase("asc"))
                c.orderBy(cb.asc(r.get(sortField)));
            else
                c.orderBy(cb.desc(r.get(sortField)));
        }

        TypedQuery<T> q = em.createQuery(c);
        if (offset > 0) q.setFirstResult(offset);
        if (limit > 0) q.setMaxResults(limit);
        return q.getResultList();
    }

    private String findIdField(Class cls) {
        for (Field field : cls.getDeclaredFields()) {
            String name = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                if (annotations[i].annotationType().equals(Id.class)) {
                    return name;
                }
            }
        }
        return null;
    }

    public boolean isTableEmpty() {
        List<T> results = getSelectAllQuery().setMaxResults(2).getResultList();
        return (results.size() < 1);
    }

    private Query getSelectAllQuery() {
        return em.createQuery(getSelectAllQueryString());
    }

    private String getSelectAllQueryString() {
        return "SELECT data FROM " + tableName + " data";
    }

    public List<T> getMatching(T keyObject) {
        return getMatching(keyObject, 0, 0);
    }

    public List<T> getMatching(T keyObject, int offset, int limit) {
        return getMatching(keyObject, 0, 0, null, null);
    }

    public List<T> getMatching(T keyObject, String sortField, String sortDir) {
        return getMatching(keyObject, 0, 0, sortField, sortDir);
    }

    public List<T> getMatching(T keyObject, int offset, int limit, String sortField, String sortDir) {
        Map<String, Object> importantFields = getImportantFields(keyObject);
        String queryString = getSelectAllQueryString();
        boolean isFirst = true;
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            if (isFirst)
                queryString += " where ";
            else
                queryString += " and ";
            queryString += String.format("data.%s = :%s", field.getKey(), field.getKey());
            isFirst = false;
        }
        if (sortField != null) {
            queryString += " order by " + sortField;
            if (sortDir != null) queryString += " " + sortDir;
        }
        Query q = em.createQuery(queryString);
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            q.setParameter(field.getKey(), field.getValue());
        }
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    public Long getCountMatching(T keyObject) {
        Map<String, Object> importantFields = getImportantFields(keyObject);
        String queryString = "select count(*) from " + tableName + " data ";
        boolean isFirst = true;
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            if (isFirst)
                queryString += " where ";
            else
                queryString += " and ";
            queryString += String.format("data.%s = :%s", field.getKey(), field.getKey());
            isFirst = false;
        }
        Query q = em.createQuery(queryString);
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            q.setParameter(field.getKey(), field.getValue());
        }
        return (Long) q.getSingleResult();
    }

    private Map<String, Object> getImportantFields(T keyObject) {
        Field[] members = clazz.getDeclaredFields();
        Map<String, Object> importantFields = new TreeMap<String, Object>();
        for (Field member : members) {
            try {
                member.setAccessible(true);
                if (!member.getType().isPrimitive()) {
                    Object val = member.get(keyObject);
                    if (val != null)
                        importantFields.put(member.getName(), val);
                }
            } catch (SecurityException e) {
                logger.error("getImportantFields error", e);
            } catch (Exception e) {
                logger.error("getImportantFields error", e);
            }
        }

        return importantFields;
    }

    public List<T> getNotMatching(T keyObject) {
        Field[] members = clazz.getDeclaredFields();
        Map<String, Object> importantFields = getImportantFields(keyObject);
        String queryString = getSelectAllQueryString();
        boolean isFirst = true;
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            if (isFirst)
                queryString += " where ";
            else
                queryString += " and ";
            queryString += String.format("data.%s <> :%s", field.getKey(), field.getKey());
            isFirst = false;
        }
        Query q = em.createQuery(queryString);
        for (Map.Entry<String, Object> field : importantFields.entrySet()) {
            q.setParameter(field.getKey(), field.getValue());
        }
        return q.getResultList();
    }

    public long count() {
        return (Long) em.createQuery("select count(*) from " + tableName + " data").getSingleResult();
    }

}