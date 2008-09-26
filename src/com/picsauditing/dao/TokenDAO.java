package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
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
	
	public List<Token> findByType(String type) {
		String where = "";
		if ("Audits".equals(type))
			where = "WHERE type IN ('Audits','Contractors')";
		else if (type != null && type.length() > 0) {
			where = "WHERE type = '" + Utilities.escapeQuotes(type) + "'";
		}
		
		Query query = em.createQuery("FROM Token " + where + " ORDER BY type, tokenName");
		return query.getResultList();
	}

}
