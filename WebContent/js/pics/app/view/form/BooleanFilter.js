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
        fieldLabel: 'Equals',
        items: [{
            xtype: 'radiofield',
            boxLabel: 'Yes',
            inputValue: '1',
            name: "boolean"
        },{
            xtype: 'radiofield',
            boxLabel: 'No',
            inputValue: '0',
            name: "boolean"
        }]
    }]
});

