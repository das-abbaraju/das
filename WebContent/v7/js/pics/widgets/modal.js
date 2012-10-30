/**
 * Modal
 *
 * Implements http://twitter.github.com/bootstrap/javascript.html#modal
 *
 * bind()
 * create()
 * destroy()
 * getConfig()
 * getDefaults()
 * getElement()
 * hide()
 * show()
 * toggle()
 * update()
 *
 * @author: Carey Hinoki
 * @date: 1-18-2012
 * @version: 2
 * @updated 10-30-2012 Jbrownell
 */
(function ($) {
    PICS.define('modal.Modal', {
        methods: (function () {
            // default configuration
            var defaults = {
                modal_id: 'bootstrap_modal',
                modal_class: 'modal',
                modal_link_class: 'modal-link',

                backdrop: true,
                height: 'auto', // height of content area
                keyboard: true,
                show: false,
                width: 560, // width of modal

                title: 'TITLE',
                content: 'CONTENT',

                // button cofiguration: parameters include label, html, callback
                buttons: []
            };

            var modal;

            function getModal() {
                if (!modal) {
                    throw 'modal is undefined - initialize modal by using create()';
                }

                return modal;
            }

            return {
                bind: function (event, callback) {
                    if (typeof callback == 'function') {
                        var modal = getModal();

                        modal.bind(event, callback);
                    }
                },

                create: function (options) {
                    var config = {};

                    // generate config
                    $.extend(config, defaults, options);

                    function removeModal() {
                        try {
                            var modal = getModal();

                            if (modal) {
                                modal.remove();
                                $('.modal-backdrop').remove();
                            }
                        } catch (e) {}
                    }

                    function createModal() {
                        // update modal content
                        function updateModal(modal_header, modal_body) {
                            modal_header.find('h3').html(config.title);
                            modal_body.html(config.content);
                        }

                        function updateModalButtons(modal_footer) {
                            for (var i in config.buttons) {
                                if (config.buttons.hasOwnProperty(i) && typeof config.buttons[i] == 'object') {
                                    var button = config.buttons[i];

                                    if (button.label) {
                                        var label = button.label;
                                    } else {
                                        var label = 'Button';
                                    }

                                    if (button.html) {
                                        var btn = $(button.html)
                                    } else {
                                        var btn = $('<a href="javascript:;" class="btn">' + label + '</a>');
                                    }

                                    if (button.callback && typeof button.callback == 'function') {
                                        btn.bind('click', button.callback);
                                    }

                                    modal_footer.append(btn);
                                }
                            }
                        }

                        function updateModalPosition(modal, modal_body) {
                            // manually position modal
                            modal.css({
                                marginLeft: '-' + (config.width / 2) + 'px',
                                marginTop: '-' + ((config.height / 2) + 50) + 'px',
                                width: config.width
                            });

                            // configure height of modal content
                            modal_body.css({
                                height: config.height,
                                overflowY: 'auto'
                            });
                        }

                        // define modal element
                        var modal = $('<div id="' + config.modal_id + '" class="' + config.modal_class + '" style="display: none;">');

                        var modal_header = $('<div class="modal-header"><a href="#" class="close">&#215;</a><h3></h3></div>');
                        var modal_body = $('<div class="modal-body">');
                        var modal_footer = $('<div class="modal-footer">');

                        var html = modal.append(modal_header, modal_body, modal_footer);

                        $('body').append(html);

                        updateModal(modal_header, modal_body);

                        updateModalButtons(modal_footer);

                        updateModalPosition(modal, modal_body);


                        return modal;
                    }

                    removeModal();

                    modal = createModal();

                    modal.modal(config);
                },

                destroy: function () {
                    var modal = getModal();

                    modal.remove();
                    $('.modal-backdrop').remove();
                },

                getConfig: function () {
                    return config;
                },

                getDefaults: function () {
                    return defaults;
                },

                getElement: function () {
                    var modal = getModal();

                    return modal;
                },

                hide: function () {
                    var modal = getModal();

                    if (modal.is(':visible')) {
                        modal.modal('hide');
                    }
                },

                show: function () {
                    var modal = getModal();

                    if (!modal.is(':visible')) {
                        modal.modal('show');

                        // ie specific js to shim select menus
                        if ($.browser.msie && $.browser.version == 6) {
                            var offset = modal.offset();
                            var shim = $('<iframe class="shim" frameborder="0" scrolling="no"></iframe>');

                            // paste shim
                            shim.css({
                                'height': modal.height(),
                                'left': offset.left,
                                'position': 'absolute',
                                'top': offset.top,
                                'width': modal.width()
                            }).prependTo('body');

                            // add event to destroy shim after modal closes
                            modal.bind('hide', function () {
                                shim.remove();
                            });
                        }
                    }
                },

                toggle: function () {
                    var modal = getModal();

                    modal.modal('toggle');
                },

                update: function (config) {
                    var modal_element = getModal();

                    for (var prop in config) {
                        if (config.hasOwnProperty(prop)) {
                            if (prop === 'header') {
                                modal_element.find('.modal-header h3').html(config[prop]);
                            }
                            if (prop === 'body') {
                                modal_element.find('.modal-body').html(config[prop]);
                            }
                            if (prop === 'footer') {
                                modal_element.find('.modal-footer').html(config[prop]);
                            }
                        }
                    }
                }
            };
        }())
    });
}(jQuery));