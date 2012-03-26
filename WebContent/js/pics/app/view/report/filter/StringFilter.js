Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    testingDock: false,
    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',        
        name: 'title'
    },{
        xtype: 'combo',
        name: 'not',
        store: [
            ['false', ''],
            ['true', 'not']
        ],
        typeAhead: false,
        width: 50
    },{
        xtype: 'combo',
        name: 'operator',
        store: [
	        ['Contains', 'contains'],
	        ['BeginsWith', 'begins with'],
	        ['EndsWith', 'ends with'],
	        ['Equals', 'equals'],
	        ['Empty', 'blank']
        ],
        typeAhead: true
    },{
        xtype: 'textfield',
        listeners: {
            focus: function (target) {
                target.setValue(target.up().record.data.value);
            }
        },
        name: 'textfilter',
        text: 'Value'
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child('combo[name=operator]'),
                textfield = target.child('textfield[name=textfilter]'),
                docked = target.child('checkbox[name=dockFilter]'),                
                value = target.record.data.operator;
            
            if (target.displayMode !== 'docked') {
                combo.setValue(value);
            }
            textfield.setValue(target.record.data.value);
        },
        beforeShow: function (target) {
            var combo = target.child('combo[name=operator]'),
                textfield = target.child('textfield[name=textfilter]'),
                docked = target.child('checkbox[name=dockFilter]'),                
                value = target.record.data.operator;
            
            if (target.displayMode !== 'docked') {
                combo.setValue(value);
            }
            textfield.setValue(target.record.data.value);
        }  
    },
    applyFilter: function() {
        //console.log('apply in string filter');
        var values = this.getValues();


        if (this.displayMode === 'docked') {
            values.operator = "Contains";
        }
        this.record.set('value', values.textfilter);
        this.record.set('operator', values.operator);

        if (values.not === 'true') {
            this.record.set('not', true);    
        } else {
            this.record.set('not', false);
        }
        this.superclass.applyFilter();
    },
    constructor: function (config) {
        if (config.displayMode === 'docked') {
            this.items = [{
                xtype: 'panel',
                bodyStyle: 'background:transparent;',                
                name: 'title',
            },{
                xtype: 'textfield',
                listeners: {
                    focus: function (target) {
                        target.setValue(target.up().record.data.value);
                    }
                },                
                name: 'textfilter',
                text: 'Value'
            },{
                xtype: 'button',
                itemId: 'apply',
                action: 'apply',
                listeners: {
                    click: function () {
                        this.up().applyFilter();
                    }
                },
                text: 'Apply',
                cls: 'x-btn-default-small'
            }];
        }
        this.callParent(arguments);
    }
});