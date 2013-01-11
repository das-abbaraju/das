Ext.define('PICS.view.report.modal.column-filter.ColumnModal', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterModal',
    alias: 'widget.columnmodal',

    id: 'column_modal',
    items: [{
        xtype: 'columnlist'
    }],

    initComponent: function () {
        this.setTitle('Add Column');

        this.callParent(arguments);
    }
});