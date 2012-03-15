Ext.define('PICS.view.report.filter.NumberFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.numberfilter'],

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
        allowDecimals: false,
        hideTrigger: true,
        keyNavEnabled: false,
        id: 'numberfilter',
        mouseWheelEnabled: false,
        name: 'textfilter',
        text: 'Value'        
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('numberfilter')[0],
                combo = form.child("combo"),
                textfield = form.child("#numberfilter");
            
            combo.setValue(form.record.data.operator);
            textfield.setValue(form.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();

        this.record.set('value', values.textfilter);
        this.record.set('operator', values.operator);
        this.superclass.applyFilter();
    }
});