Ext.define('PICS.view.report.filter.Filter', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportfilter',

    requires: [
        'PICS.view.report.filter.base.AccountId',
        'PICS.view.report.filter.base.Autocomplete',
        'PICS.view.report.filter.base.Boolean',
        'PICS.view.report.filter.base.Date',
        'PICS.view.report.filter.base.Filter',
        'PICS.view.report.filter.base.Number',
        'PICS.view.report.filter.base.MultiSelect',
        'PICS.view.report.filter.base.String',
        'PICS.view.report.filter.base.UserId',
        'PICS.view.report.filter.FilterTooltip',
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    bodyCls: 'filter-body',
    border: 0,
    cls: 'filter',
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    padding: '0, 0, 10, 0',
    overCls: 'x-over',
    width: 320,

    initComponent: function () {
        var filter_record = this.filter,
            index = this.index;

        if (Ext.getClassName(filter_record) != 'PICS.model.report.Filter') {
            Ext.Error.raise('Invalid filter record');
        }

        if (!index) {
            Ext.Error.raise('Invalid filter index');
        }

        var type = filter_record.get('type'),
            filter_number = this.createNumber(index),
            filter_content = this.createContent(filter_record),
            remove_button = this.createRemoveButton();

        this.height = filter_content.items[1].height;

        this.items = [
            filter_number,
            filter_content
        ];

        this.dockedItems = [
            remove_button
        ];

        this.callParent(arguments);
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

    createContent: function (filter_record) {
        var filter_title = this.createTitle(filter_record);
        var filter_input = this.createIsput(filter_record);

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

    createTitle: function (filter_record) {
        var name = filter_record.get('name');

        if (name.length >= 29) {
            name = name.substring(0, 29) + '&hellip;';
        }

        return {
            border: 0,
            items: [{
                xtype: 'toolbar',
                style: {
                    background: 'transparent'
                },
                height: name == "Country" ? 34 : 30,
                items: [{
                    xtype: 'displayfield',
                    cls: 'filter-name',
                    name: 'filter_name',
                    value: name
                }, {
                    xtype: 'tbfill'
                },
                    name == "Country" ? this.createNegateOperatorToggleSlide() : this.createNegateOperatorToggleButton()
                ],
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                name: 'filter_title'
            }]
        };
    },

    createIsput: function (filter_record) {
        var type = filter_record.get('type'),
            cls = this.getFilterClassByType(type);

        return Ext.create(cls);
    },

    createNegateOperatorToggleButton: function () {
        return {
            xtype: 'button',
            action: 'toggle-negate-operator',
            cls: 'negate-operator-toggle-button',
            height: 17,
            text: '<i class="icon-ban-circle"></i>',
            listeners: {
                'render': function () {
                    Ext.merge(this, {
                        tooltip_cmp: Ext.create('Ext.tip.ToolTip', {
                            target: this.getEl(),
                            html: 'Ignore selected values'
                        }),
                        getTooltipHtml: function() {
                            if (typeof this.tooltip_cmp.body == 'undefined') {
                                return;
                            }

                            return this.tooltip_cmp.body.getHTML();
                        },
                        setTooltipHtml: function (value) {
                            if (typeof this.tooltip_cmp.body == 'undefined') {
                                return;
                            }

                            this.tooltip_cmp.body.setHTML(value);
                        }
                    });
                }
            }
        };
    },

    createNegateOperatorToggleSlide: function () {
        return {
            xtype: 'toggleslide',
            onText: '&nbsp',
            offText: '&nbsp',
            state: true,
            tooltip: 'Toggle include / ignore',
            listeners: {
                'render': function () {
                    Ext.merge(this, {
                        tooltip_cmp: Ext.create('Ext.tip.ToolTip', {
                            target: this.getEl(),
                            html: 'Ignore selected values'
                        }),
                        getTooltipHtml: function() {
                            if (typeof this.tooltip_cmp.body == 'undefined') {
                                return;
                            }

                            return this.tooltip_cmp.body.getHTML();
                        },
                        setTooltipHtml: function (value) {
                            if (typeof this.tooltip_cmp.body == 'undefined') {
                                return;
                            }

                            return this.tooltip_cmp.body.setHTML(value);
                        }
                    });
                }
            }
        };
    },

    createRemoveButton: function () {
        return {
            xtype: 'toolbar',
            defaults: {
                margin: '2 4 0 0'
            },
            dock: 'top',
            items: [{
                xtype: 'button',
                action: 'remove-filter',
                cls: 'remove-filter',
                height: 22,
                text: '<i class="icon-remove-sign"></i>',
                tooltip: PICS.text('Report.execute.filter.tooltipRemove'),
                width: 20
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        };
    },

    createTooltip: function () {
        var filter = this.filter;

        if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
            Ext.Error.raise('Invalid filter record');
        }

        var target = this.el.down('.filter-name'),
            description = filter.get('description');

        var tooltip = Ext.create('PICS.view.report.filter.FilterTooltip', {
            target: target
        });

        tooltip.update({
            description: description
        });
    },

    getFilterClassByType: function (type) {
        var filter_classes = this.getFilterClasses(),
            filter_class = filter_classes[type];

        if (typeof filter_class == 'undefined') {
            Ext.Error.raise('Invalid filter type: ' + type);
        }

        return filter_class;
    },

    getFilterClasses: function () {
        var filter_classes = {};

        filter_classes[PICS.data.FilterType.AccountID] = 'PICS.view.report.filter.base.AccountId';
        filter_classes[PICS.data.FilterType.Autocomplete] = 'PICS.view.report.filter.base.Autocomplete';
        filter_classes[PICS.data.FilterType.Boolean] = 'PICS.view.report.filter.base.Boolean';
        filter_classes[PICS.data.FilterType.Date] = 'PICS.view.report.filter.base.Date';
        filter_classes[PICS.data.FilterType.Multiselect] = 'PICS.view.report.filter.base.MultiSelect';
        filter_classes[PICS.data.FilterType.Number] = 'PICS.view.report.filter.base.Number';
        filter_classes[PICS.data.FilterType.String] = 'PICS.view.report.filter.base.String';
        filter_classes[PICS.data.FilterType.UserID] = 'PICS.view.report.filter.base.UserId';

        return filter_classes;
    }
});