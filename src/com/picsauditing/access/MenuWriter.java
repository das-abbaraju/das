package com.picsauditing.access;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class MenuWriter {

    public static JSONArray convertMenuToJSON(MenuComponent menu) {
        JSONArray jsonArray = new JSONArray();

        for (MenuComponent menuItem : menu.getChildren()) {
            jsonArray.add(convertMenuItemToJSON(menuItem));
        }

        return jsonArray;
    }

    public static JSONObject convertMenuItemToJSON(MenuComponent menuItem) {
        JSONObject json = new JSONObject();

        if (menuItem.hasName()) {
            json.put("text", menuItem.getName());
        }

        if (menuItem.hasHtmlID()) {
        	json.put("id", menuItem.getHtmlId());
        }

    	if (menuItem.hasTarget()) {
    		json.put("target", menuItem.getTarget());
    	}

        if (menuItem.hasUrl()) {
        	String tag = (menuItem.getLevel() > 1) ? "href" : "url";
            json.put(tag, menuItem.getUrl());
        }

        if (menuItem.hasChildren()) {
            JSONObject subMenu = new JSONObject();

            subMenu.put("items", convertMenuToJSON(menuItem));

            json.put("menu", subMenu);
        }

        return json;
    }
}
