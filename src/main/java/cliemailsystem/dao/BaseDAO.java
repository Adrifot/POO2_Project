package cliemailsystem.dao;

import cliemailsystem.audit.AuditLogger;

import java.util.List;

public abstract class BaseDAO<T> {
    protected AuditLogger logger = AuditLogger.getInstance();

    public abstract T save(T entity);

    public abstract T findById(int id);

    public abstract List<T> findAll();

    public abstract T update(T entity);

    public abstract void deleteById(int id);

}