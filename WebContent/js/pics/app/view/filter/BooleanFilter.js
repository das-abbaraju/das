Ext.define('PICS.view.filter.BooleanFilter', {
    extend: 'PICS.view.filter.BaseFilter',
    alias: ['widget.booleanfilter'],    

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'checkbox',
        boxLabel  : 'Equals',
        name      : 'boolean',
        inputValue: '1'
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query("booleanfilter")[0],
                checkbox = form.child("checkbox");

            checkbox.setValue(form.record.data.value);
        }
    },
    constructor: function () {
        Ext.override(PICS.view.filter.BaseFilter, {
            applyFilter: function() {
                var values = this.getValues();
               
                if (values.boolean === '1') {
                    this.record.set('value', values.boolean);
                } else {
                    this.record.set('value', 0);
                }
                //this.callOverridden();  //call base function
            }
        });
       this.callParent(arguments);        
    }
});
