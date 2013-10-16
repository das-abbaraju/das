package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ProjectAction extends PicsRestActionSupport {

	/* pages */

	public String index() {
		return LIST;
	}

	public String show() {
		return SHOW;
	}

	public String create() {
		return CREATE;
	}

	public String edit() {
		return EDIT;
	}

	/* other methods */

	public String insert() {
		return NONE;
	}

	public String update() {
		return NONE;
	}

	public String delete() {
		return NONE;
	}

	/* getters + setters */

	public ArrayList<Map<String, Object>> getProjects() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("id", "1");
		map1.put("name", "THUMS Island");
		map1.put("location", "Long Beach, CA");
		map1.put("start_date", "2014-01-01");
		map1.put("end_date", "2015-02-02");

		list.add(map1);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", "2");
		map2.put("name", "Tesoro");
		map2.put("location", "Long Beach, CA");
		map2.put("start_date", "2013-05-01");
		map2.put("end_date", "2013-08-03");

		list.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("id", "3");
		map3.put("name", "BP Carson Refinery");
		map3.put("location", "Carson, CA");
		map3.put("start_date", "2011-12-16");
		map3.put("end_date", "2013-04-22");

		list.add(map3);

		return list;
	}
}
