package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("unchecked")
public class AuditTypeCache extends BaseCache 
{
	protected static long lastLoadTime = 0L;
	
	List<AuditType> auditTypes = null;
	Map<Integer, AuditType> byId = null;
	Map<String, AuditType> byName = null;
	
	public AuditTypeCache() {
		if(getContext().getAttribute("auditTypes") != null)
			getContext().removeAttribute("auditTypes");
	}
	
	public AuditTypeCache(AuditTypeDAO dao) 
	{
		auditTypes = (List<AuditType>) getContext().getAttribute("auditTypes");
		byId = (Map<Integer,AuditType>) getContext().getAttribute("auditTypesById");
		byName = (Map<String,AuditType>) getContext().getAttribute("auditTypesByName");
		if( auditTypes == null || (System.currentTimeMillis() - lastLoadTime > 1000 * 60 * 60 * 24 ) )
		{
			auditTypes = dao.findAll();

			byId = new HashMap<Integer, AuditType>();
			byName = new HashMap<String, AuditType>();

			List<AuditType> list = dao.findAll();
			
			for( AuditType obj : list )
			{
				byId.put(obj.getId(), obj);
				byName.put(obj.getAuditName(), obj);
			}
			
			getContext().setAttribute("auditTypes", list);
			getContext().setAttribute("auditTypesById", byId);
			getContext().setAttribute("auditTypesByName", byName);
			
			lastLoadTime = System.currentTimeMillis();
		}
	}

	public List<AuditType> getAuditTypes() {
		return auditTypes;
	}
	public List<AuditType> getAuditTypes(Permissions permissions) {
		List<AuditType> list = new ArrayList<AuditType>();
		for (AuditType aType : auditTypes) {
			if ((aType.getClassType().equals(AuditTypeClass.Audit)  || aType.getClassType().equals(AuditTypeClass.IM)) && permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}
	
	public List<AuditType> getPqfTypes(Permissions permissions) {
		List<AuditType> list = new ArrayList<AuditType>();
		for (AuditType aType : auditTypes) {
			if (aType.getClassType().equals(AuditTypeClass.PQF) && permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}

	public List<AuditType> getPolicyTypes(Permissions permissions) {
		List<AuditType> list = new ArrayList<AuditType>();
		for (AuditType aType : auditTypes) {
			if (aType.getClassType().isPolicy() && permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}
	public void setPolicyTypes(List<AuditType> auditTypes) {
		this.auditTypes = auditTypes;
	}
	
	public void setAuditTypes(List<AuditType> auditTypes) {
		this.auditTypes = auditTypes;
	}
	
	public Map<Integer, AuditType> getById() {
		return byId;
	}
	public void setById(Map<Integer, AuditType> byId) {
		this.byId = byId;
	}
	public Map<String, AuditType> getByName() {
		return byName;
	}
	public void setByName(Map<String, AuditType> byName) {
		this.byName = byName;
	}
}
