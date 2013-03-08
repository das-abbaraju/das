Ext.define('PICS.view.report.modal.column-filter.ColumnModal', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterModal',
    alias: 'widget.reportcolumnmodal',

    id: 'column_modal',
    items: [{
        xtype: 'reportcolumnlist'
    }],

    initComponent: function () {
        this.setTitle('Add Column');

        this.callParent(arguments);
    }
});