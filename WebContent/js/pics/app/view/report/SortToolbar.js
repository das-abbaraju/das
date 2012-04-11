Ext.define('PICS.view.report.SortToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsorttoolbar'],
    
    items: [
        'Sort on these fields:',
        '->',
        'Download',
        { xtype: 'tbspacer' },
        { href: '#', icon: 'js/pics/resources/images/tools/icon_link_pdf.png', target:'_self', scale: 'medium', width: 30},
        { xtype: 'tbspacer' },
        { href: '#', icon: 'js/pics/resources/images/tools/icon_link_excel.png', target:'_self', scale: 'medium', width: 30},
        { xtype: 'tbspacer' },
        { href: '#', icon: 'js/pics/resources/images/tools/icon_link_print.png', target:'_self', scale: 'medium', width: 30},
        { xtype: 'tbspacer' },
        {xtype: 'button', action: 'add-column', icon: 'js/pics/resources/images/dd/drop-add.gif', text: 'Add Column'}
    ]    
});