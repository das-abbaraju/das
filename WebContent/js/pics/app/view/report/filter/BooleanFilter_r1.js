Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],    

    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',        
        name: 'title'
    },{
        xtype: 'checkbox',
        boxLabel  : 'On',
        name      : 'boolean',
        inputValue: '1'
    }],
    listeners: {
        beforeRender: function (target) {
            var checkbox = target.child("checkbox");

            checkbox.setValue(target.record.data.value);
        }
    },
    applyFilter: function() {
        var values = this.getValues();
       
        if (values.boolean === '1') {
            this.record.set('value', values.boolean);
        } else {
            this.record.set('value', 0);
        }
        this.record.set('operator', 'Equals'); //TODO remove hack to get boolean working
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
        }
        this.callParent(arguments);
    }
});
