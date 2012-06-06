Ext.define('PICS.view.report.DataSetGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdatasetgrid'],
    requires: [
        'PICS.view.report.LinkColumn',
        'PICS.view.report.SortToolbar'        
    ],
    store: 'report.DataSets',

    columns: [{
        xtype: 'rownumberer',
        width: 27
    }],
    dockedItems: [{
        xtype: 'reportsorttoolbar',
        dock: 'top'
    }, {
        xtype: 'pagingtoolbar',
        store: 'report.DataSets',
        
        displayInfo: true,
        dock: 'top',
        items: [{
          xtype: 'tbseparator'
        }, {
            xtype: 'combo',
            editable: false,
            name: 'visibleRows',
            store: [
                ['10', '10'],
                ['25', '25'],
                ['50', '50'],
                ['100', '100'],
                ['150', '150'],
                ['200', '200'],
                ['250', '250'],
            ],
            width: 50
        }],
        padding: '0 20 0 0'
    }],
    id: 'dataGrid',
    margin: '0 0 0 10'
});