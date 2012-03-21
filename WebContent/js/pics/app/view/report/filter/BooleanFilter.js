Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],    

    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'checkbox',
        boxLabel  : 'On',
        name      : 'boolean',
        inputValue: '1'
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query("booleanfilter")[0],
                checkbox = form.child("checkbox");

            checkbox.setValue(form.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();
       
        if (values.boolean === '1') {
            this.record.set('value', values.boolean);
        } else {
            this.record.set('value', 0);
        }
        this.record.set('operator', 'Equals'); //TODO remove hack to get boolean working
        this.superclass.applyFilter();
    }
});
