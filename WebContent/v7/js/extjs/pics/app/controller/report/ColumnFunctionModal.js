Ext.define('PICS.controller.report.ColumnFunctionModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'columnFunctionModal',
        selector: 'reportcolumnfunctionmodal'
    }],

    stores: [
        'report.Reports',
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

        column_function_modal.hide();

        // refresh report
        PICS.data.ServerCommunication.loadReportAndData();
    },

    openColumnFunctionModal: function (selected_column) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_type = report.get('type'),
            field_id = selected_column.get('field_id'),
            column_function_store = this.getReportColumnFunctionsStore(),
            url = PICS.data.ServerCommunicationUrl.getColumnFunctionUrl(report_type, field_id),
            column_function_modal = this.getColumnFunctionModal();

        column_function_store.setProxyForRead(url);

        // TODO: errors caught???
        column_function_store.load(function (records, operation, success) {
            if (success) {
                if (column_function_modal) {
                    if (column_function_modal.column != selected_column) {
                        column_function_modal.destroy();

                        column_function_modal = Ext.create('PICS.view.report.modal.column-function.ColumnFunctionModal', {
                            column: selected_column
                        });
                    }
                } else {
                    column_function_modal = Ext.create('PICS.view.report.modal.column-function.ColumnFunctionModal', {
                        column: selected_column
                    });
                }

                column_function_modal.show();
            } else {
                // TODO: throw error
            }
        });
    }
});