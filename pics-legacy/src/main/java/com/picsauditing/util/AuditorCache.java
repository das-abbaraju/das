package com.picsauditing.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("unchecked")
public class AuditorCache extends BaseCache 
{
	protected static long lastLoadTime = 0L;
	
	List<User> list = null;
	Map<Integer, User> byId = null;
	Map<String, User> byName = null;

	
	public AuditorCache(UserDAO dao) 
	{
		list = (List<User>) getContext().getAttribute("auditors");
		byId = (Map<Integer,User>) getContext().getAttribute("auditorsById");
		byName = (Map<String,User>) getContext().getAttribute("auditorsByName");
		
		if( list == null || (System.currentTimeMillis() - lastLoadTime > 1000 * 60 * 60 * 24 ) )
		{
			list = dao.findAuditors();

			byId = new HashMap<Integer, User>();
			byName = new HashMap<String, User>();

			for( User obj : list )
			{
				byId.put(obj.getId(), obj);
				byName.put(obj.getUsername(), obj);
			}
			
			getContext().setAttribute("auditors", list);
			getContext().setAttribute("auditorsById", byId);
			getContext().setAttribute("auditorsByName", byName);
			
			lastLoadTime = System.currentTimeMillis();
		}
	}


	public List<User> getList() {
		return list;
	}
	public void setList(List<User> list) {
		this.list = list;
	}
	public Map<Integer, User> getById() {
		return byId;
	}
	public void setById(Map<Integer, User> byId) {
		this.byId = byId;
	}
	public Map<String, User> getByName() {
		return byName;
	}
	public void setByName(Map<String, User> byName) {
		this.byName = byName;
	}
}