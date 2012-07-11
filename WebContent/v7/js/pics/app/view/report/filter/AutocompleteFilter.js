Ext.define('PICS.view.report.filter.AutocompleteFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.autocompletefilter'],

    record: null,

    constructor: function (data) {
        var me = this;

        this.callParent(arguments);

        this.record = data.record;

        this.record.set('operator', 'In');

        this.createFilterCombo();
    },

    createFilterCombo: function () {
        var me = this;

        var url = Ext.Object.fromQueryString(document.location.search);

        var list_filter = Ext.create('Ext.ux.form.BoxSelect', {
            displayField: 'name',
            delimiter: ',',
            forceSelection: true,
            hideTrigger: true,
            listeners: {
                beforequery: function (queryEvent, options) {
                    //prevent empty queries
                    if (!queryEvent.query) {
                        return false;
                    }
                },
                change: function (obj, newval, oldval, options) {
                    newval = newval.replace(/\s/g,"");
                    this.up('autocompletefilter').record.set('value', newval);
                }
            },
            margin: '0 5 0 0',
            multiSelect: true,
            name: 'list_value',
            minChars: 1,
            mode: 'remote',
            queryParam: 'searchQuery',
            store: {
                fields: [{
                    name: 'id',
                    type: 'string'
                }, {
                    name: 'name',
                    type: 'string'
                }],
                proxy: {
                    type: 'ajax',
                    url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + this.record.get('name'),
                    reader: {
                        root: 'result',
                        type: 'json'
                    }
                },
                listeners: {
                    load: function () {
                        //set valueField to pull from json id
                        me.child('panel [name=filter_input] boxselect').valueField = 'id';
                    }
                }
            },
            typeAhead: false,
            valueField: 'searchQuery',
            value: this.record.get('value'),
            width: 258
        });

       // add filter
       this.child('panel [name=filter_input]').add(list_filter);
   }
});