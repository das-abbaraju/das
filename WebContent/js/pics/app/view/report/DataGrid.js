Ext.define('PICS.view.report.DataGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdatagrid'],

    columns: [{
        xtype: 'rownumberer',
        width: 27
    }],
    dockedItems: [{
        xtype: 'reportsorttoolbar',
        dock: 'top'
    },{
        xtype: 'pagingtoolbar',
        displayInfo: true,
        dock: 'top',
        items: [{
          xtype: 'tbseparator'
        },{
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
        padding: '0 20 0 0',
        store: 'report.ReportData'
    },{
        xtype: 'pagingtoolbar',
        cls: 'ext-no-bottom-border',
        displayInfo: true,
        dock: 'bottom',
        items: [{
          xtype: 'tbseparator'
        },{
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
        padding: '0 20 0 0',
        store: 'report.ReportData'
    }],
    id: 'dataGrid',
    margin: '0 0 0 10',
    store: 'report.ReportData'
});