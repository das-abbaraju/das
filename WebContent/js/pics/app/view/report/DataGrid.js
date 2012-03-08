Ext.define('PICS.view.report.DataGrid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportdatagrid'],
    store: 'report.ReportData',
    
    requires: ['PICS.view.report.LinkColumn'],
    
    enableColumnHide: false,
    id: 'dataGrid',
    sortableColumns:  false,
    title: 'Grid',
    
    initComponent: function () {
        this.columns = [{"width":27,"xtype":"rownumberer"}];
        
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