Ext.define('PICS.controller.report.ColumnFunctionModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'columnFunctionModal',
        selector: 'reportcolumnfunctionmodal'
    }],

    stores: [
        'report.ColumnFunctions'
    ],

    views: [
        'PICS.view.report.modal.column-function.ColumnFunctionModal'
    ],

    init: function () {
        this.control({
            'reportcolumnfunctionmodal button': {
                click: this.onButtonClick
            }
        });

        this.application.on({
            opencolumnfunctionmodal: this.openColumnFunctionModal,
            scope: this
        });
    },

    onButtonClick: function (cmp, event, eOpts) {
        var column_function_modal = this.getColumnFunctionModal(),
            column = column_function_modal.column,
            action = cmp.action;

        // set the method on the column store - column
        column.set('sql_function', action);

        // destroy modal for next use (generate with correct column)
        column_function_modal.close();
        
        // refresh report
        PICS.data.ServerCommunication.loadData();
    },

    openColumnFunctionModal: function (column) {
        var field_id = column.get('field_id'),
            column_function_store = this.getReportColumnFunctionsStore(),
            url = PICS.data.ServerCommunicationUrl.getColumnFunctionUrl(field_id);
        
        column_function_store.setProxyForRead(url);
        
        column_function_store.load(function (records, operation, success) {
            if (success) {
                var column_function_modal = Ext.create('PICS.view.report.modal.column-function.ColumnFunctionModal');
                
                column_function_modal.show();
            } else {
                // TODO: throw error
            }
        });
    }
});