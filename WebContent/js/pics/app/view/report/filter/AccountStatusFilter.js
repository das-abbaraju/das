Ext.define('PICS.view.report.filter.AccountStatusFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.AccountStatusFilter'],

    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',
        name: 'title'
    }, {
        xtype: 'combo',
        editable: false,
        name: 'not',
        store: [
            ['false', ' '],
            ['true', 'not']
        ],
        width: 50
    }, {
        xtype: 'combo',
        name: 'accountstatus',
        store: [
	        ['Active', 'active'],
	        ['Pending', 'pending'],
	        ['Demo', 'demo'],
            ['Deleted', 'deleted'],
            ['Deactivated', 'deactivated'],            
	        ['Empty', 'blank']
        ],
        typeAhead: true
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child('combo[name=accountstatus'),
                value = target.record.data.value;
            
            (value) ? combo.setValue(value) : combo.setValue('Active'); 
        }
    },
    applyFilter: function() {
        var values = this.getValues();
        
        this.record.set('value', values.accountstatus);
        this.record.set('operator', 'Equals');
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
        }
        this.callParent(arguments);
    }    
});