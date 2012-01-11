Ext.define('PICS.view.report.Grid', {
	extend: 'Ext.grid.Panel',
	alias: ['widget.reportgrid'],

    store: 'report.Reports',
    
    title: 'Grid',
    
    enableColumnHide: false,
    sortableColumns:  false,
    
    initComponent: function () {
        this.columns = gridColumns;
        
        this.dockedItems = [{
            xtype: 'pagingtoolbar',
            
            store: 'report.Reports',
            
            dock: 'top'
        }, {
            xtype: 'pagingtoolbar',
            
            store: 'report.Reports',
            
            dock: 'bottom'
        }];
        
        this.callParent();
    }
});