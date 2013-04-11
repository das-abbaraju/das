PICS.define('report.manage-report.ShareController', {
	methods: {
		init: function () {
		    if ($('#ManageReports_share_page').length > 0) {
		        $('#report_share_search_form input').typeahead({
		            source: ['Ancon Marine', 'Don Couch']
		        });
		        
		        $('#group_access')
                    .on('click', '.access-options .permission.edit', $.proxy(this.assignGroupViewPermission, this))
                    .on('click', '.access-options .permission.view', $.proxy(this.assignGroupEditPermission, this))
                    .on('click', '.access-options .permission.remove', $.proxy(this.removeGroupPermission, this))
                    .on('click', '.confirm-options .cancel', $.proxy(this.cancelGroupChanges, this))
                    .on('click', '.confirm-options .remove-permission', $.proxy(this.confirmRemoveGroupPermission, this));
		        
		        $('#user_access')
		            .on('click', '.access-options .permission.owner', $.proxy(this.assignUserOwner, this))
                    .on('click', '.access-options .permission.edit', $.proxy(this.assignUserViewPermission, this))
                    .on('click', '.access-options .permission.view', $.proxy(this.assignUserEditPermission, this))
                    .on('click', '.access-options .permission.remove', $.proxy(this.removeUserPermission, this))
                    .on('click', '.confirm-options .assign-ownership', $.proxy(this.confirmAssignUserOwner, this))
                    .on('click', '.confirm-options .cancel', $.proxy(this.cancelUserChanges, this))
                    .on('click', '.confirm-options .remove-permission', $.proxy(this.confirmRemoveUserPermission, this));
		    }
		},
		
        assignGroupEditPermission: function (event) {
            var $element = $(event.currentTarget);
            
            // make permission edit ajax
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        assignGroupViewPermission: function (event) {
            var $element = $(event.currentTarget);
            
            // make permission view ajax
            
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
                    '<button class="assign-ownership btn btn-info" data-href="' + $element.attr('href') + '">Assign</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
        
        assignUserEditPermission: function (event) {
            var $element = $(event.currentTarget);
            
            // make permission edit ajax
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        assignUserViewPermission: function (event) {
            var $element = $(event.currentTarget);
            
            // make permission view ajax
            
            this.updateSelectedPermission($element);
            
            event.preventDefault();
        },
        
        cancelGroupChanges: function (event) {
            var $element = $(event.currentTarget), 
                $group = $element.closest('.group'),
                $summary = $group.find('.summary'),
                $info = $summary.find('.info'),
                $description = $summary.find('.description'),
                $confirm_options = $group.find('.confirm-options'),
                $access_options = $group.find('.access-options');
            
            $info.remove();
            
            $description.show();
            
            $confirm_options.remove();
            
            $access_options.show();
            
            $group.removeClass('remove');
        },
        
        cancelUserChanges: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                $summary = $user.find('.summary'),
                $info = $summary.find('.info'),
                $description = $summary.find('.description'),
                $confirm_options = $user.find('.confirm-options'),
                $access_options = $user.find('.access-options');
            
            $info.remove();
            
            $description.show();
            
            $confirm_options.remove();
            
            $access_options.show();
            
            $user.removeClass('remove assign');
        },
        
        confirmAssignUserOwner: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                $access_options = $user.find('.access-options');
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
                // remove ajax
                
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
                    '<button class="remove-permission btn btn-warning" data-href="' + $element.attr('href') + '">Remove</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
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
                    '<button class="remove-permission btn btn-warning" data-href="' + $element.attr('href') + '">Remove</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
		
		updateSelectedPermission: function ($element) {
            var $item = $element.html(),
                $btn_group = $element.closest('.access-options'),
                $dropdown_toggle = $btn_group.find('.dropdown-toggle');
            
            $dropdown_toggle.html($item + ' ').append($('<span class="caret">'));
		}
	}
});