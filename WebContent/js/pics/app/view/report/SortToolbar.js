Ext.define('PICS.view.report.SortToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsorttoolbar'],
    requires: ['PICS.view.report.SortButtons'],

    items: [{
        xtype: 'button',
        cls: 'tooltipIcon',
        icon: 'js/pics/resources/images/tools/question_mark.png',
        iconAlign: 'right',
        scale: 'small',
        text: 'Sort on these fields:',
        tooltip: 'Drag to change sort priority'
    }, {
        xtype: 'sortbuttons'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        text: 'Download'
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'button',
        href: '#',
        icon: 'js/pics/resources/images/tools/icon_link_pdf.png',
        target:'_self',
        scale: 'medium',
        width: 30
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'button',
        name: 'downloadexcel',
        icon: 'js/pics/resources/images/tools/icon_link_excel.png',
        target:'_self',
        scale: 'medium',
        width: 30
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'button',
        href: '#',
        icon: 'js/pics/resources/images/tools/icon_link_print.png',
        target:'_self',
        scale: 'medium',
        width: 30
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'button',
        action: 'add-column',
        icon: 'js/pics/resources/images/dd/drop-add.gif',
        text: 'Add Column'
    }]
});
