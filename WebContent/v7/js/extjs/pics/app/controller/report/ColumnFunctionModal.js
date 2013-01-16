Ext.define('PICS.controller.report.ColumnFunctionModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'columnFunctionModal',
        selector: 'reportcolumnfunctionmodal'
    }],

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
            showcolumnfunctionmodal: this.showColumnFunctionModal,
            scope: this
        });
    },

    onButtonClick: function (cmp, event, eOpts) {
        var column_function_modal = this.getColumnFunctionModal(),
            column = column_function_modal.column,
            action = cmp.action;

        // set the method on the column store - column
        column.set('method', action);

        // destroy modal for next use (generate with correct column)
        column_function_modal.destroy();
        
        // refresh report
        this.application.fireEvent('refreshreport');
    },

    // show the column function modal , but attach the specific column store - column your modifying
    showColumnFunctionModal: function (column) {
        var column_function_modal = Ext.create('PICS.view.report.modal.column-function.ColumnFunctionModal', {
            column: column
        });

        column_function_modal.show();
    }
});