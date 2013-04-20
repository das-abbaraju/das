PICS.define('report.manage-report.AccessController', {
    methods: {
        jqXHR: null,

        init: function () {
            if ($('#ManageReports_access_page').length > 0) {
                this.configureShareSearch();

                $('#report_access_container')
                    .on('click', '#group_access .access-options .edit a', $.proxy(this.assignGroupEditPermission, this))
                    .on('click', '#group_access .access-options .view a', $.proxy(this.assignGroupViewPermission, this))
                    .on('click', '#group_access .access-options .remove a', $.proxy(this.removeGroupPermission, this))
                    .on('click', '#group_access .confirm-options .cancel', $.proxy(this.cancelGroupChanges, this))
                    .on('click', '#group_access .confirm-options .remove-permission', $.proxy(this.confirmRemoveGroupPermission, this))
                    .on('click', '#user_access .access-options .owner a', $.proxy(this.assignUserOwner, this))
                    .on('click', '#user_access .access-options .edit a', $.proxy(this.assignUserEditPermission, this))
                    .on('click', '#user_access .access-options .view a', $.proxy(this.assignUserViewPermission, this))
                    .on('click', '#user_access .access-options .remove a', $.proxy(this.removeUserPermission, this))
                    .on('click', '#user_access .confirm-options .assign-ownership', $.proxy(this.confirmAssignUserOwner, this))
                    .on('click', '#user_access .confirm-options .cancel', $.proxy(this.cancelUserChanges, this))
                    .on('click', '#user_access .confirm-options .remove-permission', $.proxy(this.confirmRemoveUserPermission, this));
            }
        },

        assignGroupEditPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group'),
                report_id = $element.data('report-id'),
                account_id = $group.data('account-id'),
                group_id = $group.data('group-id');
            
            if (account_id) {
                this.shareEditPermissionWithAccount({
                    report_id: report_id,
                    account_id: account_id
                });
            } else if (group_id) {
                this.shareEditPermissionWithGroup({
                    report_id: report_id,
                    group_id: group_id
                });
            }

            this.resetGroup($group);

            $group.addClass('edit');

            this.updateSelectedPermission($element);

            event.preventDefault();
        },

        assignGroupViewPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group'),
                report_id = $element.data('report-id'),
                account_id = $group.data('account-id'),
                group_id = $group.data('group-id');
            
            if (account_id) {
                this.shareViewPermissionWithAccount({
                    report_id: report_id,
                    account_id: account_id
                });
            } else if (group_id) {
                this.shareViewPermissionWithGroup({
                    report_id: report_id,
                    group_id: group_id
                });
            }

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
                $location = $summary.find('.location'),
                $access_options = $user.find('.access-options');

            $user.addClass('assign');

            $location.hide();

            $summary.append($('<p class="info"><b>Confirm assigning ownership' + '&hellip;' + '</b></p>'));

            $access_options.hide();

            // add cancel and assign button
            $user.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="assign-ownership btn btn-warning" data-href="' + $element.attr('href') + '" data-report-id="' + $element.data('report-id') + '">Assign</button>',
                '</div>'
            ].join('')));

            event.preventDefault();
        },

        assignUserEditPermission: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                report_id = $element.data('report-id'),
                user_id = $user.data('user-id');

            this.shareEditPermissionWithUser({
                report_id: report_id,
                user_id: user_id
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

            this.shareViewPermissionWithUser({
                report_id: report_id,
                user_id: user_id
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

        configureShareSearch: function () {
            var $element = $('#report_access_search_form input'),
                that = this;

            $element.typeahead({
                items: 10,
                source: PICS.debounce($.proxy(this.shareSearch, this), 350),
                menu: '<ul id="share_search_list" class="typeahead dropdown-menu dropdown-striped"></ul>',
                item: '<li><a href="#"></a></li>'
            });

            $element.data('typeahead').process = function (items) {
                return this.render(items.slice(0, this.options.items)).show();
            };

            $element.data('typeahead').render = function (items) {
                var that = this;

                items = $(items).map(function (i, item) {
                    i = $(that.options.item).data({
                        id: this.id,
                        location: this.location,
                        name: this.name,
                        type: this.type,
                        'access-type': this.access_type
                    });

                    i.find('a').html([
                        '<div class="clearfix">',
                            '<span class="id">' + this.id + '</span>',
                            '<span class="name" title="' + this.name + '">' + this.name + '</span>',
                        '</div>',
                        '<div class="clearfix">',
                            '<span class="type">' + this.type + '</span>',
                            '<span class="location">' + this.location + '</span>',
                        '</div>'
                    ].join(''));

                    return i[0];
                });

                items.first().addClass('active');

                this.$menu.html(items);

                return this;
            };

            $element.data('typeahead').select = function () {
                var $selected = this.$menu.find('.active'),
                    report_id = $element.data('report-id'),
                    id = $selected.data('id'),
                    type = $selected.data('access-type');

                switch (type) {
                    case 'account':
                        that.shareViewPermissionWithAccount({
                            report_id: report_id,
                            account_id: id,
                            success: that.refreshAccess
                        });

                        break;
                    case 'group':
                        that.shareViewPermissionWithGroup({
                            report_id: report_id,
                            group_id: id,
                            success: that.refreshAccess
                        });

                        break;
                    case 'user':
                    default:
                        that.shareViewPermissionWithUser({
                            report_id: report_id,
                            user_id: id,
                            success: that.refreshAccess
                        });

                        break;
                }


                return this.hide();
            };
        },

        confirmAssignUserOwner: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                report_id = $element.data('report-id'),
                user_id = $user.data('user-id');

            this.transferOwnership({
                report_id: report_id,
                user_id: user_id,
                success: this.refreshAccess
            });
        },

        confirmRemoveGroupPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group'),
                report_id = $element.data('report-id'),
                account_id = $group.data('account-id'),
                group_id = $group.data('group-id'),
                that = this;

            $group.slideUp(400, function () {
                if (account_id) {
                    that.unshareWithAccount({
                        report_id: report_id,
                        account_id: account_id
                    })
                } else if (group_id) {
                    that.unshareWithGroup({
                        report_id: report_id,
                        group_id: group_id
                    });
                }

                $(this).remove();
            });
        },

        confirmRemoveUserPermission: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                report_id = $element.data('report-id'),
                user_id = $user.data('user-id'),
                that = this;

            $user.slideUp(400, function () {
                that.unshareWithUser({
                    report_id: report_id,
                    user_id: user_id
                });

                $(this).remove();
            });
        },

        refreshAccess: function () {
            PICS.ajax({
                url: window.location.href,
                success: function (data, textStatus, jqXHR) {
                    $('#report_access_container').html(data);
                }
            });
        },

        removeGroupPermission: function (event) {
            var $element = $(event.currentTarget),
                $group = $element.closest('.group'),
                $summary = $group.find('.summary'),
                $location = $summary.find('.location'),
                $access_options = $group.find('.access-options');

            $group.addClass('remove');

            $location.hide();

            $summary.append($('<p class="info"><b>Confirm removing access' + '&hellip;' + '</b></p>'));

            $access_options.hide();

            // add cancel and remove button
            $group.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="remove-permission btn btn-danger" data-href="' + $element.attr('href') + '" data-report-id="' + $element.data('report-id') + '">Remove</button>',
                '</div>'
            ].join('')));

            event.preventDefault();
        },

        removeUserPermission: function (event) {
            var $element = $(event.currentTarget),
                $user = $element.closest('.user'),
                $summary = $user.find('.summary'),
                $location = $summary.find('.location'),
                $access_options = $user.find('.access-options');

            $user.addClass('remove');

            $location.hide();

            $summary.append($('<p class="info"><b>Confirm removing access' + '&hellip;' + '</b></p>'));

            $access_options.hide();

            // add cancel and remove button
            $user.prepend($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="cancel btn">Cancel</button>',
                    '<button class="remove-permission btn btn-danger" data-href="' + $element.attr('href') + '" data-report-id="' + $element.data('report-id') + '">Remove</button>',
                '</div>'
            ].join('')));

            event.preventDefault();
        },

        resetGroup: function ($group) {
            var $summary = $group.find('.summary'),
                $info = $summary.find('.info'),
                $location = $summary.find('.location'),
                $confirm_options = $group.find('.confirm-options'),
                $access_options = $group.find('.access-options');

            $info.remove();

            $location.show();

            $confirm_options.remove();

            $access_options.show();

            $group.removeClass('remove edit view');
        },

        resetUser: function ($user) {
            var $summary = $user.find('.summary'),
                $info = $summary.find('.info'),
                $location = $summary.find('.location'),
                $confirm_options = $user.find('.confirm-options'),
                $access_options = $user.find('.access-options');

            $info.remove();

            $location.show();

            $confirm_options.remove();

            $access_options.show();

            $user.removeClass('remove assign edit view');
        },

        shareEditPermissionWithAccount: function (options) {
            var report_id = options.report_id,
                account_id = options.account_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithAccountEditPermission.action',
                data: {
                    reportId: report_id,
                    shareId: account_id
                },
                success: success,
                error: error
            });
        },

        shareEditPermissionWithGroup: function (options) {
            var report_id = options.report_id,
                group_id = options.group_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithGroupEditPermission.action',
                data: {
                    reportId: report_id,
                    shareId: group_id
                },
                success: success,
                error: error
            });
        },

        shareEditPermissionWithUser: function (options) {
            var report_id = options.report_id,
                user_id = options.user_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithUserEditPermission.action',
                data: {
                    reportId: report_id,
                    shareId: user_id
                },
                success: success,
                error: error
            });
        },

        shareSearch: function (query, process) {
            var $element = $('#report_access_search_form input'),
                report_id = $element.data('report-id'),
                that = this;

            if (this.jqXHR && typeof this.jqXHR.abort == 'function') {
                this.jqXHR.abort();
            }

            this.jqXHR = PICS.ajax({
                url: 'Autocompleter!reportSharingAutocomplete.action',
                dataType: 'json',
                data: {
                    reportId: report_id,
                    searchQuery: query
                },
                success: function (data, textStatus, jqXHR) {
                    process(data);

                    that.jqXHR = null;
                }
            });
        },

        shareViewPermissionWithAccount: function (options) {
            var report_id = options.report_id,
                account_id = options.account_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function'? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithAccountViewPermission.action',
                data: {
                    reportId: report_id,
                    shareId: account_id
                },
                success: success,
                error: error
            });
        },

        shareViewPermissionWithGroup: function (options) {
            var report_id = options.report_id,
                group_id = options.group_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function'? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithGroupViewPermission.action',
                data: {
                    reportId: report_id,
                    shareId: group_id
                },
                success: success,
                error: error
            });
        },

        shareViewPermissionWithUser: function (options) {
            var report_id = options.report_id,
                user_id = options.user_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function'? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!shareWithUserViewPermission.action',
                data: {
                    reportId: report_id,
                    shareId: user_id
                },
                success: success,
                error: error
            });
        },

        transferOwnership: function (options) {
            var report_id = options.report_id,
                user_id = options.user_id,
                success = typeof options.success == 'function' ? options.success: function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!transferOwnership.action',
                data: {
                    reportId: report_id,
                    shareId: user_id
                },
                success: success,
                error: error
            });
        },

        unshareWithAccount: function (options) {
            var report_id = options.report_id,
                account_id = options.account_id,
                success = typeof options.success == 'function' ? options.success: function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!unshareAccount.action',
                data: {
                    reportId: report_id,
                    shareId: account_id
                }
            });
        },

        unshareWithGroup: function (options) {
            var report_id = options.report_id,
                group_id = options.group_id,
                success = typeof options.success == 'function' ? options.success: function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!unshareGroup.action',
                data: {
                    reportId: report_id,
                    shareId: group_id
                }
            });
        },

        unshareWithUser: function (options) {
            var report_id = options.report_id,
                user_id = options.user_id,
                success = typeof options.success == 'function' ? options.success: function () {},
                error = typeof options.error == 'function' ? options.error : function () {};

            PICS.ajax({
                url: 'ManageReports!unshareUser.action',
                data: {
                    reportId: report_id,
                    shareId: user_id
                }
            });
        },

        updateSelectedPermission: function ($element) {
            var $item = $element.html(),
                $btn_group = $element.closest('.access-options'),
                $dropdown_toggle = $btn_group.find('.dropdown-toggle');

            $dropdown_toggle.html($item + ' ').append($('<span class="caret">'));
        }
    }
});