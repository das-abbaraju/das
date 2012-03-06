Ext.define('PICS.view.report.DataGrid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportdatagrid'],
    store: 'report.ReportData',
    
    enableColumnHide: false,
    id: 'dataGrid',
    sortableColumns:  false,
    title: 'Grid',
    
    initComponent: function () {
        this.columns = [{"width":27,"xtype":"rownumberer"},{"text":"Account Name","width":180,"dataIndex":"accountName"},{"text":"Account Status","dataIndex":"accountStatus"},{"text":"Account ID","dataIndex":"accountID"}];
        
        this.dockedItems = [{
            xtype: 'pagingtoolbar',
            store: 'report.ReportData',
            dock: 'top'
        }, {
            xtype: 'pagingtoolbar',
            store: 'report.ReportData',
            dock: 'bottom'
        }];
        
        this.callParent();
    }
});