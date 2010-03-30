package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FileAttachment;

@Transactional
public class FileDAO extends PicsDAO {

	public FileAttachment find(int id) {
		return em.find(FileAttachment.class, id);
	}

	@SuppressWarnings("unchecked")
	public Map<String, FileAttachment> findByDirectory(String directory) {
		Query query = em.createQuery("SELECT t FROM FileAttachment t WHERE t.directory = ?");
		query.setParameter(1, directory);
		Map<String, FileAttachment> resultMap = new HashMap<String, FileAttachment>();
		List<FileAttachment> results = query.getResultList();
		for (FileAttachment fileAttachment : results) {
			resultMap.put(fileAttachment.getFileName(), fileAttachment);
		}
		return resultMap;
	}
}
