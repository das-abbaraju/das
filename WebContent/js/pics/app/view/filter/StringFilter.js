Ext.define('PICS.view.filter.StringFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.stringfilter'],

    border: false,
    tbar: [{
        xtype: 'button',
        // disabled: true,
        listeners: {
            click: function () {
                var form = Ext.ComponentQuery.query('stringfilter')[0];
                var values = form.getValues();
                
                form.record.set('value', values.textfilter);
                form.record.set('operator', values.operator); 
            }
        },
        text: 'Apply'
    }],    
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel'
    },{
        xtype: 'combo',
        name: 'operator',
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
        id: 'textfilter',
        name: 'textfilter',
        text: 'Value'
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('stringfilter')[0],
                combo = Ext.ComponentQuery.query('stringfilter combo')[0],  
                textfield = Ext.ComponentQuery.query('stringfilter #textfilter')[0];
            
            combo.setValue(form.record.data.operator);
            textfield.setValue(form.record.data.value);
        }
    },    
    record: null,
    setRecord: function (record) {
    	this.record = record;
    	this.items.items[0].html = '<h1>' + record.data.field.data.text + '</h1>';
    }
});