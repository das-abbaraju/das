package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;

@SuppressWarnings("rawtypes")
public class DynaBeanListBuilder  {
	String name;
	Map<String, Class> properties = new HashMap<String, Class>();
	BasicDynaClass dynaClass = null;
	BasicDynaBean dynaBean = null;
	List<BasicDynaBean> rows = new ArrayList<BasicDynaBean>();
	
	public DynaBeanListBuilder(String name) {
		this.name = name;
	}

	public void addProperty(String fieldName, Class fieldType) {
		properties.put(fieldName, fieldType);
	}

	public void addRow() {
		if (dynaClass == null) {
			dynaClass = new BasicDynaClass(name, BasicDynaBean.class, buildDynaProperties());
		}
		dynaBean = new BasicDynaBean(dynaClass);
		rows.add(dynaBean);
	}
	
	private DynaProperty[] buildDynaProperties() {
		DynaProperty[] dynaProperties = new DynaProperty[properties.size()];
		int counter = 0;
		for (String propertyName : properties.keySet()) {
			dynaProperties[counter ] = new DynaProperty(propertyName, properties.get(propertyName));
			counter++;
		}
		return dynaProperties;
	}
	
	public void setValue(String fieldName, Object value) {
		dynaBean.set(fieldName, value);
	}
	
	public List<BasicDynaBean> getRows() {
		return rows;
	}
}
