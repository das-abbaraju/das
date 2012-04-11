Ext.define('PICS.view.layout.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.layoutheader'],

    border: false,
    defaults: {
        border: false
    },
    id: 'reportHeader',
    height: 50,
    items: [{
        xtype: 'toolbar',
        id: 'headerToolbar',
        height: 50,
        items: [
            '<a href="Home.action"><img src="images/logo_sm.png"  /></a>',
            '->',
            '<a href="Home.action"><img src="images/*.png"  /></a>',   
            {xtype: 'tbspacer', width: 15 },
            {xtype: 'tbtext', text: 'Welcome, ', cls: 'toolbarBold'},            
            {xtype: 'tbtext', text: '<a href="ProfileEdit.action">Joel</a>', cls: 'toolbarBold'},
            '-',
            '<a href="Home.action">Home</a>',
            '-',
            '<a href="http://www.picsauditing.com/">PICS</a>',
            '-',
            '<a href="Login.action?button=logout">Logout</a>'
        ]
    }]
});