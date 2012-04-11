Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    items: [{
        xtype: 'displayfield',
        value: null
    },{
        xtype: 'panel',
        border: 0,
        items: [{
            xtype: 'combo',
            listeners: {
                change: function (obj, newval, oldval, options) {
                   this.up('stringfilter').record.set('operator', newval);
                }
            },
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.TEXTSTORE,
            flex: 1.5,
            value: 'Contains'
        }, {
            xtype: 'textfield',
            flex: 2,
            value: null
        }],
        layout: 'hbox'  
    }],
    title: 'String'
});