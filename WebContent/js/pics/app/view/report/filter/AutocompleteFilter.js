Ext.define('PICS.view.report.filter.AutocompleteFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.autocompletefilter'],

    record: null,

    constructor: function (data) {
        var me = this;

        this.callParent(arguments);

        this.record = data.record;

        this.record.set('operator', 'In');

        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('name'));

        this.createFilterCombo();
   },

   createFilterCombo: function () {
       var me = this;

       var url = Ext.Object.fromQueryString(document.location.search);

       var listFilter = {
            xtype: 'panel',
            items: [{
                xtype: 'combo',
                allowBlank: false,
                displayField: 'name',
                forceSelection: true,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('autocompletefilter').record.set('value', newval);
                    }
                },
                margin: '0 5 0 0',
                multiSelect: true,
                name: 'listValue',
                minChars: 1,
                mode: 'remote',
                queryParam: 'searchQuery',
                store: {
                   fields: [
                       {name: 'id', type: 'string'},
                       {name: 'name',  type: 'string'}
                    ],
                    proxy: {
                        type: 'ajax',
                        url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + this.record.get('name'),
                        reader: {
                            root: 'result',
                            type: 'json'
                        }
                    }
                },
                valueField: 'id'
            }],
            layout: 'hbox'
        };
        this.add(listFilter);
   }
});