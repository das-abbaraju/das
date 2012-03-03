Ext.define('PICS.view.report.DataGrid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportdatagrid'],
    store: 'report.ReportData',
    
    enableColumnHide: false,
    sortableColumns:  false,
    title: 'Grid',
    
    initComponent: function () {
        this.columns = gridColumns;
        
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