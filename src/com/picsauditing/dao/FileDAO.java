package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.jpa.entities.FileBase;

@Transactional
public class FileDAO extends PicsDAO {

	public FileBase save(FileBase o) {
		if (o.getId() == 0) {
			try {
				FileBase o2 = find(o.getTableType(), o.getForeignKeyID());
				em.remove(o2);
				em.flush();
			} catch (Exception noFileFound) {
				noFileFound.getMessage();
			}
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public FileBase find(PICSFileType fileType, int foreignKeyID) {
		Query query = em.createQuery("SELECT a FROM FileBase a WHERE a.tableType = ? AND a.foreignKeyID = ?");
		query.setParameter(1, fileType);
		query.setParameter(2, foreignKeyID);
		return (FileBase) query.getSingleResult();
	}
	
}
