(function ($) {
    PICS.define('widget.chart.custom-color-chart.flags-chart.StackedFlagsChart', {
        extend: 'widget.chart.custom-color-chart.FlagsChart',

        methods: {
            init: function () {
                this.style_type = 'StackedFlags';

                this.loadCharts();
            },

            getChartOptions: function (data_table) {
                var options = {};

                options.isStacked = true;

                return options;
            }
        }
    });
}(jQuery));