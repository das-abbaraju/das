Ext.define('PICS.view.report.filter.Filter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportfilter'],

    requires: [
        'PICS.view.report.filter.base.AccountIDFilter',
        'PICS.view.report.filter.base.AutocompleteFilter',
        'PICS.view.report.filter.base.BooleanFilter',
        'PICS.view.report.filter.base.DateFilter',
        'PICS.view.report.filter.base.FloatFilter',
        'PICS.view.report.filter.base.IntegerFilter',
        'PICS.view.report.filter.base.ListFilter',
        'PICS.view.report.filter.base.StringFilter',
        'PICS.view.report.filter.base.UserIDFilter',
        'PICS.view.report.filter.FilterTooltip'
    ],

    bodyCls: 'filter-body',
    border: 0,
    cls: 'filter',
    height: 96,
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

        var filter = this.record,
            field = filter.getAvailableField();
        
        var type = field.get('filterType'),
            cls = this.getFilterClassByType(type),
            filter_number = this.createNumber(this.index),
            filter_content = this.createContent(this.record);

        this.add([
            filter_number,
            filter_content
        ]);
        
        
        // TODO: update this
        this.addRemoveButton();
        
        if (cls == 'PICS.view.report.filter.base.UserIDFilter') {
            // TODO: update this - probably doesn't belong
            this.addEditableButton();
        }
    },

    addEditableButton: function () {
        this.addDocked({
            xtype: 'toolbar',
            defaults: {
                margin: '0 5 5 0'
            },
            dock: 'bottom',
            items: [{
                xtype: 'button',
                action: 'show-advanced-filter',
                cls: 'advanced-filter-button',
                height: 22,
                text: '<i class="icon-pencil"></i>',
                tooltip: 'Advanced Filter',
                width: 20
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        });
    },
    
    addRemoveButton: function () {
        this.addDocked({
            xtype: 'toolbar',
            defaults: {
                margin: '2 4 0 0'
            },
            dock: 'top',
            items: [{
                xtype: 'button',
                action: 'remove-filter',
                cls: 'remove-filter',
                height: 20,
                text: '<i class="icon-remove-sign"></i>',
                tooltip: 'Remove',
                width: 20
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        });        
    },
    
    createNumber: function (index) {
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

    createContent: function (record) {
        var filter_title = this.createTitle(record);
        var filter_input = this.createInput(record);

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

    createTitle: function (record) {
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
            }],
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            name: 'filter_title'
        };
    },

    createInput: function (record) {
        var field = record.getAvailableField(),
            type = field.get('filterType');

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }
        
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
    
    createTooltip: function () {
        var filter = this.record;
        
        if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
            Ext.Error.raise('Invalid filter record');
        }
        
        var target = this.el.down('.filter-name'),
            field = filter.getAvailableField(),
            help = field.get('help');
        
        var tooltip = Ext.create('PICS.view.report.filter.FilterTooltip', {
            target: target
        });
        
        tooltip.update({
            help: help
        });
    },

    getFilterClassByType: function (type) {
        var cls;
        
        switch (type) {
            case 'AccountID':
            case 'Autocomplete':
            case 'Boolean':
            case 'Date':
            case 'Float':
            case 'Integer':
            case 'String':
            case 'UserID':
                cls = 'PICS.view.report.filter.base.' + type + 'Filter';
                break;
            case 'ShortList':
                // TODO Rename ListFilter to ShortListFilter
                cls = 'PICS.view.report.filter.base.ListFilter';
                break;
            case 'DateTime':
                // TODO add in a DateTime filter type
                cls = 'PICS.view.report.filter.base.DateFilter';
                break;
            default:
                cls = 'PICS.view.report.filter.base.StringFilter';
                break;
        }

        return cls;
    }
});