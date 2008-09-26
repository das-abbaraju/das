package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Token;

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

}
