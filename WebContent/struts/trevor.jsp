<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/pics.css">
<link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css">
        
<script type="text/javascript" src="js/pics/extjs/bootstrap.js"></script>

<nav id="site_navigation" style="margin-top: 10px;"></nav>

<script>

Ext.scopeResetCSS = true;

Ext.Loader.setConfig({
    enabled: true,
    paths: {
        PICS: './js/pics/app'
    }
});

Ext.onReady(function () {
    var menu = Ext.create('PICS.view.layout.Menu', {
        renderTo: 'site_navigation'
    });
    
    Ext.EventManager.onWindowResize(function () {
        menu.doLayout();
    });
});
</script>