package com.picsauditing.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

public class MenuComponent implements Serializable, Comparable<MenuComponent> {
	private static final long serialVersionUID = 923449569385839331L;

	private String name;
	private String url;
	private String title;
	private int id = 1;
	private String sortField;

	protected int auditId = 0;

	protected boolean current = false;

	private String cssClass = "";
	private ArrayList<MenuComponent> children = new ArrayList<MenuComponent>();

	public MenuComponent() {
	}

	public MenuComponent(String name) {
		this.name = name;
	}

	public MenuComponent(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public boolean visible() {
		return hasChildren() || hasUrl();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasUrl() {
		if (url == null)
			return false;
		return url.length() > 0;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public ArrayList<MenuComponent> getChildren() {
		return children;
	}

	public MenuComponent addChild(String name) {
		MenuComponent child = new MenuComponent(name);
		this.children.add(child);
		child.setId(this.children.size() + (100 * this.id));
		return child;
	}

	public MenuComponent addChild(String name, String url) {
		MenuComponent child = new MenuComponent(name, url);
		this.children.add(child);
		child.setId(this.children.size() + (100 * this.id));
		return child;
	}

	public boolean isCurrent() {

		if (current)
			return true;

		for (MenuComponent child : children) {

			if (child.isCurrent()) {
				return true;
			}
		}
		return false;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public int getAuditId() {
		return auditId;
	}

	public void setAuditId(int auditId) {
		this.auditId = auditId;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	@Override
	public int compareTo(MenuComponent o) {
		return sortField.compareTo(o.sortField);
	}

	public void sortChildren() {
		Collections.sort(children);
	}

}
