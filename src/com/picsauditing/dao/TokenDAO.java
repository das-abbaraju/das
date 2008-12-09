package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class TokenDAO extends PicsDAO {
	public Token save(Token o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Token row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public Token find(int id) {
		Token t = em.find(Token.class, id);
		return t;
	}
	
	public List<Token> findByType(ListType type) {
		String where = "WHERE listType IN ('ALL'";
		if (type != null) {
			where += ",'" + type.toString() + "'";
			if (type.equals(ListType.Audit)
					|| type.equals(ListType.ContractorOperator)
					|| type.equals(ListType.Certificate))
				where += ",'Contractor'";
		}
		where += ")";
		Query query = em.createQuery("FROM Token " + where + " ORDER BY listType, tokenName");
		return query.getResultList();
	}

}
