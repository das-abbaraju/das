Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],

    items: [{
        xtype: 'displayfield',
        value: null
    },{
        xtype: 'form',
        border: 0,
        items: [{
            xtype: 'checkbox',
            boxLabel: 'On',
            margin: '0 5 0 0',
            name: 'operator',
            flex: 1.5,
            inputValue: '1',
            listeners: {
                change: function (obj, newval, oldval, options) {
                    this.up('booleanfilter').record.set('value', newval);
                }
            }
        }],
        layout: 'hbox'  
    }],
    
    title: 'Boolean',
    
    initComponent: function () {
        var display = this.record.get('column');
        this.items[0].value = display;
        this.items[1].items[0].inputValue = this.record.get('value');
        this.callParent(arguments);
    }
});