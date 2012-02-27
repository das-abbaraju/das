package com.picsauditing.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MenuWriterTest {

    @Test
    public void testExportMenuItemToExtJSBasic() throws Exception {
        String json = "{'text':'Test'}";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent("Test");
        
        assertEquals(json, MenuWriter.exportMenuItemToExtJS(menu).toString());
    }
    
    @Test
    public void testExportMenuItemToExtJSAdvanced() throws Exception {
        String json = "{'text':'Test','url':'http://www.google.com'}";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent("Test");
        menu.setUrl("http://www.google.com");
        
        assertEquals(json, MenuWriter.exportMenuItemToExtJS(menu).toString());
    }
    
    @Test
    public void testExportMenuItemToExtJSXType() throws Exception {
        String json = "{'xtype':'tbseparator'}";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent();
        menu.setXtype("tbseparator");
        
        assertEquals(json, MenuWriter.exportMenuItemToExtJS(menu).toString());
    }

    @Test
    public void testExportMenuToExtJSBasic() throws Exception {
        String json = "[{'text':'Test 1'},{'xtype':'tbseparator'},{'text':'Test 2'}]";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent();
        menu.addChild("Test 1");
        menu.addChild("Test 2");
        
        assertEquals(json, MenuWriter.exportMenuToExtJS(menu).toString());
    }

    @Test
    public void testExportMenuToExtJSSubmenu() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test'}],'hideMode':'display'}}]";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        menuItem.addChild("Test");
        
        assertEquals(json, MenuWriter.exportMenuToExtJS(menu).toString());
    }
    
    @Test
    public void testExportMenuToExtJSAdvanced() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test','href':'http://www.google.com'}],'hideMode':'display'}}]";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        MenuComponent menuSubItem = menuItem.addChild("Test");
        menuSubItem.setUrl("http://www.google.com");
        
        assertEquals(json, MenuWriter.exportMenuToExtJS(menu).toString());
    }
    
    @Test
    public void testExportMenuToExtJSFull() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test','href':'http://www.google.com'}],'hideMode':'display'}},{'xtype':'tbseparator'},{'text':'Bottom','menu':{'items':[{'text':'Test 2'}],'hideMode':'display'}}]";
        json = convertQuotes(json);
        
        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        MenuComponent menuSubItem = menuItem.addChild("Test");
        menuSubItem.setUrl("http://www.google.com");
        
        MenuComponent menuItem2 = menu.addChild("Bottom");
        menuItem2.addChild("Test 2");
        
        assertEquals(json, MenuWriter.exportMenuToExtJS(menu).toString());
    }
    
    private static String convertQuotes(String json) {
        return json.replace("'", "\"").replace("/", "\\/");
    }
}
