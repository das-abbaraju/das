Ext.define('PICS.view.report.filter.StateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.statefilter'],

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
        multiSelect: true,
        name: 'state',
        store: [
            ['AL', 'Alabama'],  
            ['AK', 'Alaska'],  
            ['AZ', 'Arizona'],  
            ['AR', 'Arkansas'],  
            ['CA', 'California'],  
            ['CO', 'Colorado'],  
            ['CT', 'Connecticut'],  
            ['DE', 'Delaware'],  
            ['DC', 'District Of Columbia'],  
            ['FL', 'Florida'],  
            ['GA', 'Georgia'],  
            ['HI', 'Hawaii'],  
            ['ID', 'Idaho'],  
            ['IL', 'Illinois'],  
            ['IN', 'Indiana'],  
            ['IA', 'Iowa'],  
            ['KS', 'Kansas'],  
            ['KY', 'Kentucky'],  
            ['LA', 'Louisiana'],  
            ['ME', 'Maine'],  
            ['MD', 'Maryland'],  
            ['MA', 'Massachusetts'],  
            ['MI', 'Michigan'],  
            ['MN', 'Minnesota'],  
            ['MS', 'Mississippi'],  
            ['MO', 'Missouri'],  
            ['MT', 'Montana'],
            ['NE', 'Nebraska'],
            ['NV', 'Nevada'],
            ['NH', 'New Hampshire'],
            ['NJ', 'New Jersey'],
            ['NM', 'New Mexico'],
            ['NY', 'New York'],
            ['NC', 'North Carolina'],
            ['ND', 'North Dakota'],
            ['OH', 'Ohio'],  
            ['OK', 'Oklahoma'],  
            ['OR', 'Oregon'],  
            ['PA', 'Pennsylvania'],  
            ['RI', 'Rhode Island'],  
            ['SC', 'South Carolina'],  
            ['SD', 'South Dakota'],
            ['TN', 'Tennessee'],  
            ['TX', 'Texas'],  
            ['UT', 'Utah'],  
            ['VT', 'Vermont'],  
            ['VA', 'Virginia'],  
            ['WA', 'Washington'],  
            ['WV', 'West Virginia'],  
            ['WI', 'Wisconsin'],  
            ['WY', 'Wyoming'],           
	        ['Empty', 'blank']
        ],
        typeAhead: true
    }],
    listeners: {
        beforeRender: function (target) {
            var combo = target.child('combo[name=state]'),
                value = target.record.data.value;

            (value) ? combo.setValue(value) : combo.setValue(''); 
        }
    },
    applyFilter: function() {
        var values = this.getValues(),
        valuesFormat = "",
        formatted = values.state;
        
        if (values.state.length < 2) {
            values.state[0] = values.state[0].replace(/'/g,'');
            formatted = values.state[0].split(',');
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