package com.picsauditing.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MenuWriterTest {

    @Test
    public void testConvertMenuItemToJSONBasic() throws Exception {
        String json = "{'text':'Test'}";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent("Test");

        assertEquals(json, MenuWriter.convertMenuItemToJSON(menu).toString());
    }

    @Test
    public void testConvertMenuItemToJSONAdvanced() throws Exception {
        String json = "{'text':'Test','url':'http://www.google.com'}";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent("Test");
        menu.setUrl("http://www.google.com");

        assertEquals(json, MenuWriter.convertMenuItemToJSON(menu).toString());
    }

    @Test
    public void testConvertMenuToJSONBasic() throws Exception {
        String json = "[{'text':'Test 1'},{'text':'Test 2'}]";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent();
        menu.addChild("Test 1");
        menu.addChild("Test 2");

        assertEquals(json, MenuWriter.convertMenuToJSON(menu).toString());
    }

    @Test
    public void testConvertMenuToJSONSubmenu() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test'}]}}]";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        menuItem.addChild("Test");

        assertEquals(json, MenuWriter.convertMenuToJSON(menu).toString());
    }

    @Test
    public void testConvertMenuToJSONAdvanced() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test','href':'http://www.google.com'}]}}]";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        MenuComponent menuSubItem = menuItem.addChild("Test");
        menuSubItem.setUrl("http://www.google.com");

        assertEquals(json, MenuWriter.convertMenuToJSON(menu).toString());
    }

    @Test
    public void testConvertMenuToJSONFull() throws Exception {
        String json = "[{'text':'Top','menu':{'items':[{'text':'Test','href':'http://www.google.com'}]}},{'text':'Bottom','menu':{'items':[{'text':'Test 2'}]}}]";
        json = convertQuotes(json);

        MenuComponent menu = new MenuComponent();
        MenuComponent menuItem = menu.addChild("Top");
        MenuComponent menuSubItem = menuItem.addChild("Test");
        menuSubItem.setUrl("http://www.google.com");

        MenuComponent menuItem2 = menu.addChild("Bottom");
        menuItem2.addChild("Test 2");

        assertEquals(json, MenuWriter.convertMenuToJSON(menu).toString());
    }
    
    @Test
    public void testConvertToSimpleJSON () {
    	//String json = "{'id':'parent_id','text':'parent_name','menu':[{'text':'child_name','target':'child_target','menu':[{'text':'subChild_name'}],'href':'child_url'}]}";
    	String json = "{'id':'parent_id','text':'parent_name','menu':{'items':[{'text':'child_name','menu':{'items':[{'text':'subChild_name'}]},'target':'child_target','url':'child_url'}]}}";
    	json = convertQuotes(json);
    	
    	MenuComponent parent = new MenuComponent();
    	parent.setHtmlId("parent_id");
    	parent.setName("parent_name");
    	MenuComponent child = parent.addChild("child_name");
    	child.setUrl("child_url");
    	child.setTarget("child_target");
    	MenuComponent subChild = child.addChild("subChild_name");
    	
    	String convertedJSON = MenuWriter.convertMenuItemToJSON(parent).toJSONString();

    	assertEquals(json, convertedJSON);
    }

    private static String convertQuotes(String json) {
        return json.replace("'", "\"").replace("/", "\\/");
    }
}
