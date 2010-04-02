package com.picsauditing.actions;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	static private Set<Trevor> manager = new HashSet<Trevor>();
	private String hash;
	private int id;

	@Override
	public String execute() throws Exception {
		try {
			manager.add(this);
			process();
		} catch (Exception e) {
			throw e;
		} finally {
			manager.remove(this);
		}

		return SUCCESS;
	}

	private void process() {
		System.out.println("managers running now = " + manager.size());
		hash = Strings.hash(manager.size() + "hi" + new Date().toString());
		for (Trevor trevor : manager) {
			System.out.println(" - " + id + ": " + trevor.getHash() + (this.equals(trevor) ? " self" : ""));
		}
		output = "hello world";
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getHash() {
		return hash;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
