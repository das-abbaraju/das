Ext.define('PICS.view.layout.Header', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutheader'],

    height: 50,
    id: 'layoutHeader',
    items: [{
        xtype: 'tbtext',
        text: '<a href="Home.action"><img src="images/logo_sm.png" /></a>',
        width: 100,
        height: 35
    },{
        xtype: 'tbfill'
    },{
        xtype: 'tbtext',
        text: '<a href="Home.action"><img src="images/*.png" /></a>'
    },{
        xtype: 'tbspacer',
        width: 15
    },{
        xtype: 'tbtext',
        cls: 'toolbarBold',
        text: 'Welcome, '
    },{
        xtype: 'tbtext',
        cls: 'toolbarBold',
        text: '<a href="ProfileEdit.action">Joel</a>'
    },{
        xtype: 'tbseparator'
    },{
        xtype: 'tbtext',
        text: '<a href="Home.action">Home</a>'
    },{
        xtype: 'tbseparator'
    },{
        xtype: 'tbtext',
        text: '<a href="http://www.picsauditing.com/">PICS</a>'
    },{
        xtype: 'tbseparator'
    },{
        xtype: 'tbtext',
        text: '<a href="Login.action?button=logout">Logout</a>'
    }],
    padding: '0 20'
});