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
				if (o2.getModifiedDate().after(o.getModifiedDate())) {
					// Don't ever overwrite a newer file with an older file
					o = o2;
					return o;
				}
				if (o2.getFileHash().equals(o.getFileHash())) {
					// Don't ever overwrite the same file
					o = o2;
					return o;
				}
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
