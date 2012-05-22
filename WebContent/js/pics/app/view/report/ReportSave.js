Ext.define('PICS.view.report.ReportSave', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsave'],

    bodyStyle: 'background: #FFF',
    height: 300,
   
    items: [{
        xtype: 'panel',
        border: false,
        items: [{
            xtype: 'textfield',
                flex: 2,
                fieldLabel: 'Name',
                name: 'reportName',
                value: null
        }, {
                xtype: 'textarea',
                cols: 25,
                flex: 2,
                fieldLabel: 'Description',
                name: 'reportDescription',
                value: null
        }]
    }],
    layout: {
        type: 'hbox',
        pack: 'center',
        align: 'middle'
    },
    modal: true,
    width: 500
});
