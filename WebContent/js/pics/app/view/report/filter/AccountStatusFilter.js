Ext.define('PICS.view.report.filter.AccountStatusFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.AccountStatusFilter'],

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
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
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('AccountStatusFilter')[0],
                combo = form.child("combo"),
                value = form.record.data.accountstatus;
            
            (value) ? combo.setValue(value) : combo.setValue('Active'); 
        }
    },
    constructor: function () {
        Ext.override(PICS.view.report.filter.BaseFilter, {
            applyFilter: function() {
                var values = this.getValues();
                
                this.record.set('value', values.accountstatus);
                this.record.set('operator', 'Equals');
                this.callOverridden();  //call base function
            }
        });
        this.callParent(arguments);        
    }
});