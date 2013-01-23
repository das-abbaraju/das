Ext.define('PICS.view.report.modal.column-function.ColumnFunctionModal', {
    extend: 'PICS.ux.window.Window',
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
    dockedItems: [{
        xtype: 'panel',
        border: 0,
        dock: 'bottom',
        height: 10,
        id: 'column_function_modal_footer'
    }],
        
    initComponent: function () {
        if (Ext.getClassName(this.column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column');
        }

        var field = this.column,
            sql_functions = [{key: 'Sum', value: 'Count'}], // TODO: Temporary. Replace with server request.
            sql_function_items = this.createSqlFunctionItems(sql_functions),
            sql_function_list_view = this.createSqlFunctionListView(sql_function_items);

        this.dockedItems.push(sql_function_list_view);

        this.height = (40 * sql_functions.length) + 95;

        this.callParent(arguments);
    },

    createSqlFunctionListView: function (sql_function_items) {
        return {
            xtype: 'toolbar',
            border: 0,
            defaults: {
                height: 35
            },
            dock: 'top',
            id: 'column_function_list',
            items: sql_function_items,
            layout: 'vbox'
        };
    },

    createSqlFunctionItem: function (sql_function) {
        return {
            action: sql_function.key,
            height: 40,
            text: sql_function.value,
            textAlign: 'left'
        };
    },

    createSqlFunctionItems: function (sql_functions) {
        var items = [{
            action: '',
            height: 40,
            text: 'None',
            textAlign: 'left'
        }];

        var that = this;
        Ext.each(sql_functions, function (sql_function) {
            items.push(that.createSqlFunctionItem(sql_function));
        });

        return items;
    }
});