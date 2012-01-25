Ext.define('PICS.view.report.FilterColumnGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportfiltercolumngrid'],

    store: 'report.AvailableFields',
    
    enableColumnHide: false,
    //sortableColumns:  false,
    
    initComponent: function () {
        this.columns = [{
            text: 'Column Name',
            dataIndex: 'text'
        }, {
            text: 'Category',
            dataIndex: 'category'
        }];
        
        this.callParent();
    }
});