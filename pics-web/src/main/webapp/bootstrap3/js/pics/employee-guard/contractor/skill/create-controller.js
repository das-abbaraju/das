// (function ($) {
//     PICS.define('employe-guard.contractor.skill.CreateController', {
//         methods: {
//             init: function () {
//                 var $employee_guard_contractor_skill_create_page = $('#employee_guard_contractor_skill_create_page'),
//                     $employee_guard_contractor_skill_insert_page = $('#employee_guard_contractor_skill_insert_page');

//                 if ($employee_guard_contractor_skill_create_page.length > 0) {
//                     $employee_guard_contractor_skill_create_page
//                         .on('change', '#contractor_skill_create_skillType', $.proxy(this.onTypeChange, this))
//                         .on('click', '#contractor_skill_create_required', this.onRequiredForAllEmployeesClick);
//                 } else if ($employee_guard_contractor_skill_insert_page.length > 0) {
//                     $employee_guard_contractor_skill_insert_page
//                         .on('change', '#contractor_skill_create_skillType', $.proxy(this.onTypeChange, this))
//                         .on('click', '#contractor_skill_create_required', this.onRequiredForAllEmployeesClick);
//                 }
//             },

//             onTypeChange: function (event) {
//                 var $element = $(event.currentTarget),
//                     $form = $element.closest('form');

//                 PICS.ajax({
//                     url: document.location.href,
//                     type: 'GET',
//                     data: $form.serialize(),
//                     success: function (data, textStatus, jqXHR) {
//                         $form.replaceWith(data);
//                     }
//                 });
//             },

//             onRequiredForAllEmployeesClick: function (event) {
//                 var $element = $(event.currentTarget),
//                     $contractor_skill_create_groups = $('#contractor_skill_create_groups');

//                 if ($element.is(':checked')) {
//                     $contractor_skill_create_groups.attr('disabled', true);
//                 } else {
//                     $contractor_skill_create_groups.attr('disabled', null);
//                 }
//             }
//         }
//     });
// }(jQuery));