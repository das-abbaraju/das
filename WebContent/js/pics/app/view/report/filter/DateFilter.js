Ext.define('PICS.view.report.filter.DateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.datefilter'],

    
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
        typeAhead: true
    },{
        xtype: 'datefield',
        id: 'datefilter',
        name: 'date',
        maxValue: new Date()  // limited to the current date or prior
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('datefilter')[0],
                combo = form.child("combo"),
                textfield = form.child("#datefilter");

            combo.setValue(form.record.data.operator);
            textfield.setValue(form.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();
     
        this.record.set('value', values.date);
        this.record.set('operator', values.operator);
        this.superclass.applyFilter();
    }
    dateConvert: function (year,month,day,hours,minutes,seconds,ms) {
        var d = Date.UTC(year,month,day,hours,minutes,seconds,ms);
        return d;
    }
});