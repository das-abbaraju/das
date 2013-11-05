Ext.define('PICS.view.report.modal.column-filter.ColumnList', {
    extend: 'PICS.view.report.modal.column-filter.ColumnFilterList',
    alias: 'widget.reportcolumnlist',

    store: 'report.Columns',

    id: 'column_list',

    selModel: Ext.create('Ext.selection.CheckboxModel', {
        mode: 'SIMPLE',

        listeners: {
            // This probably belongs in the controller. When defined there, however, selectionchange
            // doesn't fire on subsequent loads of the modal.
            selectionchange: function () {
                var add_button = Ext.ComponentQuery.query('reportcolumnmodal button[action=add]')[0];

                this.getCount() ? add_button.setDisabled(false) : add_button.setDisabled(true);
            }
        }
    })
});