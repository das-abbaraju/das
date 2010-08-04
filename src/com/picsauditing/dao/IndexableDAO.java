package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;

@Transactional
public class IndexableDAO extends PicsDAO {
	public BaseTable find(int id){
		return em.find(BaseTable.class, id);
	}

}
