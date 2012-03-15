Ext.define('PICS.view.report.filter.AccountTypeFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.accounttypefilter'],    

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'combo',
        name: 'accounttype',
        store: [
	        ['Contractor', 'contractor'],
	        ['Corporate', 'corporate'],
	        ['Operator', 'operator'],
	        ['Empty', 'blank']
        ],
        typeAhead: true
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('accounttypefilter')[0],
                combo = form.child("combo"),
                value = form.record.data.value;
            
            (value) ? combo.setValue(value) : combo.setValue('Contractor'); 
        }
    },
    applyFilter: function() {
        var values = this.getValues();
        
        this.record.set('value', values.accounttype);
        this.record.set('operator', 'Equals');
        this.superclass.applyFilter();
    }
});