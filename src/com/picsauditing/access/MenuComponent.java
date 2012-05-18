package com.picsauditing.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MenuComponent implements Serializable, Comparable<MenuComponent> {
    private static final long serialVersionUID = 923449569385839331L;

    private int level = 0;
    
    private String name;
    private String url;
    private String htmlId;
    private String title;
    private String target;
    private int id = 1;
    private String sortField;
    private String xtype;

    protected int auditId = 0;

    protected boolean current = false;

    private String cssClass = "";
    private Map<String, String> dataFields = new TreeMap<String, String>();
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

    public MenuComponent(String name, String url, String htmlId) {
        this.name = name;
        this.url = url;
        this.htmlId = htmlId;
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

    public String getNameIdSafe() {
        return name.toLowerCase().replaceAll(" ", "_");
    }

    public boolean hasUrl() {
        if (url == null)
            return false;
        return url.length() > 0;
    }
    
    public boolean hasHtmlID () {
    	if (htmlId == null) return false;
    	return htmlId.length() > 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public void setHtmlId(String htmlId) {
        this.htmlId = htmlId;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<MenuComponent> getChildren() {
        return children;
    }

    public MenuComponent addChild(String name) {
    	return addChild(name, null, null);
    }
    
    public MenuComponent addChild(String name, String url) {
    	return addChild(name, url, null);
    }

    public MenuComponent addChild(String name, String url, String htmlID) {
        MenuComponent child = new MenuComponent(name, url);
        
        child.setId(this.children.size() + (100 * this.id));
        child.setLevel(this.level + 1);
        child.setHtmlId(htmlID);
        
        this.children.add(child);
        
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

    public Map<String, String> getDataFields() {
        return dataFields;
    }

    public void setDataFields(Map<String, String> dataFields) {
        this.dataFields = dataFields;
    }

    public void addDataField(String key, String value) {
        dataFields.put(key, value);
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }

    public int compareTo(MenuComponent o) {
        return sortField.compareTo(o.sortField);
    }

    public void sortChildren() {
        Collections.sort(children);
    }

}
