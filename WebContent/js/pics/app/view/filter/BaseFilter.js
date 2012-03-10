Ext.define('PICS.view.filter.BaseFilter', {
    extend: 'Ext.form.Panel',

    border: false,
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel'
    }],
    tbar: [{
		xtype: 'button',
		itemId: 'apply',
		// disabled: true,
	    text: 'Apply'
    }],
    listeners: {
        beforerender: function () {
        	var tbar = this.getDockedComponent(0),
        		button = tbar.child("button");
        	button.addListener("click", this.apply, this);
        }
    },
    apply: function() {},
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	// this.items.items[0].html = "<h1>" + record.data.field.data.text + "</h1>";
    }
});