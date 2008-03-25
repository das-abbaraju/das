package com.picsauditing.dao;

import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.jpa.entities.ContractorInfo;
import java.util.List;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorInfoDAO extends PicsDAO {
	public ContractorInfo save(ContractorInfo o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		ContractorInfo row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public ContractorInfo find(int id) {
        return em.find(ContractorInfo.class, id);
    }

    public List<ContractorInfo> findWhere(String where) {
    	if (where == null) where = "";
    	if (where.length() > 0) where = "WHERE " + where;
        Query query = em.createQuery("select a from Account a "+where+" order by a.name");
        return query.getResultList();
    }
}
