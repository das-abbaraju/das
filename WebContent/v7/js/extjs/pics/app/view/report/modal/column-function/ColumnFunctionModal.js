Ext.define('PICS.view.report.modal.column-function.ColumnFunctionModal', {
    extend: 'PICS.ux.window.Window',
    alias: 'widget.reportcolumnfunctionmodal',

    requires: [
        'PICS.view.report.modal.column-function.ColumnFunctionList'
    ],

    border: 0,
    bodyBorder: false,
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
    title: PICS.text('Report.execute.columnFunctionModal.title'),
    width: 300,
    items: [{
        xtype: 'reportcolumnfunctionlist'
    }],
    dockedItems: [{
        xtype: 'panel',
        border: 0,
        dock: 'bottom',
        height: 10,
        id: 'column_function_modal_footer'
    }]
});