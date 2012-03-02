Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'button',
            text: 'Search for Columns',
            handler: function () {
                var window = Ext.ComponentQuery.query('reportcolumnselector');
                
                if (!window.length) {
                    var window = Ext.create('PICS.view.report.ColumnSelector');
                } else {
                    window = window[0];
                }
                
                window.show();
            }
        }]
    }]
});