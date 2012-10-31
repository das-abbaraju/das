(function($) {
    PICS.define('employee.Competencies', {
        methods : {
            init : function() {
                var element = $('.EmployeeCompetencies-page');
                if (element.length) {
                    element.delegate('.preview', 'click', this.toggleCompetencyMatrix);
                    element.delegate('#report_data input[type=checkbox]', 'click', this.updateCompetency);
                }
            },
            
            toggleCompetencyMatrix: function(event) {
                $('#job_competency_matrix').toggle();
            },
            
            updateCompetency: function(event) {
                var checkbox = $(this);
                var checked = $(this).is(":checked");
                var account = $(this).attr('data-account');
                var audit = $(this).attr('data-audit');
                var competency = $(this).attr('data-competency');
                var employee = $(this).attr('data-employee');
                
                PICS.ajax({
                    url: 'EmployeeCompetencies!changeCompetency.action',
                    data: {
                        account: account,
                        audit: audit,
                        competency: competency,
                        employee: employee,
                        skilled: checked
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $("#messages").html(data);
                        
                        if (checked) {
                            checkbox.closest('td').removeClass('red').addClass('green');
                        } else {
                            checkbox.closest('td').removeClass('green').addClass('red');
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        checkbox.attr('checked', !checked);
                    }
                });
            }
        }
    });
})(jQuery);