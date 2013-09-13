package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.Token;

@SuppressWarnings("unchecked")
public class TokenDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public Token save(Token o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
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
			if (type.equals(ListType.Audit) || type.equals(ListType.ContractorOperator))
				where += ",'Contractor'";
		}
		where += ")";
		Query query = em.createQuery("FROM Token " + where + " ORDER BY listType, tokenName");
		return query.getResultList();
	}

}
