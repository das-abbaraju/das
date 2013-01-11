Ext.define('PICS.view.report.modal.column-filter.FilterModal', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterModal',
    alias: 'widget.filtermodal',

    id: 'filter_modal',
    items: [{
        xtype: 'filterlist'
    }],

    initComponent: function () {
        this.setTitle('Add Filter');

        this.callParent(arguments);
    }
});