Ext.define('PICS.view.filter.StringFilter', {
    extend: 'PICS.view.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    items: [{
        xtype: 'panel',
        name: 'title'
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
                combo = form.child("combo"),
                textfield = form.child("#textfilter");
            
            combo.setValue(form.record.data.operator);
            textfield.setValue(form.record.data.value);
        }
    },
    constructor: function () {
        Ext.override(PICS.view.filter.BaseFilter, {
            applyFilter: function() {
                var values = this.getValues();
                
                this.record.set('value', values.textfilter);
                this.record.set('operator', values.operator);
                //this.callOverridden();  //call base function
            }
        });
        this.callParent(arguments);        
    }
});