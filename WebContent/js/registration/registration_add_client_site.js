(function ($) {
    PICS.define('registration.RegistrationAddClientSite', {
        methods: {
            init: function () {
                if ($('.RegistrationAddClientSite-page').length) {
                    var that = this;

                    // ajax filter
                    $('.client-site-filter').bind('submit', this.search);

                    // suggest button in selected client sites
                    $('.client-site-right').delegate('.suggest-client-site', 'click', this.suggest);

                    $('.show-all-client-site').bind('click', this.show_all);

                    // add client sites - ignore disabled to prevent multiple ajax requests
                    $('.client-site-left').delegate('.client-site-list a:not(.disable)', 'click', function (event) {
                        that.add_client_site.apply(that, [event]);
                    });

                    // info client sites
                    $('.client-site-left, .client-site-right').delegate('.client-site-list .info', 'click', this.info);

                    // remove client sites - ignore disabled to prevent multiple ajax requests
                    $('.client-site-right').delegate('.client-site-list a:not(.disable)', 'click', function (event) {
                        that.remove_client_site.apply(that, [event]);
                    });
                }
            },

            add_client_site: function (event) {
                var that = this;
                var element = $(event.target).closest('a');

                var client_site = element.closest('li');
                var client_site_id = element.attr('data-id');
                var requires_general_contractor_modal = element.attr('data-requires-general-contractor-modal');

                if (requires_general_contractor_modal === "Yes") {
                    that.general_contractor = {
                            id: element.attr('data-id'),
                            name: client_site.find('.name').text(),
                            location: client_site.find('.location').text()
                    };
                }

                if (requires_general_contractor_modal) {
                    this.show_general_contractor_modal(element);

                    return false;
                }

                // disable client site add - remove it from delegated event list
                element.addClass('disable');

                // fade element out
                client_site.fadeOut(500, function () {

                    // Remove the added site's list item from the available sites' node tree.
                    client_site.remove();

                    that.trigger_client_help_removal();

                    that.add(client_site_id, function () {
                        that.add_to_selected_client_site_list(client_site);
                    });
                });

            },

            add_to_client_site_list: function (client_site, client_site_list) {
                var that = this;

                // Get the names of the client sites currently in the target list.
                var client_sites = client_site_list.find('li');
                var client_site_names = [];
                client_sites.each(function (key, value) {
                    client_site_names[key] = $(this).find('a .name').text();
                });

                // Get the name of the client site to be added.
                var client_site_name = client_site.find('a .name').text();

                // Is the client site already in the list?
                var existingIndex = $.inArray(client_site_name, client_site_names);

                // If not...
                if (existingIndex == -1) {

                    // Add the site name to the array of existing site names.
                    client_site_names.push(client_site_name);

                    // Sort the array alphabetically.
                    client_site_names.sort();

                    // Identify the index of the newly added site name within the sorted array.
                    var index = $.inArray(client_site_name, client_site_names);

                    // Insert the client site within the array of client sites
                    // (The object array is already sorted alphabetically.)
                    if (client_sites.eq(index).length > 0) {
                        // ...immediately before that index if that index is currently occupied
                        client_sites.eq(index).before(client_site);
                    } else {
                        // ...or at that same index, if it is not.
                        client_site_list.append(client_site);
                    }

                    // Re-enable client site add.
                    client_site.find('a').removeClass('disable');
                }
            },

            add_to_available_client_site_list: function (client_site) {
                var that = this;
                var client_site = client_site.clone();
                var add_text = translate('JS.RegistrationAddClientSite.AddSite');

                that.add_to_client_site_list(client_site, $('.client-site-left .client-site-list'));

                // update element to have remove instead of add
                client_site.find('.remove').replaceWith($('<span class="add btn success">+ ' + add_text + '</span>'));

                if ($.browser.msie) {
                    $('a:not(.disable) .add', client_site).bind('click', function (event) {
                        that.add_client_site.apply(that, [event]);
                    });
                }

                client_site.show();
            },

            add_to_selected_client_site_list: function (client_site) {
                var that = this;
                var client_site = client_site.clone();
                var remove_text = translate('JS.RegistrationAddClientSite.RemoveSite');

                that.add_to_client_site_list(client_site, $('.client-site-right .client-site-list'));

                // update element to have remove instead of add
                client_site.find('.add').replaceWith($('<span class="remove btn error">- ' + remove_text + '</span>'));

                if ($.browser.msie) {
                    $('a:not(.disable) .remove', client_site).bind('click', function (event) {
                        that.remove_client_site.apply(that, [event]);
                    });
                }

                client_site.show();
            },

            add: function (client_site_id, success_callback) {
                // ajax request to save operator
                PICS.ajax({
                    url: 'RegistrationAddClientSite!ajaxAdd.action',
                    data: {
                        operator: client_site_id
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        if (typeof success_callback == 'function') {
                            success_callback();
                        }
                    }
                });
            },

            info: function (event) {
                event.stopPropagation();

                var element = $(this).closest('a');
                var container = element.closest('li');

                container.siblings('li.inspect').removeClass('inspect');

                container.toggleClass('inspect');
            },

            remove_client_site: function (event) {
                var that = this;
                var element = $(event.target).closest('a');

                var sites_selected_container = $('.client-site-right');
                var num_sites_selected = sites_selected_container.find('li:visible').length - 1;

                var client_site = element.closest('li');
                var client_site_id = element.attr('data-id');

                // disable client site remove - remove it from delegated event list
                element.addClass('disable');

                // fade element out
                client_site.fadeOut(500, function () {

                    // Remove the removed site's list item from the selected sites' node tree.
                    client_site.remove();

                    // show help message if all client sites have been removed
                    if (num_sites_selected < 1) {
                        sites_selected_container.fadeOut(250, function () {
                            $('.client-site-get-started').fadeIn(250);
                        });
                    }

                    // show suggest if there are less than 4 client sites
                    if (num_sites_selected < 4) {
                        $('.client-site-help').fadeIn(250);
                    }

                    that.remove(client_site_id, function () {
                        that.add_to_available_client_site_list(client_site);
                    });
                });

            },

            remove: function (client_site_id, success_callback) {
                // ajax request to save operator
                PICS.ajax({
                    url: 'RegistrationAddClientSite!ajaxRemove.action',
                    data: {
                        operator: client_site_id
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        if (typeof success_callback == 'function') {
                            success_callback();
                        }
                    }
                });
            },

            search: function (event) {
                // prevent filter from submitting
                event.preventDefault();

                var form = $(this);

                // check if the form is disabled - if so don't execute anything
                if (form.hasClass('disable')) {
                    return false;
                }

                // disable the form submission
                form.addClass('disable');

                // send request to search method
                var data = form.serialize() + '&method%3Asearch="Search"';
                var list = $('.client-site-left .client-site-list');

                // loading icon
                list.addClass('loading');
                list.closest('.client-site-list-container').append('<span class="loading"></span>');

                PICS.ajax({
                    url: form.attr('action'),
                    data: data,
                    success: function (data, textStatus, XMLHttpRequest) {
                        list.siblings('span.loading').remove();

                        $('.client-site-left .client-site-list').replaceWith(data);

                        // re-enable form submission
                        form.removeClass('disable');
                    }
                });
            },

            show_all: function (event) {
                var form = $('client-site-filter');

                // send request to search method
                var data = 'searchValue=*&method%3Asearch="Search"';
                var list = $('.client-site-left .client-site-list');

                // loading icon
                list.addClass('loading');
                list.closest('.client-site-list-container').append('<span class="loading"></span>');

                PICS.ajax({
                    url: form.attr('action'),
                    data: data,
                    success: function (data, textStatus, XMLHttpRequest) {
                        list.siblings('span.loading').remove();

                        list.replaceWith(data);
                    }
                });
            },

            show_general_contractor_modal: function (client_site_element) {
                var that = this;

                var client_site_id = client_site_element.attr('data-id');
                var client_site_name = $('.name', client_site_element).text();
                var general_contractor_id = client_site_element.attr('data-general-contractor-id');

                var modal_title = translate('JS.RegistrationAddClientSite.' +
                        (general_contractor_id == client_site_id ? 'SelectedClientIsGC' : 'SelectedClientHasGC'),
                        [client_site_name]);

                function createModal(title, content, client_site_name) {
                    var modal = PICS.modal({
                        modal_class: 'modal client-site-modal client-site',
                        title: title,
                        content: content,
                        buttons: [
                            {
                                html: '<a href="javascript:;" class="btn danger">' + translate('JS.button.Close') + '</a>',
                                callback: function() { PICS.getClass('modal.Modal').hide(); }
                            },
                            {
                                html: '<a href="javascript:;" class="btn only-work-for">' + translate('JS.RegistrationAddClientSite.OnlyWorkFor', [client_site_name]) + '</a>'
                            }
                        ]
                    });

                    return modal;
                }

                function initializeModalEvents(config) {
                    var modal = config && config.modal;
                    var general_contractor_element = config && config.general_contractor_element;

                    var id = general_contractor_element.attr('data-id');
                    var general_contractor_id = general_contractor_element.attr('data-general-contractor-id');
                    var requires_site_selection = general_contractor_element.attr('data-requires-site-selection');

                    if (requires_site_selection) {
                        modal.find('.danger').hide();
                        modal.find('.only-work-for').hide();
                    }

                    modal.delegate('.success', 'click', function () {
                        var element = $(this).closest('a');

                        var client_site = element.closest('li');
                        var client_site_id = element.attr('data-id');

                        that.add(client_site_id, function () {
                            that.add_to_selected_client_site_list(client_site);
                            that.trigger_client_help_removal();

                            client_site.find('a .add').remove();
                        });

                        if (general_contractor_id && client_site_id != general_contractor_id) {
                            that.add(general_contractor_id, function () {
                                that.add_to_selected_client_site_list(general_contractor_element.closest('li'));
                                that.trigger_client_help_removal();
                            });
                        }

                        general_contractor_element.closest('li').hide();

                        modal.find('.danger').show();
                    });

                    modal.delegate('.only-work-for', 'click', function () {
                        that.add(id, function () {
                            that.add_to_selected_client_site_list(general_contractor_element.closest('li'));
                            that.trigger_client_help_removal();
                        });

                        general_contractor_element.closest('li').hide();

                        PICS.getClass('modal.Modal').hide();
                    });

                    modal.delegate('.already-selected', 'click', function () {
                        that.add(general_contractor_id, function () {
                            that.add_to_selected_client_site_list(general_contractor_element.closest('li'));
                            that.trigger_client_help_removal();
                        });

                        general_contractor_element.closest('li').hide();

                        PICS.getClass('modal.Modal').hide();
                    });
                }

                PICS.ajax({
                    url: 'RegistrationAddClientSite!clientList.action',
                    data: {
                        generalContractor: client_site_id
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        var title = modal_title;
                        var modal = createModal(title, data, client_site_name);

                        modal.show();

                        initializeModalEvents({
                            modal: modal.getElement(),
                            general_contractor_element: client_site_element
                        });
                    }
                });
            },

            suggest: function (event) {
                // prevent filter from submitting
                event.preventDefault();

                var form = $('.client-site-filter');

                // check if the form is disabled - if so don't execute anything
                if (form.hasClass('disable')) {
                    return false;
                }

                // disable the form submission
                form.addClass('disable');

                // send request to search method
                var data = {
                    searchValue: '',
                    'method:search': 'Search'
                };

                var list = $('.client-site-left .client-site-list');

                // loading icon
                list.addClass('loading');
                list.closest('.client-site-list-container').append('<span class="loading"></span>');

                PICS.ajax({
                    url: form.attr('action'),
                    data: data,
                    success: function (data, textStatus, XMLHttpRequest) {
                        list.siblings('span.loading').remove();

                        $('.client-site-left .client-site-list').replaceWith(data);

                        // re-enable form submission
                        form.removeClass('disable');
                    }
                });
            },

            trigger_client_help_removal: function () {
                var sites_selected_container = $('.client-site-right');
                var num_sites_selected = sites_selected_container.find('li:visible').length + 1;

                if (sites_selected_container.is(':hidden')) {
                    $('.client-site-get-started').fadeOut(250, function () {
                        sites_selected_container.fadeIn(250);
                    });
                }
                // remove suggest if there are more than 3 client sites added
                if (num_sites_selected > 3) {
                    $('.client-site-help').fadeOut(250);
                }
            }
        }
    });
})(jQuery);