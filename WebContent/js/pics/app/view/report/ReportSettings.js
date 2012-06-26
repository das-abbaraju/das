Ext.define('PICS.view.report.ReportSettings', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsettings'],

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
    width: 500,
    
    constructor: function () {
        this.callParent(arguments);

        var report = Ext.StoreManager.get('report.Reports').first();        
        
        this.child('panel textfield[name=reportName]').setValue(report.get('name'));
        this.child('panel textfield[name=reportDescription]').setValue(report.get('description'));        
    }
});
