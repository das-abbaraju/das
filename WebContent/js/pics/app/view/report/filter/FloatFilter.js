Ext.define('PICS.view.report.filter.FloatFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.floatfilter'],

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'combo',
        name: 'operator',
        store: [
	        ['Equals', '='],
	        ['GreaterThan', '>'],
	        ['LessThan', '<'],
	        ['GreaterThanOrEquals', '>='],
	        ['LessThanOrEquals', '<='],	        
	        ['Empty', 'blank']
        ],
        typeAhead: true,
        width: 55
    },{
        xtype: 'numberfield',
        hideTrigger: true,
        keyNavEnabled: false,
        id: 'floatfilter',
        mouseWheelEnabled: false,
        name: 'textfilter',
        text: 'Value'        
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('floatfilter')[0],
                combo = form.child("combo"),
                textfield = form.child("#floatfilter");
            
            combo.setValue(form.record.data.operator);
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