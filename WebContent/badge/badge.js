/**
 * PICS Badge
 *
 * Remote server inclusion of PICS Badge
 * _pbq = {
 *     container_element_id: pics_badge_container,
 *     size: 80, 100, 150
 * };
 * 
 * Author: Carey Hinoki
 * Date: 1-4-2012
 * Version: 1.0
 */

// Example Code:

//<script id="pics_badge" type="text/javascript">
//    var _pbq = _pbq || {};
//    
//    (function () {
//        var pb = document.createElement('script');
//        pb.type = 'text/javascript';
//        pb.async = true;
//        pb.src = '//alpha.picsorganizer.com/badge/badge.js';
//        
//        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(pb);
//    })();
//</script>

(function () {
    // default configuration
    var badge_size = _pbq.size || 80;
    var badge_url = '//www.google.com';
    var container_element_id = _pbq.container_element_id || 'pics_badge_container';
    var host = '//alpha.picsorganizer.com';
    var logo_url;
    var website_text = 'The Contractor\'s Choice';
    var website_url = '//www.picsauditing.com';
    
    // script tag 
    var scripts = document.getElementsByTagName('script');
    var script_element = scripts[scripts.length - 1];
    
    console.log(script_element);
    
    // badge style
    var style_element = document.createElement('style');
    style_element.type = 'text/css';
    
    var css = 
    '#pics_badge_container { text-align: center; }' +
    '#pics_badge_container a, #pics_badge_container img { border: 0px; color: #A94C0F; font-size: 8pt; text-decoration: none; }' +
    '#pics_badge_container a:hover { text-decoration: underline; }';
    
    if (navigator.appName == 'Microsoft Internet Explorer') {
        style_element.styleSheet.cssText = css;
    } else {
        style_element.appendChild(document.createTextNode(css));
    }
    
    // container element
    var container_element = document.createElement('div');
    container_element.id = container_element_id;
    
    // badge link
    var a_badge_element = document.createElement('a');
    a_badge_element.href = badge_url;
    
    // badge
    var badge_element = document.createElement('img');
    
    switch (badge_size) {
        case 150:
            logo_url = 'PICS-Seal-150x150.png';
            container_element.style.width = badge_size;
            a_badge_element.style.width = badge_size;
            break;
        case 100:
            logo_url = 'PICS-Seal-100x100.png';
            container_element.style.width = badge_size;
            a_badge_element.style.width = badge_size;
            break;
        default:
            logo_url = 'PICS-Seal-80x80.png';
            container_element.style.width = 80;
            a_badge_element.style.width = 80;
            break;
    }
    
    badge_element.src = host + 'badge/images/' + logo_url;
    
    // website link
    var a_pics_element = document.createElement('a');
    a_pics_element.href = website_url;
    a_pics_element.appendChild(document.createTextNode(website_text));
    
    // render html
    document.getElementsByTagName('head')[0].appendChild(style_element);
    a_badge_element.appendChild(badge_element);
    container_element.appendChild(a_badge_element);
    container_element.appendChild(a_pics_element);
    script_element.parentNode.insertBefore(container_element, script_element.nextSibling);
})();