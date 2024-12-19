package org.atm.dao;

public interface GenericDAO<T> {
    T findById(String id);
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(String id);
}
