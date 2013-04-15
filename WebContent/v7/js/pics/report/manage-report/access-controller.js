PICS.define('report.manage-report.AccessController', {
	methods: {
		init: function () {
		    if ($('#ManageReports_access_page').length > 0) {
		        $('#report_access_search_form input').typeahead({
		            source: ['Ancon Marine', 'Don Couch']
		        });
		        
		        $('#group_access')
                    .on('click', '.access-options .edit a', $.proxy(this.assignGroupEditPermission, this))
                    .on('click', '.access-options .view a', $.proxy(this.assignGroupViewPermission, this))
                    .on('click', '.access-options .remove a', $.proxy(this.removeGroupPermission, this))
                    .on('click', '.confirm-options .cancel', $.proxy(this.cancelGroupChanges, this))
                    .on('click', '.confirm-options .remove-permission', $.proxy(this.confirmRemoveGroupPermission, this));
		        
		        $('#user_access')
		            .on('click', '.access-options .owner a', $.proxy(this.assignUserOwner, this))
                    .on('click', '.access-options .edit a', $.proxy(this.assignUserEditPermission, this))
                    .on('click', '.access-options .view a', $.proxy(this.assignUserViewPermission, this))
                    .on('click', '.access-options .remove a', $.proxy(this.removeUserPermission, this))
                    .on('click', '.confirm-options .assign-ownership', $.proxy(this.confirmAssignUserOwner, this))
                    .on('click', '.confirm-options .cancel', $.proxy(this.cancelUserChanges, this))
                    .on('click', '.confirm-options .remove-permission', $.proxy(this.confirmRemoveUserPermission, this));
		    }
		},
		
        assignGroupEditPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group');
            
            // make permission edit ajax
            
            this.resetGroup($group);
            
            $group.addClass('edit');
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        assignGroupViewPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group');
            
            // make permission view ajax
            
            this.resetGroup($group);
            
            $group.addClass('view');
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        assignUserOwner: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                $summary = $user.find('.summary'),
                $info = $summary.find('.info'),
                $description = $summary.find('.description'),
                $access_options = $user.find('.access-options');
            
            $user.addClass('assign');
            
            $description.hide();
            
            $summary.append($('<p class="info"><b>Confirm assigning ownership' + '&hellip;' + '</b></p>'));
            
            $access_options.hide();
            
            // add cancel and assign button
            $user.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="assign-ownership btn btn-warning" data-href="' + $element.attr('href') + '">Assign</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
        
        assignUserEditPermission: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                report_id = $element.data('report-id'),
                user_id = $user.data('user-id');
            
            // make permission edit ajax
            
            PICS.ajax({
                url: 'ManageReports!shareWithEditPermission.action',
                data: {
                    reportId: report_id,
                    toUser: user_id,
                }
            });
            
            this.resetUser($user);
            
            $user.addClass('edit');
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        assignUserViewPermission: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                report_id = $element.data('report-id'),
                user_id = $user.data('user-id');
            
            // make permission view ajax
            
            PICS.ajax({
                url: 'ManageReports!shareWithViewPermission.action',
                data: {
                    reportId: report_id,
                    toUser: user_id,
                }
            });
            
            this.resetUser($user);
            
            $user.addClass('view');
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        cancelGroupChanges: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group');
            
            this.resetGroup($group);
        },
        
        cancelUserChanges: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user');
            
            this.resetUser($user);
        },
        
        confirmAssignUserOwner: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                $edit = $user.find('.edit a'),
                $access_options = $user.find('.access-options');
            
            this.removePreviousOwner();
            
            this.resetUser($user);
            
            $user.addClass('owner');
            
            $access_options.hide();
            
            $user.prepend($([
                '<div class="is-owner pull-right">',
                    '<i class="icon-key"></i> Owner',
                '</div>'
            ].join('')));
            
            this.updateSelectedPermission($edit);
        },
        
        confirmRemoveGroupPermission: function (event) {
            var $element = $(event.currentTarget), 
                $group = $element.closest('.group');
            
            $group.slideUp(400, function () {
                // make permission remove ajax
                
                $(this).remove();
            });
        },
        
        confirmRemoveUserPermission: function (event) {
            var $element = $(event.currentTarget), 
                $user = $element.closest('.user');
            
            $user.slideUp(400, function () {
                // make permission remove ajax
                
                $(this).remove();
            });
        },
        
        removeGroupPermission: function (event) {
            var $element = $(event.currentTarget), 
                $group = $element.closest('.group'),
                $summary = $group.find('.summary'),
                $description = $summary.find('.description'),
                $access_options = $group.find('.access-options');
            
            $group.addClass('remove');
            
            $description.hide();
            
            $summary.append($('<p class="info"><b>Confirm removing access' + '&hellip;' + '</b></p>'));
            
            $access_options.hide();
            
            // add cancel and remove button
            $group.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="remove-permission btn btn-danger" data-href="' + $element.attr('href') + '">Remove</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
        
        removePreviousOwner: function () {
            var $owner = $('#user_access .user.owner'),
                $is_owner = $owner.find('.is-owner'),
                $edit = $owner.find('.edit a');
            
            // make permission edit ajax
            
            $is_owner.remove();
            
            this.resetUser($owner);
            
            $owner.addClass('edit');
            
            this.updateSelectedPermission($edit);
        },
        
        removeUserPermission: function (event) {
            var $element = $(event.currentTarget), 
                $user = $element.closest('.user'),
                $summary = $user.find('.summary'),
                $description = $summary.find('.description'),
                $access_options = $user.find('.access-options');
            
            $user.addClass('remove');
            
            $description.hide();
            
            $summary.append($('<p class="info"><b>Confirm removing access' + '&hellip;' + '</b></p>'));
            
            $access_options.hide();
            
            // add cancel and remove button
            $user.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="remove-permission btn btn-danger" data-href="' + $element.attr('href') + '">Remove</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
        
        resetGroup: function ($group) {
            var $summary = $group.find('.summary'),
                $info = $summary.find('.info'),
                $description = $summary.find('.description'),
                $confirm_options = $group.find('.confirm-options'),
                $access_options = $group.find('.access-options');
            
            $info.remove();
            
            $description.show();
            
            $confirm_options.remove();
            
            $access_options.show();
            
            $group.removeClass('remove edit view');
        },
        
        resetUser: function ($user) {
            var $summary = $user.find('.summary'),
                $info = $summary.find('.info'),
                $description = $summary.find('.description'),
                $confirm_options = $user.find('.confirm-options'),
                $access_options = $user.find('.access-options');
            
            $info.remove();
            
            $description.show();
            
            $confirm_options.remove();
            
            $access_options.show();
            
            $user.removeClass('remove assign edit view owner');
        },
		
		updateSelectedPermission: function ($element) {
            var $item = $element.html(),
                $btn_group = $element.closest('.access-options'),
                $dropdown_toggle = $btn_group.find('.dropdown-toggle');
            
            $dropdown_toggle.html($item + ' ').append($('<span class="caret">'));
		}
	}
});