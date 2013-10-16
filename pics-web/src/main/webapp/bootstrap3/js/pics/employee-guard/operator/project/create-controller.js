(function ($) {
    PICS.define('employee-guard.operator.project.CreateController', {
        methods: {
            init: function () {
                if ($('#employee_guard_operator_project_create_page').length > 0) {
                    $start_date_picker = $('.start-date .date-picker');
                    $end_date_picker = $('.end-date .date-picker'); 
                    
                    $start_date_picker.datepicker({
                        todayBtn: "linked"
                    });
                    
                    $start_date_picker.on('changeDate', $.proxy(this.changeStartDate, this));

                    $('#operator_project_create_start_year, #operator_project_create_start_month, #operator_project_create_start_day').on('blur', $.proxy(this.blurStartDate, this));
                    
                    $end_date_picker.datepicker({
                        todayBtn: "linked"
                    });
                    
                    $end_date_picker.on('changeDate', $.proxy(this.changeEndDate, this));
                    
                    $('#operator_project_create_end_year, #operator_project_create_end_month, #operator_project_create_end_day').on('blur', $.proxy(this.blurEndDate, this));
                }
            },
            
            blurEndDate: function (event) {
                var $element = $(event.currentTarget),
                    $datepicker = $element.siblings('.date-picker:first'),
                    $year = $('#operator_project_create_end_year'),
                    $month = $('#operator_project_create_end_month'),
                    $day = $('#operator_project_create_end_day'),
                    year = $year.val(),
                    month = $month.val(),
                    day = $day.val();
                
                if (year && month && day) {
                    var date = moment([year, month, day].join('-')).format('YYYY-MM-DD');
                    
                    $datepicker.data('date', date);
                    $datepicker.data('datepicker').update();
                    
                    this.updateEndDate($datepicker.data('datepicker').getFormattedDate());
                }
            },
            
            blurStartDate: function (event) {
                var $element = $(event.currentTarget),
                    $datepicker = $element.siblings('.date-picker:first'),
                    $year = $('#operator_project_create_start_year'),
                    $month = $('#operator_project_create_start_month'),
                    $day = $('#operator_project_create_start_day'),
                    year = $year.val(),
                    month = $month.val(),
                    day = $day.val();
                
                if (year && month && day) {
                    var date = moment([year, month, day].join('-')).format('YYYY-MM-DD');
                    
                    $datepicker.data('date', date);
                    $datepicker.data('datepicker').update();
                    
                    this.updateStartDate($datepicker.data('datepicker').getFormattedDate());
                }
            },
            
            changeEndDate: function (event) {
                var $element = $(event.currentTarget), 
                    date = $element.data('date');
                
                this.updateEndDate(date);
            },
            
            changeStartDate: function (event) {
                var $element = $(event.currentTarget), 
                    date = $element.data('date');
                
                this.updateStartDate(date);
            },
            
            updateEndDate: function (date) {
                var date_split = date.split('-'),
                    $year = $('#operator_project_create_end_year'),
                    $month = $('#operator_project_create_end_month'),
                    $day = $('#operator_project_create_end_day');
                
                $year.val(date_split[0]);
                $month.val(date_split[1]);
                $day.val(date_split[2]);
            },
            
            updateStartDate: function (date) {
                var date_split = date.split('-'),
                    $year = $('#operator_project_create_start_year'),
                    $month = $('#operator_project_create_start_month'),
                    $day = $('#operator_project_create_start_day');
                
                $year.val(date_split[0]);
                $month.val(date_split[1]);
                $day.val(date_split[2]);
            }
        }
    });
}(jQuery));