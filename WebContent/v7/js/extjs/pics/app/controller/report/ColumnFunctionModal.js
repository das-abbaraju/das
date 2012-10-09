Ext.define('PICS.controller.report.ColumnFunctionModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'functionModal',
        selector: 'reportcolumnfunctionmodal'
    }],

    views: [
        'PICS.view.report.column-function.ColumnFunctionModal'
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
        var modal = this.getFunctionModal(),
            column = modal.column,
            action = cmp.action;

        // set the method on the column store - column
        column.set('method', action);

        // refresh report
        this.application.fireEvent('refreshreport');

        // destroy modal for next use (generate with correct column)
        modal.destroy();
    },

    // show the column function modal , but attach the specific column store - column your modifying
    showColumnFunctionModal: function (column) {
        var modal = Ext.create('PICS.view.report.column-function.ColumnFunctionModal', {
                column: column
            }),
            that = this;

        modal.show(false, function () {
            that.application.fireEvent('setonxmaskclick', modal);
        });
    }
});