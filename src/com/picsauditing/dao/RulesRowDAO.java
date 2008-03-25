package com.picsauditing.dao;

import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.entities.RulesRow;
import java.util.List;

@Transactional
public class RulesRowDAO extends PicsDAO {
	public RulesRow save(RulesRow o) {
		if (o.getRowID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		RulesRow row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	@SuppressWarnings("unchecked")
    public List<RulesRow> findAll() {
        Query query = em.createQuery("select r FROM RulesRow r");
        return query.getResultList();
    }
    public List<RulesRow> findByTable(String tableName) {
        Query query = em.createQuery("select r FROM RulesRow r WHERE tableName = '"+tableName+"'");
        return query.getResultList();
    }
	
	public RulesRow find(int id) {
        return em.find(RulesRow.class, id);
    }

}
