Ext.define('PICS.view.report.modal.column-function.ColumnFunctionList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.reportcolumnfunctionlist',

    border: 0,
    bodyBorder: false,
    cls: 'column_function_list',
    columns: [{
        xtype: 'templatecolumn',
        dataIndex: 'name',
        tpl: '<span class="column-function">{value}</span>',
        flex: 1
    }],
    enableColumnHide: false,    
    hideHeaders: true,
    rowLines: false,
    store: 'report.ColumnFunctions'
});