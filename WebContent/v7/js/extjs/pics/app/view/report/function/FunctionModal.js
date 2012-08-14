Ext.define('PICS.view.report.function.FunctionModal', {
    extend: 'Ext.window.Window',
    alias: 'widget.reportfunctionmodal',

    border: 0,
    draggable: false,
    dockedItems: [{
        xtype: 'toolbar',
        border: 0,
        defaults: {
            height: 35
        },
        dock: 'top',
        items: [{
            text: 'Sum',
            textAlign: 'left',
            width: '100%'
        }, {
            text: 'Average',
            textAlign: 'left',
            width: '100%'
        }, {
            text: 'Min',
            textAlign: 'left',
            width: '100%'
        }, {
            text: 'Max',
            textAlign: 'left',
            width: '100%'
        }, {
            text: 'Count',
            textAlign: 'left',
            width: '100%'
        }],
        layout: 'vbox'
    }, {
        xtype: 'panel',
        border: 0,
        dock: 'bottom',
        height: 10
    }],
    header: {
        height: 45
    },
    height: 500,
    id: 'function_modal',
    layout: 'fit',
    modal: true,
    resizable: false,
    title: 'Column Functions',
    width: 300,

    initComponent: function () {
        this.column;

        this.callParent(arguments);
    }
});