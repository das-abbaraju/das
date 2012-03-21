Ext.define('PICS.view.report.filter.AccountStatusFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.AccountStatusFilter'],

    items: [{
        xtype: 'panel',
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
        id: 'accountstatus',
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
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('AccountStatusFilter')[0],
                combo = form.child("#accountstatus"),
                value = form.record.data.value;
            
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
    }
});