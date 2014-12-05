/*
  Configuration for Karma test engine.
 */
var serverConfig = require('./server-config');

module.exports = function(config) {
  config.set({
    basePath: '.',

    files: [
      "bower_components/angular/angular.min.js",
      "bower_components/angular-mocks/angular-mocks.js",
      "bower_components/angular-route/angular-route.js",
      "bower_components/angular-cookies/angular-cookies.js",
      "bower_components/ngQuickDate/dist/ng-quick-date.min.js",
      "bower_components/jquery/dist/jquery.min.js",

      'public/' + serverConfig.version + '/javascripts/*.js',

      'test/unit/' + serverConfig.version + '/*.js'
    ],

    exclude: [
    ],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: [
      'karma-junit-reporter',
      'karma-jasmine',
      'karma-phantomjs-launcher'
    ],

    junitReporter: {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    },

    port: 3000

  })
}
