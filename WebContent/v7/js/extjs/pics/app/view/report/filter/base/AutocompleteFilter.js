Ext.define('PICS.view.report.filter.base.AutocompleteFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseautocompletefilter'],

    requires: [
        'Ext.ux.form.BoxSelect'
    ],

    cls: 'autocomplete-filter',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            throw '';
        }

        // TODO: why the hell is this here
        this.record.set('operator', 'In');

        var autocomplete = this.createAutocomplete(this.record);

        this.add(autocomplete);
    },

    createAutocomplete: function (record) {
        var value = record.get('value');
        var store = this.getStoreForAutocomplete(record);

        return {
            xtype: 'combobox',
            displayField: 'name',
            editable: true,
            hideTrigger: true,
            multiSelect: false,
            name: 'filter_value',
            queryParam: 'searchQuery',
            store: store,
            value: value,
            valueField: 'id',
            width: 258
        };

        return Ext.create('Ext.ux.form.BoxSelect', {
            delimiter: ',',
            displayField: 'name',
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

                    record.set('value', newval);
                }
            },
            margin: '0 5 0 0',
            minChars: 1,
            mode: 'remote',
            multiSelect: true,
            name: 'list_value',
            queryParam: 'searchQuery',
            store: store,
            typeAhead: false,
            value: value,
            valueField: 'id',
            width: 258
        });
    },

    getStoreForAutocomplete: function (record) {
        var url = Ext.Object.fromQueryString(document.location.search);
        var name = record.get('name');

        return {
            fields: [{
                name: 'id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }],
            proxy: {
                type: 'ajax',
                // TODO: why does this require a report number
                url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + name,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});