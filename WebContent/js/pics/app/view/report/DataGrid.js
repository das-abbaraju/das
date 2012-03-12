Ext.define('PICS.view.report.DataGrid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportdatagrid'],
    requires: [
        'PICS.view.report.LinkColumn'
    ],
    store: 'report.ReportData',
    
    enableColumnHide: false,
    id: 'dataGrid',
    sortableColumns:  false,
    title: 'Grid',
    
    initComponent: function () {
        this.columns = [{
            xtype: 'rownumberer',
            
            width: 27
        }];
        
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