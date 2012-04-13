Ext.define('PICS.view.report.DataGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdatagrid'],
    requires: [
        'PICS.view.report.LinkColumn'
    ],
    store: 'report.ReportData'

    columns: [{
        xtype: 'rownumberer',
        width: 27
    }],
    dockedItems: [{
        xtype: 'reportsorttoolbar',
        dock: 'top'
    }, {
        xtype: 'pagingtoolbar',
        store: 'report.ReportData',
        
        displayInfo: true,
        dock: 'top',
        items: [{
          xtype: 'tbseparator'
        }, {
            xtype: 'combo',
            editable: false,
            name: 'itemsperpage',
            store: [
                ['10', '10'],
                ['25', '25'],
                ['50', '50'],
                ['100', '100'],
                ['150', '150'],
                ['200', '200'],
                ['250', '250'],
            ],
            value: '10',
            width: 50
        }],
        padding: '0 20 0 0'
    }, {
        xtype: 'pagingtoolbar',
        store: 'report.ReportData',
        
        cls: 'ext-no-bottom-border',
        displayInfo: true,
        dock: 'bottom',
        items: [{
          xtype: 'tbseparator'
        }, {
            xtype: 'combo',
            editable: false,
            name: 'itemsperpage',
            store: [
                ['10', '10'],
                ['25', '25'],
                ['50', '50'],
                ['100', '100'],
                ['150', '150'],
                ['200', '200'],
                ['250', '250'],
            ],
            value: '10',
            width: 50
        }],
        padding: '0 20 0 0'
    }],
    id: 'dataGrid',
    margin: '0 0 0 10'
});