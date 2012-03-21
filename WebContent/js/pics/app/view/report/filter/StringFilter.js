Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'combo',
        name: 'not',
        store: [
            ['false', ''],
            ['true', 'not']
        ],
        typeAhead: false,
        width: 50
    },{
        xtype: 'combo',
        id: 'operator',
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
                combo = form.child("#operator"),
                textfield = form.child("#textfilter"),
                value = form.record.data.operator;
            
            combo.setValue(value);
            textfield.setValue(form.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();

        this.record.set('value', values.textfilter);
        this.record.set('operator', values.operator);
        if (values.not === 'true') {
            this.record.set('not', true);    
        } else {
            this.record.set('not', false);
        }
        this.superclass.applyFilter();
    }
});