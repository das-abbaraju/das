/*! Picsorganizer - v0.1.0 - 2012-11-15
* http://www.picsorganizer.com/
* Copyright (c) 2012 Joel Brownell;
*
 * What does this do
 *
 * Overrides the base Ext.application function allowing for the ExtJs "appFolder" to be set by an alternate configuration option.
 * This allows for sencha create to not choke if the "appFolder" is not set to the default "app"
 *
 * Usage:
 * In the application configuration, add APPFOLDER with file path into the "constants" configuration object
 *
 * Example:
 *
 * Ext.application({
 * name: 'PICS',
 *
 * constants: {
 *    APPFOLDER: '/v7/js/extjs/pics/app'
 * }
 *
*/
Ext.application = function(config) {
    Ext.require('Ext.app.Application');

    Ext.onReady(function() {
        if (config.constants.APPFOLDER) {
            config.appFolder = config.constants.APPFOLDER;
        }
        new Ext.app.Application(config);
    });
};