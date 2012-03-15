Ext.define('PICS.view.report.filter.BaseFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.basefilter'],

    constructor:function(config){
        this.callParent(arguments);
        this.buttonSetup();
    },  
    border: false,
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel',
        id: "basePanel",
        html: "Field Name"
    }],
    bbar: [{
		xtype: 'button',
		itemId: 'apply',
		action: 'apply',
		// disabled: true,
	    text: 'Apply',
	    cls: 'x-btn-default-small'	    
    }],
    applyFilter: function() {
        //console.log("base applyFilter");
    },
    buttonSetup: function () {
        var tbar = this.getDockedComponent(0),
            button = tbar.child("button");
            
        button.addListener("click", this.applyFilter, this); 
    },
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.setTitle();
    },
    setTitle: function () {
        this.items.items[0].html = "<h1>" + this.record.data.field.data.text + "</h1>";        
    }
});