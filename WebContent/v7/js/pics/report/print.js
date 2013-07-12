(function ($) {
    PICS.define('report.Print', {
        methods: {
            init: function () {
                $('.print-date p').html(this.getDateString());

                window.print();
            },

            getDateString: function () {
                var date = new Date(),
                    month = date.getMonth() + 1,
                    month_string = month < 10 ? '0' + month : month,
                    minutes = date.getMinutes(),
                    minutes_string = minutes < 10 ? '0' + minutes : minutes;

                return date.getFullYear() + '-' + month_string + '-' + date.getDate() + ' @ ' + date.getHours() + ':' + minutes_string;
            }
        }
    });
}(jQuery));