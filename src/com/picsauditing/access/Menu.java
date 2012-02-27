package com.picsauditing.access;

import com.picsauditing.actions.PicsActionSupport;

public class Menu extends PicsActionSupport {

    public String execute() throws Exception {
        loadPermissions();
        
        MenuComponent menu = PicsMenu.getMenu(permissions);
        
        jsonArray = MenuWriter.exportMenuToExtJS(menu);
        
        return JSON_ARRAY;
    }
}
