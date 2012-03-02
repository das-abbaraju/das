Ext.define('PICS.view.report.ColumnSelector', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportcolumnselector'],
    
    layout: {
        type: 'fit'
    },
    
    title: 'Select Report Columns',
    
    height: 500,
    width: 600,

    autoScroll: true,
    
    items: {
        xtype: 'reportcolumnselectorgrid'
    }
});