Ext.define('PICS.view.filter.BooleanFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.booleanfilter'],    

    border: false,
    tbar: [{
		xtype: 'button',
		// disabled: true,
        listeners: {
            click: function () {
                var form = Ext.ComponentQuery.query('booleanfilter')[0];
                var values = form.getValues();

                if (values.boolean === '1') {
                    form.record.set('value', values.boolean);
                } else {
                    form.record.set('value', 0);
                }
            }
        },
	    text: 'Apply'
    }],
    items: [{
        xtype: 'panel',
        border: false,
    	html: "Field Name"
    },{
        xtype: 'checkbox',
        boxLabel  : 'Equals',
        name      : 'boolean',
        inputValue: '1'
    }],
    /*{
        xtype: 'radiogroup',
        id: 'radioFields',
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
        }],
    }*/
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query("booleanfilter")[0],
                checkbox = Ext.ComponentQuery.query("booleanfilter checkbox")[0];

            checkbox.setValue(form.record.data.value);
        }
        /*beforeRender: function () {
            var booleanFilter = Ext.ComponentQuery.query("booleanfilter")[0],
                radioGroup = Ext.ComponentQuery.query("booleanfilter radiogroup")[0];
            
            radioGroup.setValue({'boolean': booleanFilter.record.data.value});
        }*/
    },
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.items.items[0].html = "<h1>" + record.data.field.data.text + "</h1>";
    }
});
