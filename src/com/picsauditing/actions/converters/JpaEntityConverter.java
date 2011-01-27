package com.picsauditing.actions.converters;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class JpaEntityConverter extends StrutsTypeConverter {

	protected EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		Object response = null;

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			try {
				response = em.find(toClass, Integer.parseInt(values[0]));
			} catch (Exception tryStringId) {
				try {
					response = em.find(toClass, values[0]);
				} catch (NumberFormatException notFound) {
					response = null;
				}
			}
		} else {
			response = null;
		}
		return response;
	}

	@Override
	public String convertToString(Map context, Object o) {
		String response = null;

		if (o instanceof BaseTable) {
			response = "" + ((BaseTable) o).getId();
		}

		return response.toString();
	}

}
