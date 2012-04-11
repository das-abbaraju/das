Ext.define('PICS.view.report.filter.CountryFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.countryfilter'],

    id: 'test',
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
        valueField: 'countryCode'
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child('combo[name=country]'),
                value = target.record.data.value;

            (value) ? combo.setValue(value) : combo.setValue(''); 
        }
    },
    applyFilter: function() {
        var values = this.getValues(),
        valuesFormat = "",
        formatted = values.country;
        
        if (values.country.length < 2) {
            values.country[0] = values.country[0].replace(/'/g,'');
            formatted = values.country[0].split(',');
        }        
        
        for (x = 0; x < formatted.length; x++) {
            if (x !== 0) {
                valuesFormat += ',';    
            }
            valuesFormat += '\'' + formatted[x] + '\'';
        }
        this.record.set('value', valuesFormat);
        this.record.set('operator', 'In');
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