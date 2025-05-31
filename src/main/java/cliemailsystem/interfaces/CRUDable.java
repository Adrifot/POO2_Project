package cliemailsystem.interfaces;

import java.util.List;

public interface CRUDable<T> {
    T save(T entity);
    T findById(int id);
    List<T> findAll();
    T update(T entity);
    void deleteById(int id);
}