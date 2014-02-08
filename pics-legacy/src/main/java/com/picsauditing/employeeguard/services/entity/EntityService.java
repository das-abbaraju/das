package com.picsauditing.employeeguard.services.entity;

public interface EntityService<E, ID> {

    E find(ID id);

    E save(E entity);

    E update(E entity);

    void delete(E entity);

}
