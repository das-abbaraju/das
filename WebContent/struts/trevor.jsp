<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/pics.css">
<link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css">

<script type="text/javascript" src="js/pics/extjs/bootstrap.js"></script>

<nav id="site_navigation"></nav>

<script>

Ext.scopeResetCSS = true;

Ext.Loader.setConfig({
    enabled: true,
    paths: {
        PICS: './js/pics/app'
    }
});

window.onload = function () {
    Ext.onReady(function ()  {
        var menu = Ext.create('PICS.view.layout.Menu', {
            renderTo: 'site_navigation'
        });

        Ext.EventManager.onWindowResize(function () {
            menu.doLayout();
        });
    })
};

</script>

<script type="text/javascript" id="la_x2s6df8d" src="//chat.picsorganizer.com/scripts/trackjs.php"></script> 
<img src="//chat.picsorganizer.com/scripts/pix.gif" onLoad="LiveAgentTracker.createButton('button1', this);"/>
