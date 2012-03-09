Ext.define('PICS.view.filter.BooleanFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.booleanfilter'],    

    border: false,
    tbar: [{
		xtype: 'button',
		// disabled: true,
        listeners: {
            click: function () {
                var radioGroup = Ext.ComponentQuery.query("booleanfilter radiogroup")[0],
                    booleanFilter = Ext.ComponentQuery.query("booleanfilter")[0];
                
                var value = radioGroup.getValue().boolean;
                booleanFilter.record.set("value", value);
            }
        },
	    text: 'Apply'
    }],
    items: [{
        xtype: 'panel',
        border: false,
    	html: "Field Name"
    },{
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
    }],
    listeners: {
        beforeRender: function () {
            var booleanFilter = Ext.ComponentQuery.query("booleanfilter")[0],
                radioGroup = Ext.ComponentQuery.query("booleanfilter radiogroup")[0];
            
            radioGroup.setValue({'boolean': booleanFilter.record.data.value});
        }
    },
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.items.items[0].html = "<h1>" + record.data.field.data.text + "</h1>";
    }
});
