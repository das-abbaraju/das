(function($) {
    PICS.define('employee.ManageJobRoles', {
        methods : {
            init : function() {
                var element = $('.ManageJobRoles-page');
    
                if (element.length) {
                    this.setUpAutocomplete();
    
                    element.delegate('.role-link', 'click', { manageJobRoles : this }, this.loadJobRole);
                    element.delegate('#role_form .negative', 'click', this.deleteJobRole);
                }
            },
    
            deleteJobRole: function(event) {
                return confirm(translate('JS.ManageJobRoles.confirm.RemoveJobRole'));
            },
    
            loadJobRole: function(event) {
                var manageJobRoles = event.data.manageJobRoles;
    
                var account = $(this).attr('data-account');
                var audit = $(this).attr('data-audit');
                var questionId = $(this).attr('data-questionId');
                var role = $(this).attr('data-role');
    
                var data = {
                    account : account,
                    audit : audit,
                    questionId : questionId
                };
    
                if (role) {
                    data['role'] = role;
                }
    
                $('#edit_role').html('<img src="images/ajax_process.gif" alt="'
                    + translate('JS.ManageJobRoles.message.LoadingJobRole') + '" /> '
                    + translate('JS.ManageJobRoles.message.LoadingJobRole'));
    
                PICS.ajax({
                    url : 'ManageJobRoles!get.action',
                    data : data,
                    success : function(data, textStatus, XMLHttpRequest) {
                        $('#edit_role').html(data);
    
                        manageJobRoles.setUpAutocomplete();
                    }
                });
            },
    
            setUpAutocomplete : function() {
                $('#role_name').autocomplete(
                    'RoleSuggestAjax.action',
                    {
                        minChars : 1,
                        formatItem : function(data, i,
                                count) {
                            return data[1];
                        }
                    }
                );
            }
        }
    });
})(jQuery);