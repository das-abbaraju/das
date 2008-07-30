package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.Strings;

public class OrderAuditChildren extends PicsActionSupport {
	protected Map<Integer, Integer> list = new HashMap<Integer, Integer>();

	public String execute() {
		String[] listString = (String[]) ActionContext.getContext().getParameters().get("list[]");
		for(int i=0; i < listString.length; i++) {
			
			//System.out.println("listString[" + i + "]" + "   " + listString[i]);
			
			try {
				int id = Integer.parseInt(listString[i]);
				list.put(id, i+1);
			} catch(Exception e){}
		}

		// TODO iterate over children and update num for each child id in "list"
		
		Map<Integer, Integer> tempList = new HashMap<Integer, Integer>();
		
		for(Integer i : list.keySet()) {
			Integer value = list.get(i);
			tempList.put(i, value);
			System.out.print(i);
			System.out.println(" " + value);
		}
		
		

		return SUCCESS;
	}

}
