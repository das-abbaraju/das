Ext.define('PICS.view.report.SortToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsorttoolbar'],

    padding: '5',
    items: [
        'Sort on these fields:',
        '->',
        'Download',
        {xtype: 'tbspacer', width: 5 },
        '<a href="#"><img src="js/pics/resources/images/tools/icon_link_pdf.png" /></a>',
        {xtype: 'tbspacer', width: 5 },
        '<a href="#"><img src="js/pics/resources/images/tools/icon_link_excel.png" /></a>',        
        {xtype: 'tbspacer', width: 5 },
        '<a href="#"><img src="js/pics/resources/images/tools/icon_link_print.png" /></a>',
        {xtype: 'tbspacer', width: 10 },
        {xtype: 'button', text: 'Add Column'}
    ]
});