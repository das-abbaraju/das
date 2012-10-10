Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],

    fields: [{
        // report id
        name: 'id',
        type: 'int'
    }, {
        // report base (aka mysql view)
        name: 'modelType',
        type: 'string'
    }, {
        // report name
        name: 'name',
        type: 'string'
    }, {
        // report description
        name: 'description',
        type: 'string'
    }, {
        // query expression used to generate report data aka (1 AND 2) OR 3
        name: 'filterExpression',
        type: 'string'
    }, {
        // report limit
        name: 'rowsPerPage',
        type: 'int'
    }],
    hasMany: [{
        model: 'PICS.model.report.Column',
        name: 'columns'
    }, {
        model: 'PICS.model.report.Filter',
        name: 'filters'
    }, {
        model: 'PICS.model.report.Sort',
        name: 'sorts'
    }],

    /**
     * Get Report JSON
     *
     * Builds a jsonified version of the report to be sent to the server
     */
    toJson: function () {
        var report = {};

        function convertStoreToDataObject(store) {
            var data = [];

            store.each(function (record) {
                var item = {};
                
                record.fields.each(function (field) {
                    // block to prevent extraneous id from being inject into request parameters
                    // ???
                    if (record.get(field.name)) {
                        item[field.name] = record.get(field.name);
                    }
                });
                
                data.push(item);
            });

            return data;
        }

        report = this.data;
        report.columns = convertStoreToDataObject(this.columns());
        report.filters = convertStoreToDataObject(this.filters());
        report.sorts = convertStoreToDataObject(this.sorts());

        return Ext.encode(report);
    },

    toRequestParams: function () {
        var report = {};

        report.report = this.get('id');
        report['report.description'] = this.get('description');
        report['report.name'] = this.get('name');
        report['report.parameters'] = this.toJson();
        report['report.rowsPerPage'] = this.get('rowsPerPage');

        return report;
    }
});