Ext.define('PICS.view.report.filter.FloatFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.floatfilter'],

    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',
        name: 'title'
    },{
        xtype: 'combo',
        editable: false,
        name: 'not',
        store: [
            ['false', ' '],
            ['true', 'not']
        ],
        width: 50
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
        value: '<',
        width: 55
    },{
        xtype: 'numberfield',
        hideTrigger: true,
        keyNavEnabled: false,
        mouseWheelEnabled: false,
        name: 'textfilter',
        text: 'Value'        
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child('combo[name=operator]'),
                textfield = target.child('textfield[name=textfilter]');
            
            combo.setValue(target.record.data.operator);
            textfield.setValue(target.record.data.value);
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
    },
    constructor: function (config) {
        if (config.displayMode === 'docked') {
            this.items.push({
                xtype: 'button',
                itemId: 'apply',
                action: 'apply',
                listeners: {
                    click: function () {
                        this.up().applyFilter(true);
                    }
                },
                text: 'Apply',
                cls: 'x-btn-default-small'
            });
            this.items.splice(1,1); //remove NOT combo
        }
        this.callParent(arguments);
    }
});