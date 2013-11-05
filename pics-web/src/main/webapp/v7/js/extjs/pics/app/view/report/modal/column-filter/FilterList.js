Ext.define('PICS.view.report.modal.column-filter.FilterList', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterList',
    alias: 'widget.reportfilterlist',

    store: 'report.Filters',

    id: 'filter_list',

    selModel: Ext.create('Ext.selection.CheckboxModel', {
        mode: 'SIMPLE',

        listeners: {
            // This probably belongs in the controller. When defined there, however, selectionchange
            // doesn't fire on subsequent loads of the modal.
            selectionchange: function () {
                var add_button = Ext.ComponentQuery.query('reportfiltermodal button[action=add]')[0];

                this.getCount() ? add_button.setDisabled(false) : add_button.setDisabled(true);
            }
        }
    }),
});