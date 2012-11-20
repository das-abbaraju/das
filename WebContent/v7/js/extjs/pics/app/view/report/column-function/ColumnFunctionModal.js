Ext.define('PICS.view.report.column-function.ColumnFunctionModal', {
    extend: 'PICS.view.report.ReportModal',
    alias: 'widget.reportcolumnfunctionmodal',

    border: 0,
    closeAction: 'destroy',
    draggable: false,
    header: {
        height: 44
    },
    id: 'column_function_modal',
    layout: 'fit',
    modal: true,
    resizable: false,
    shadow: 'frame',
    title: 'Column Functions',
    width: 300,

    initComponent: function () {
        if (!this.column
                || this.column.modelName != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column record');
        }

        this.callParent(arguments);

        var field = this.column.getAvailableField(),
            column_functions = field.get('functions'),
            column_function_items = this.getColumnFunctionItems(column_functions);

        this.addDockedItems(column_function_items);

        this.height = (40 * column_functions.length) + 95;
    },

    addDockedItems: function (column_function_items) {
        this.addDocked({
            xtype: 'toolbar',
            border: 0,
            defaults: {
                height: 35
            },
            dock: 'top',
            id: 'column_function_list',
            items: column_function_items,
            layout: 'vbox'
        });

        this.addDocked({
            xtype: 'panel',
            border: 0,
            dock: 'bottom',
            height: 10,
            id: 'column_function_modal_footer'
        });
    },

    getColumnFunctionItem: function (column_function) {
        return {
            action: column_function.key,
            height: 40,
            text: column_function.value,
            textAlign: 'left'
        };
    },

    getColumnFunctionItems: function (column_functions) {
        var items = [{
            action: '',
            height: 40,
            text: 'None',
            textAlign: 'left'
        }];

        var that = this;
        Ext.each(column_functions, function (column_function) {
            items.push(that.getColumnFunctionItem(column_function));
        });

        return items;
    }
});