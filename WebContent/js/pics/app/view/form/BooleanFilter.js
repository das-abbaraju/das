Ext.define('PICS.view.form.BooleanFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.booleanfilter'],    

    border: false,
    items: [{
        xtype: 'panel',
        border: false,
    	html: "Field Name"
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
        }],
        listeners: {
        	change: function (field, value) {
        		if (value) {
            		console.log(field.getValue());
            		console.log(this.record);
                    //this.record.set("value", field.getValue());
        		}
        	}
        	
        }        
    }],
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.items.items[0].html = "<h1>" + record.data.field.data.text + "</h1>";
    }
});
