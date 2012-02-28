/**
 * PICS Badge
 *
 * Remote server inclusion of PICS Badge
 * _pbq = {
 *     id: 3,
 *     name: 'Ancon Marine',
 *     container_id: 'pics_badge_container',
 *     size: 80, 100, 150
 * };
 * 
 * Author: Carey Hinoki
 * Date: 2-27-2012
 * Version: 1.1
 */

(function () {
    /**
     * Prototypal Inheritance
     * 
     * Helper method to create class instance
     * http://javascript.crockford.com/prototypal.html
     */
    if (typeof Object.create !== 'function') {
        Object.create = function (o) {
            function F() {}
            
            F.prototype = o;
            
            return new F();
        };
    }
    
    /**
     * Base64 encode / decode
     * http://www.webtoolkit.info/ 
     */
    var Base64 = {
            
        // private property
        _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
     
        // public method for encoding
        encode : function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;
     
            input = Base64._utf8_encode(input);
     
            while (i < input.length) {
     
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
     
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
     
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
     
                output = output +
                this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
     
            }
     
            return output;
        },
     
        // public method for decoding
        decode : function (input) {
            var output = "";
            var chr1, chr2, chr3;
            var enc1, enc2, enc3, enc4;
            var i = 0;
     
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
     
            while (i < input.length) {
     
                enc1 = this._keyStr.indexOf(input.charAt(i++));
                enc2 = this._keyStr.indexOf(input.charAt(i++));
                enc3 = this._keyStr.indexOf(input.charAt(i++));
                enc4 = this._keyStr.indexOf(input.charAt(i++));
     
                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;
     
                output = output + String.fromCharCode(chr1);
     
                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }
     
            }
     
            output = Base64._utf8_decode(output);
     
            return output;
     
        },
     
        // private method for UTF-8 encoding
        _utf8_encode : function (string) {
            string = string.replace(/\r\n/g,"\n");
            var utftext = "";
     
            for (var n = 0; n < string.length; n++) {
     
                var c = string.charCodeAt(n);
     
                if (c < 128) {
                    utftext += String.fromCharCode(c);
                }
                else if((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
                else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
     
            }
     
            return utftext;
        },
     
        // private method for UTF-8 decoding
        _utf8_decode : function (utftext) {
            var string = "";
            var i = 0;
            var c = c1 = c2 = 0;
     
            while ( i < utftext.length ) {
     
                c = utftext.charCodeAt(i);
     
                if (c < 128) {
                    string += String.fromCharCode(c);
                    i++;
                }
                else if((c > 191) && (c < 224)) {
                    c2 = utftext.charCodeAt(i+1);
                    string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                    i += 2;
                }
                else {
                    c2 = utftext.charCodeAt(i+1);
                    c3 = utftext.charCodeAt(i+2);
                    string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                    i += 3;
                }
     
            }
     
            return string;
        }
     
    };
    
    var badge = Object.create((function () {
        var config = {
            // badge
            badge_path: '//www.picsorganizer.com/badge/images/',
            badge_size: _pbq.size || 80,
            badge_url: '//www.picsauditing.com/',
            
            // general link
            path: '//www.picsauditing.com/contractor/',
            
            // html id
            container_id: _pbq.container_id || 'pics_badge_container'
        };
        
        // script tag 
        var scripts = document.getElementsByTagName('script');
        var script_element = scripts[scripts.length - 1];
        
        // element storage
        var style_element;
        var container_element;
        var badge_element;
        var badge_link_element;
        var link_element;
        
        return {
            init: function () {
                // parse contractor id + name from hash include
                this.parseContractorHash();
                
                // create badge + link parts
                this.initBadgeCSS();
                this.initBadgeContainer();
                this.initBadge();
                this.initLink();
                
                // render badge + link to screen
                this.render();
            },
            
            parseContractorHash: function () {
                var include = document.getElementById('pics_badge');
                var a = document.createElement('a');
                a.href = include.src;
                
                // obtain hash from js inclusion
                var decoded_hash = Base64.decode(a.hash.replace('#pb-id=', ''));
                var hash_index = decoded_hash.indexOf(':');
                
                // set contractor id and name
                config.id = decoded_hash.substring(0, hash_index);
                config.name = decoded_hash.substring(hash_index + 1);
            },
            
            initBadgeCSS: function () {
                style_element = document.createElement('style');
                style_element.type = 'text/css';
                
                var css = [
                    '#pics_badge_container { text-align: center; }',
                    '#pics_badge_container a, #pics_badge_container img { border: 0px; color: #A94C0F; font-size: 8pt; text-decoration: none; }',
                    '#pics_badge_container a:hover { text-decoration: underline; }'
                ];
                
                css = css.join('');
                
                if (navigator.appName == 'Microsoft Internet Explorer') {
                    style_element.styleSheet.cssText = css;
                } else {
                    style_element.appendChild(document.createTextNode(css));
                }
            },
            
            initBadgeContainer: function () {
                container_element = document.createElement('div');
                container_element.id = config.container_id;
                container_element.style.width = config.badge_size;
            },
            
            initBadge: function () {
                var logo;
                
                badge_element = document.createElement('img');
                badge_element.alt = 'Contractor Prequalification, Contractor Management, Supplier Relationship Management';
                badge_element.title = 'Contractor Prequalification, Contractor Management, Supplier Relationship Management';
                
                badge_link_element = document.createElement('a');
                badge_link_element.href = config.badge_url;
                badge_link_element.style.width = config.badge_size;
                badge_link_element.target = '_blank';
                
                switch (config.badge_size) {
                    case 150:
                        logo = 'PICS-Seal-150x150.png';
                        break;
                    case 100:
                        logo = 'PICS-Seal-100x100.png';
                        break;
                    default:
                        logo = 'PICS-Seal-80x80.png';
                        break;
                }
                
                badge_element.src = config.badge_path + logo;
            },
            
            initLink: function () {
                link_element = document.createElement('a');
                
                link_element.href = config.path + config.id;
                link_element.target = '_blank';
                link_element.title = config.name;
                
                link_element.appendChild(document.createTextNode(config.name));
            }, 
            
            render: function () {
                document.getElementsByTagName('head')[0].appendChild(style_element);
                
                badge_link_element.appendChild(badge_element);
                container_element.appendChild(badge_link_element);
                container_element.appendChild(link_element);
                
                script_element.parentNode.insertBefore(container_element, script_element.nextSibling);
            }
        }
    }()));
    
    badge.init();
})();