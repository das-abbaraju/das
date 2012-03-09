Ext.define('PICS.view.form.BooleanFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.booleanfilter'],    

    border: false,
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel'
    },{
        xtype: 'radiogroup',
        fieldLabel: 'Type',
        items: [{
            xtype: 'radiofield',
            boxLabel: 'True',
            inputValue: 'true',
            name: "boolean"
        },{
            xtype: 'radiofield',
            boxLabel: 'False',
            inputValue: 'false',
            name: "boolean"
        }]
    }]
});

