Ext.define('PICS.view.report.filter.CountryFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.countryfilter'],

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
            store: COUNTRIES,
            flex: 1.5,
            value: ''
        }],
        layout: 'hbox'  
    }],     
    title: 'Country',
    
    initComponent: function () {
        var display = this.record.get('column');
        this.items[0].value = display;
        this.items[1].items[0].value = this.record.get('value');
        this.callParent(arguments);
    }   
});