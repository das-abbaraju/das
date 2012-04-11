Ext.define('PICS.view.report.filter.DateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.datefilter'],

    
    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',         
        name: 'title'
    },{
        xtype: 'combo',
        name: 'operator',
        store: [
            ['GreaterThan', '>'],
            ['LessThan', '<'],
            ['GreaterThanOrEquals', '>='],
            ['LessThanOrEquals', '<='],
            ['Empty', 'blank']
        ],
        value: '<',
        width: 45
    },{
        xtype: 'datefield',
        format: 'Y-m-d',        
        name: 'date',
        maxValue: new Date()  // limited to the current date or prior
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child("combo"),
                textfield = target.child("datefield[name=date]");

            combo.setValue(target.record.data.operator);
            textfield.setValue(target.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();
     
        this.record.set('value', values.date);
        this.record.set('operator', values.operator);
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
        }
        this.callParent(arguments);
    }
});