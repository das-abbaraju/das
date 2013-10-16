package com.picsauditing.authentication.dao;

import com.picsauditing.dao.QueryMetaData;
import com.picsauditing.employeeguard.entities.EmailHash;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class EmailHashDAO {
	protected EntityManager em;
	protected QueryMetaData queryMetaData = null;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public QueryMetaData getQueryMetaData() {
		return queryMetaData;
	}

	public void setQueryMetaData(QueryMetaData queryMetaData) {
		this.queryMetaData = queryMetaData;
	}

	@Transactional(propagation = Propagation.NESTED)
	public EmailHash save(EmailHash o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public EmailHash find(int id) {
		return em.find(EmailHash.class, id);
	}

	public List<EmailHash> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from EmailHash a " + where);
		return query.getResultList();
	}

	public boolean hashExists(String hash) {
		return true;
	}

	public boolean hashIsExpired(String hash) {
		return false;
	}

	public EmailHash findByHash(String hash) {
		/*Database database = new Database();
		try {
			return database.select("SELECT * FROM email_hash a JOIN account_employee e ON e.id = a.account_employeeID WHERE a.hash = '" + hash + "'", new RowMapper<EmailHash>() {

				@Override
				public EmailHash mapRow(ResultSet rs, int rowNum) throws SQLException {
					EmailHash emailHash = new EmailHash();
					emailHash.setId(rs.getInt(1));
					emailHash.setHash(rs.getString(2));
					emailHash.setEmailAddress(rs.getString(4));
					emailHash.setCreationDate(rs.getDate(5));
					emailHash.setExpirationDate(rs.getDate(6));

					Employee employee = new Employee();
					employee.setId(rs.getInt(7));
					employee.setAccountId(rs.getInt(8));
					employee.setProfile(null);
					employee.setSlug(rs.getString(11));
					employee.setFirstName(rs.getString(12));
					employee.setLastName(rs.getString(13));
					employee.setPositionName(rs.getString(15));
					employee.setEmail(rs.getString(16));
					employee.setPhone(rs.getString(17));
					employee.setEmailToken(rs.getString(18));
					employee.setCreatedBy(rs.getInt(19));
					employee.setUpdatedBy(rs.getInt(20));
					employee.setDeletedBy(rs.getInt(21));
					employee.setCreatedDate(rs.getDate(22));
					employee.setUpdatedDate(rs.getDate(23));
					employee.setDeletedDate(rs.getDate(24));

					emailHash.setEmployee(employee);

					return emailHash;
				}
			}).get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		Query query = em.createQuery("select a from EmailHash a WHERE a.hash = :hash", EmailHash.class);
		query.setParameter("hash", hash);
		return (EmailHash) query.getSingleResult();

//		return null;
	}

}
