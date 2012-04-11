Ext.define('PICS.view.report.filter.NumberFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.numberfilter'],
    
    items: [{
        xtype: 'displayfield',
        value: null
    },{
        xtype: 'panel',
        border: 0,
        items: [{
            xtype: 'combo',
            margin: '0 5 0 0',
            name: 'operator',
            store: NUMBERSTORE,
            flex: 1.5,
            value: '='
        }, {
            xtype: 'textfield',     
            flex: 2,
            value: null
        }],
        layout: 'hbox'  
    }], 
    title: 'Number',
    
    initComponent: function () {
        var display = this.record.get('column');
        this.items[0].value = display;
        this.items[1].items[1].value = this.record.get('value');
        this.callParent(arguments);
    }   
});