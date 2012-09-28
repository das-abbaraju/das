Ext.define('PICS.view.report.filter.Filter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportfilter'],

    requires: [
        'PICS.view.report.filter.base.AutocompleteFilter',
        'PICS.view.report.filter.base.BooleanFilter',
        'PICS.view.report.filter.base.DateFilter',
        'PICS.view.report.filter.base.FloatFilter',
        'PICS.view.report.filter.base.IntegerFilter',
        'PICS.view.report.filter.base.ListFilter',
        'PICS.view.report.filter.base.StringFilter'
    ],

    bodyCls: 'filter-body',
    border: 0,
    cls: 'filter',
    height: 80,
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    overCls: 'x-over',
    width: 320,

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        if (!this.index) {
            Ext.Error.raise('Invalid filter index');
        }

        var filter_number = this.createFilterNumber(this.index);
        var filter_content = this.createFilterContent(this.record);

        this.add([
            filter_number,
            filter_content
        ]);
    },

    createFilterNumber: function (index) {
        return {
            xtype: 'displayfield',
            border: 0,
            cls: 'filter-number',
            fieldLabel: index.toString(),
            labelSeparator: '',
            labelWidth: 30,
            name: 'filter_number',
            width: 30
        };
    },

    createFilterContent: function (record) {
        var filter_title = this.createFilterTitle(record);
        var filter_input = this.createFilterInput(record);

        return {
            border: 0,
            cls: 'filter-content',
            items: [
                filter_title,
                filter_input
            ],
            name: 'filter_content',
            width: 258
        };
    },

    createFilterTitle: function (record) {
        var field = record.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }

        var text = field && field.get('text');

        if (!text) {
            Ext.Error.raise('Invalid filter text');
        }

        if (text.length >= 29) {
            text = text.substring(0, 29) + '...';
        }

        return {
            border: 0,
            height: 30,
            items: [{
                xtype: 'displayfield',
                cls: 'filter-name',
                name: 'filter_name',
                value: text
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'button',
                action: 'remove-filter',
                cls: 'remove-filter',
                height: 16,
                text: '<i class="icon-remove-sign"></i>',
                tooltip: 'Remove',
                width: 16
            }],
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            name: 'filter_title',
        };
    },

    createFilterInput: function (record) {
        var field = record.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }

        var type = field && field.get('filterType');

        if (!type) {
            Ext.Error.raise('Invalid filter type');
        }

        var cls = this.getFilterClassByType(type);

        if (!cls) {
            Ext.Error.raise('Invalid filter cls');
        }

        return Ext.create(cls, {
            border: 0,
            draggable: false,
            name: 'filter_input',
            record: record
        });
    },

    getFilterClassByType: function (type) {
        var cls;

        switch (type) {
            // TODO: this is retarded the backend architecture is invalid
            case 'AccountID':
                cls = 'PICS.view.report.filter.base.AccountIDFilter';
                break;
            case 'AccountName':
                cls = 'PICS.view.report.filter.base.StringFilter';
                break;
            case 'Autocomplete':
                cls = 'PICS.view.report.filter.base.AutocompleteFilter';
                break;
            case 'Boolean':
                cls = 'PICS.view.report.filter.base.BooleanFilter';
                break;
            case 'Date':
                cls = 'PICS.view.report.filter.base.DateFilter';
                break;
            case 'DaysAgo':
                // Add new filter for days ago for Steps to Green report
                cls = 'PICS.view.report.filter.base.IntegerFilter';
                break;
            case 'Enum':
                cls = 'PICS.view.report.filter.base.ListFilter';
                break;
            case 'Float':
                cls = 'PICS.view.report.filter.base.FloatFilter';
                break;
            case 'Integer':
                cls = 'PICS.view.report.filter.base.IntegerFilter';
                break;
            case 'LowMedHigh':
                cls = 'PICS.view.report.filter.base.ListFilter';
                break;
            case 'Number':
                cls = 'PICS.view.report.filter.base.IntegerFilter';
                break;
            case 'String':
                cls = 'PICS.view.report.filter.base.StringFilter';
                break;
            case 'UserID':
                cls = 'PICS.view.report.filter.base.UserIDFilter';
                break;
            default:
                cls = null;
                break;
        }

        return cls;
    }
});