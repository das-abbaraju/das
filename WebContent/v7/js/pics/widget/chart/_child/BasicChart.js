(function ($) {
    PICS.define('widget.chart.BasicChart', {
        extend: 'widget.chart.Chart',

        methods: {
            init: function () {
                this.style_type = 'Basic';

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            }
        }
    });
}(jQuery));