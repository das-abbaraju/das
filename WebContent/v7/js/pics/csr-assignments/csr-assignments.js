(function ($) {
    PICS.define('csr-assignments.CSRAssignmentsController', {
        methods:{
            init:function () {
                var that = this;
                
                $('.status').on('click', function (event) {
                    that.toggleAccepted(event);
                });
                
               
                $('#save_assignments').on('click', function (event) {
                    that.saveCSRAssignments();
                });
            },
            
            toggleAccepted: function (event) {
                var element = $(event.target);
                var parentDiv = element.closest('div');
                
                if (parentDiv.hasClass('accept')) {
                    parentDiv.removeClass('accept');
                } else {
                    parentDiv.addClass('accept');
                }
                
            },
           saveCSRAssignments: function () {
               var approved = [],
                   accepted_value = $("#accepted"),
                   rejected = [],
                   rejected_value = $("#rejected");
                   
               $('.status').each(function (index, value) {
                   var id = $(this).closest('tr').find('.account_id').html();

                   id = id.trim();

                   if ($(this).closest('.accept').length > 0) {
                       approved.push(id);
                   } else {
                       rejected.push(id);
                   }
                   
               });
               accepted_value.val(approved.join());
               rejected_value.val(rejected.join());
            }
        }
    });
}(jQuery));