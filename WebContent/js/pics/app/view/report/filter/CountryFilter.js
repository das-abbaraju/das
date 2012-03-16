Ext.define('PICS.view.report.filter.CountryFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.countryfilter'],

    id: 'test',
    items: [{
        xtype: 'panel',
        name: 'title'
    },{
        xtype: 'combo',
        store: Ext.create('Ext.data.Store', {
                autoLoad: true,
                fields: [
                    { name: 'countryName', type: 'string' },
                    { name: 'countryCode', type: 'string' }
                ],
                proxy: {
                    type: 'ajax',
                    url : 'ReportDynamic!data.action?report.modelType=Country&report.parameters={"rowsPerPage":1000,"columns":[{"name":"countryCode"},{"name":"countryName"}]}',
                    reader: {
                        type: 'json',
                        root: 'data'
                    }
                }
        }),
        displayField: 'countryName',
        editable: false,
        multiSelect: true,
        name: 'country',
        queryMode: 'local',
        typeAhead: true,
        valueField: 'countryCode'
    }],
    listeners: {
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('countryfilter')[0],
                combo = form.child("combo"),
                value = form.record.data.value;

            (value) ? combo.setValue(value) : combo.setValue(''); 
        }
    },
    applyFilter: function() {
        var values = this.getValues(),
        valuesFormat = "";
        
        for (x = 0; x < values.country.length; x++) {
            if (x !== 0) {
                valuesFormat += ',';    
            }
            valuesFormat += '\'' + values.country[x] + '\'';
        }
        this.record.set('value', valuesFormat);
        this.record.set('operator', 'In');
        this.superclass.applyFilter();
    }    
});