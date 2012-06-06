package com.picsauditing.access;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class MenuWriter {

    public static JSONArray exportMenuToExtJS(MenuComponent menu) {
        List<MenuComponent> menuItems = menu.getChildren();

        JSONArray jsonArray = new JSONArray();

        int menuSize = menuItems.size();

        for (int i = 0; i < menuSize; i++) {
            MenuComponent menuItem = menuItems.get(i);

            jsonArray.add(convertMenuComponentToExtJS(menuItem));
        }

        return jsonArray;
    }

    public static JSONObject convertMenuComponentToExtJS(MenuComponent menu) {
        JSONObject json = new JSONObject();

        if (Strings.isEmpty(menu.getXtype())) {
            if (!Strings.isEmpty(menu.getName())) {
                json.put("text", menu.getName());
            }

            if (!Strings.isEmpty(menu.getUrl())) {
                if (menu.getLevel() > 1) {
                    json.put("href", menu.getUrl());
                } else {
                    json.put("url", menu.getUrl());
                }
            }

            if (menu.getChildren().size() > 0) {
                JSONObject subMenu = new JSONObject();

                subMenu.put("items", exportMenuToExtJS(menu));
                subMenu.put("hideMode", "display");

                json.put("menu", subMenu);
            }
        } else {
            json.put("xtype", menu.getXtype());

            // TODO get rid of this kludge
        	if ("searchTerm".equals(menu.getName())) {
        		json.put("name", menu.getName());
        		json.put("emptyText", "enter search term");
        	}
        }

        return json;
    }
}
