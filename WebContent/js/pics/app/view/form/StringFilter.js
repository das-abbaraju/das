Ext.define('PICS.view.form.StringFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.stringfilter'],    

    border: false,
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel'
    },{
        xtype: 'combo',
        
        displayField: 'name',
        labelAlign: 'left',
        padding: 0,
        size: 10,
        typeAhead: true,
        valueField: 'name'                    
    },{
        xtype: 'textfield',
        name: 'textfilter'
    }],
    initComponent: function () {
        this.items[1].store = Ext.create('Ext.data.ArrayStore', {
                fields: [
                    {name: 'name', type: 'string'},
                ],
                data: [['begins with'],
                        ['contains'],
                        ['ends with'],
                        ['equal to'],
                        ['not empty']
                ],
                autoLoad: true
            });
        this.callParent();
    }
});