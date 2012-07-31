Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],

    fields: [{
        name: 'id',
        type: 'int'
    }, {
        name: 'modelType',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'filterExpression',
        type: 'string'
    }, {
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
                    item[field.name] = record.get(field.name);
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

    toQueryString: function () {
        var report = {};

        report.report = this.get('id');
        report['report.description'] = this.get('description');
        report['report.name'] = this.get('name');
        report['report.parameters'] = this.toJson();
        report['report.rowsPerPage'] = this.get('rowsPerPage');

        return Ext.Object.toQueryString(report);
    }
});