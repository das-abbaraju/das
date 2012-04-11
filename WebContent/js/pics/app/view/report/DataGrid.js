Ext.define('PICS.view.report.DataGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdatagrid'],

    dockedItems: [{
        xtype: 'reportsorttoolbar',
        dock: 'top'
    },{
        xtype: 'pagingtoolbar',
        displayInfo: true,
        dock: 'top',
        padding: '0 20 0 0',
        store: 'report.ReportData'
    },{
        xtype: 'pagingtoolbar',
        displayInfo: true,
        dock: 'bottom',
        padding: '0 20 0 0',
        store: 'report.ReportData'
    }],

    columns: [{
            header: 'Column',
            dataIndex: 'column'
        },{ 
            header: 'Email',
            dataIndex: 'email'
        },{ 
            header: 'Value', 
            dataIndex: 'value'
        }],
    margin: '0 0 0 10',    
    store: 'report.ReportData'
});