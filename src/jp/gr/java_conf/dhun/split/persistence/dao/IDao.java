package jp.gr.java_conf.dhun.split.persistence.dao;

import java.util.List;

import jp.gr.java_conf.dhun.split.persistence.entity.IEntity;

public interface IDao<T extends IEntity<PK>, PK> {

    T findByPk(PK pk);

    List<T> findAll();

    PK insert(T entity);

    int update(T entity);

    int delete(T entity);
}
