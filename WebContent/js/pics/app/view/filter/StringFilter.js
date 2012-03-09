Ext.define('PICS.view.filter.StringFilter', {
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
        store: [
	        ['Contains', 'contains'],
	        ['BeginsWith', 'begins with'],
	        ['EndsWith', 'ends with'],
	        ['Equals', 'equals'],
	        ['Empty', 'blank']
        ],
        typeAhead: true
    },{
        xtype: 'textfield',
        name: 'textfilter',
        text: 'Value'
    }],
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.items.items[0].html = "<h1>" + record.data.field.data.text + "</h1>";
    }
});