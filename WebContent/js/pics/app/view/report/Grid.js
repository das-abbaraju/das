Ext.define('PICS.view.report.Grid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportgrid'],

    store: 'report.ReportData',
    
    title: 'Grid',
    
    enableColumnHide: false,
    sortableColumns:  false,
    
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