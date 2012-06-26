package com.picsauditing.dao;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.config.BeanDefinition;

@Deprecated
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericHibernateDAO<T extends Serializable> extends AbstractHibernateDAO<T> implements IGenericDAO<T> {
	public GenericHibernateDAO() {
		super();
	}

}