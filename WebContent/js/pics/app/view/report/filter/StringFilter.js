Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
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
                textfield = form.child("#textfilter"),
                value = form.record.data.operator;
            
            (value) ? combo.setValue(value) : combo.setValue('Contains'); 
            textfield.setValue(form.record.data.value);
        }
    },
    constructor: function () {
        Ext.override(PICS.view.report.filter.BaseFilter, {
            applyFilter: function() {
                var values = this.getValues();
                
                this.record.set('value', values.textfilter);
                this.record.set('operator', values.operator);
                this.callOverridden();  //call base function
            }
        });
        this.callParent(arguments);        
    }
});