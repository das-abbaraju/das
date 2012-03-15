Ext.define('PICS.view.report.filter.StateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.statefilter'],

    id: 'test',
    items: [{
        xtype: 'panel',
        name: 'title'
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
        beforeRender: function () {
            var form = Ext.ComponentQuery.query('statefilter')[0],
                combo = form.child("combo"),
                value = form.record.data.value;

            (value) ? combo.setValue(value) : combo.setValue(''); 
        }
    },
    applyFilter: function() {
        var values = this.getValues();
        
        this.record.set('value', values.state);
        this.record.set('operator', 'Equals');
        this.superclass.applyFilter();
    }    
});