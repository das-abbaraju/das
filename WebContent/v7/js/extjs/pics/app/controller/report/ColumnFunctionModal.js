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
            'reportcolumnfunctionlist': {
                cellclick: this.onCellClick
            }
        });

        this.application.on({
            opencolumnfunctionmodal: this.openColumnFunctionModal,
            scope: this
        });
    },

    onCellClick: function (cmp, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var column_function_modal = this.getColumnFunctionModal(),
            column = column_function_modal.column,
            column_function_key = record.get('key');

        // set the method on the column store - column
        column.set('sql_function', column_function_key);

        // destroy modal for next use (generate with correct column)
        column_function_modal.close();
        
        // refresh report
        PICS.data.ServerCommunication.loadReportAndData();
    },

    openColumnFunctionModal: function (column) {
        var field_id = column.get('field_id'),
            column_function_store = this.getReportColumnFunctionsStore(),
            url = PICS.data.ServerCommunicationUrl.getColumnFunctionUrl(field_id);
        
        column_function_store.setProxyForRead(url);
        
        column_function_store.load(function (records, operation, success) {
            if (success) {
                var column_function_modal = Ext.create('PICS.view.report.modal.column-function.ColumnFunctionModal', {
                    column: column
                });
                
                column_function_modal.show();
            } else {
                // TODO: throw error
            }
        });
    }
});