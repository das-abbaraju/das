package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.util.Strings;

@Transactional
public class AuditCategoryDataDAO extends PicsDAO {
	public AuditCatData save(AuditCatData o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditCatData row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditCatData find(int id) {
		AuditCatData a = em.find(AuditCatData.class, id);
		return a;
	}

	@SuppressWarnings("unchecked")
	public List<AuditCatData> findAllAuditCatData(int auditID, int catID) {
		String selectQuery = "FROM AuditCatData d " + "WHERE d.category.id=" + catID + " AND d.audit.id=" + auditID;
		Query query = em.createQuery(selectQuery);
		return query.getResultList();
	}

	/**
	 * Get a list of NCMS categories and their statuses for a given contractor.
	 */
	@SuppressWarnings("unchecked")
	public List<NcmsCategory> findNcmsCategories(int conID) throws Exception {
		List<NcmsCategory> categories = new ArrayList<NcmsCategory>();
		if (conID == 0)
			return categories;

		StringBuffer sql = new StringBuffer("SELECT ");
		for (String columnName : NcmsCategory.columns)
			sql.append("`").append(columnName).append("`,");
		sql.append(" 1 FROM NCMS_Desktop WHERE conID=" + conID);

		Query query = em.createNativeQuery(sql.toString());

		List<Object> results = query.getResultList();

		for (Object o : results) {
			Object[] data = (Object[]) o;
			int i = 0;
			for (String columnName : NcmsCategory.columns) {
				NcmsCategory cat = new NcmsCategory(columnName, data[i].toString());
				categories.add(cat);
				i++;
			}
		}
		return categories;
	}

}
