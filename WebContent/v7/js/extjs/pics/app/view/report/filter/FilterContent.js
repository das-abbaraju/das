Ext.define('PICS.view.report.filter.FilterContent', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterfiltercontent'],

    requires: [
        'PICS.view.report.filter.FilterTitle',
        'PICS.view.report.filter.base.AutocompleteFilter',
        'PICS.view.report.filter.base.BooleanFilter',
        'PICS.view.report.filter.base.DateFilter',
        'PICS.view.report.filter.base.FloatFilter',
        'PICS.view.report.filter.base.IntegerFilter',
        'PICS.view.report.filter.base.ListFilter',
        'PICS.view.report.filter.base.NumberFilter',
        'PICS.view.report.filter.base.StringFilter'
    ],

    border: 0,
    cls: 'filter-content',
    name: 'filter_content',
    width: 258,

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var field = this.record.getAvailableField();
        var type = field.get('filterType');

        var filter_title = this.createFilterTitle(this.record);
        var filter_input = this.createFilterInput(type, this.record);

        this.add([
            filter_title,
            filter_input
        ]);
    },

    createFilterTitle: function (record) {
        return Ext.create('PICS.view.report.filter.FilterTitle', {
            record: record
        });
    },

    createFilterInput: function (type, record) {
        var cls = this.getFilterClassByType(type);

        if (!cls) {
            throw 'Missing cls for this.getFilterClassByType(' + type + ')';
        }

        return Ext.create(cls, {
            border: 0,
            name: 'filter_input',
            draggable: false,
            record: record
        });
    },

    getFilterClassByType: function (type) {
        var cls;

        switch (type) {
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
            case 'Enum':
                cls = 'PICS.view.report.filter.base.ListFilter';
                break;
            case 'Float':
                cls = 'PICS.view.report.filter.base.FloatFilter';
                break;
            case 'Integer':
                cls = 'PICS.view.report.filter.base.IntegerFilter';
                break;
            case 'Number':
                cls = 'PICS.view.report.filter.base.IntegerFilter';
                break;
            case 'String':
                cls = 'PICS.view.report.filter.base.StringFilter';
                break;
            default:
                cls = null;
                break;
        }

        return cls;
    }
});