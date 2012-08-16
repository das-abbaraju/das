Ext.define('PICS.view.report.column-function.ColumnFunctionModal', {
    extend: 'Ext.window.Window',
    alias: 'widget.reportfunctionmodal',

    border: 0,
    closeAction: 'destroy',
    draggable: false,
    dockedItems: [{
        xtype: 'toolbar',
        border: 0,
        defaults: {
            height: 35
        },
        dock: 'top',
        id: 'column_function_list',
        items: [{
            action: '',
            height: 40,
            text: 'None',
            textAlign: 'left'
        },{
            action: 'Average',
            height: 40,
            text: 'Average',
            textAlign: 'left'
        }, {
            action: 'Count',
            height: 40,
            text: 'Count',
            textAlign: 'left'
        }, {
            action: 'Min',
            height: 40,
            text: 'Min',
            textAlign: 'left'
        }, {
            action: 'Max',
            height: 40,
            text: 'Max',
            textAlign: 'left'
        }, {
            action: 'Sum',
            height: 40,
            text: 'Sum',
            textAlign: 'left'
        }],
        layout: 'vbox'
    }, {
        xtype: 'panel',
        border: 0,
        dock: 'bottom',
        height: 10,
        id: 'column_function_modal_footer'
    }],
    header: {
        height: 44
    },
    height: 295,
    id: 'column_function_modal',
    layout: 'fit',
    modal: true,
    resizable: false,
    shadow: 'frame',
    title: 'Column Functions',
    width: 300,

    initComponent: function () {
        if (!this.column || this.column.modelName != 'PICS.model.report.Column') {
            throw 'Invalid column record';
        }

        this.callParent(arguments);
    }
});