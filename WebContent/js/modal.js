/**
 * Modal
 * 
 * Implements http://twitter.github.com/bootstrap/javascript.html#modal
 * 
 * bind()
 * create()
 * getConfig()
 * getDefaults()
 * getElement()
 * hide()
 * show()
 * toggle()
 * 
 * @author: Carey Hinoki
 * @date: 1-18-2012
 * @version: 2
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
                content: 'CONTENT'
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
                        var modal_element = $('#' + config.modal_id);
                        
                        // remove existing modal
                        if (modal_element.length) {
                            modal_element.remove();
                        }
                    }
                    
                    function createModal() {
                        // define modal element
                        var modal = $('<div id="' + config.modal_id + '" class="' + config.modal_class + '" style="display: none;">');
                        
                        var modal_header = $('<div class="modal-header"><a href="#" class="close">&#215;</a><h3></h3></div>');
                        var modal_body = $('<div class="modal-body">');
                        var modal_footer = $('<div class="modal-footer">');
                        
                        var html = modal.append(modal_header, modal_body, modal_footer);
                        
                        $('body').append(html);
                        
                        // update modal content
                        modal_header.find('h3').html(config.title);
                        modal_body.html(config.content);
                        
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
                        
                        return modal;
                    }
                    
                    removeModal();
                    
                    modal = createModal();
                    
                    modal.modal(config);
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
                    }
                },
                
                toggle: function () {
                    var modal = getModal();
                    
                    modal.modal('toggle');
                }
            };
        }())
    });
})(jQuery);