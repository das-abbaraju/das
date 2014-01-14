(function ($) {
    PICS.define('widget.chart.StackedColumnChart', {
        extend: 'widget.chart.Chart',

        methods: {
            init: function () {
                this.style_type = 'StackedColumn';

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            },

            getChartOptions: function (data_table) {
                var options = {};

                options.isStacked = true;

                return options;
            }
        }
    });
}(jQuery));