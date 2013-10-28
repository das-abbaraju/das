PICS.define('employee-guard.DatePicker', {
    methods: (function () {
        function init() {
            var $date_picker = $('.date-picker');

            $date_picker.datepicker({
                todayBtn: 'linked'
            });

            $date_picker.on('changeDate', changeDate);

            $('.date .year, .date .month, .date .day').on('blur', blurDate);
        }

        function blurDate(event) {
            var $element = $(event.target),
                $row = $element.closest('.row.date'),
                $datepicker = $row.find('.date-picker'),
                $year = $row.find('.year'),
                $month = $row.find('.month'),
                $day = $row.find('.day'),
                year = $year.val(),
                month = $month.val(),
                day = $day.val();

            if (year && month && day) {
                var date = moment([year, month, day].join('-')).format('YYYY-MM-DD');

                $datepicker.data('date', date);
                $datepicker.data('datepicker').update();
            }
        }

        function changeDate(event) {
            var $element = $(event.target),
                date = $element.data('date');

            updateDateFields(date);
        }

        function updateDateFields(date) {
            var date_split = date.split('-'),
                $year = $('.date .year'),
                $month = $('.date .month'),
                $day = $('.date .day');

                $year.val(date_split[0]);
                $month.val(date_split[1]);
                $day.val(date_split[2]);
        }

        return {
            init: init
        };
    }())
});