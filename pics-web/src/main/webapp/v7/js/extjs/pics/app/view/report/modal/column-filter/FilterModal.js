Ext.define('PICS.view.report.modal.column-filter.FilterModal', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterModal',
    alias: 'widget.reportfiltermodal',

    id: 'filter_modal',
    items: [{
        xtype: 'reportfilterlist'
    }],

    initComponent: function () {
        this.setTitle(PICS.text('Report.execute.filterModal.title'));

        this.callParent(arguments);
    }
});