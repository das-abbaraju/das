module.exports = function(grunt) {

  var vendor_js_files = [
    'vendor/angular/angular-loader.js',
    'vendor/angular/angular.js',
    'vendor/angular/angular-route.js',
  ];

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    jshint: {
      all: [
        'Gruntfile.js',
        'src/**/*.js'
      ]
    },

    karma: {
      unit: {
        options: {
          files: vendor_js_files.concat([
            'vendor/angular/jasmine/*.js',
            'src/**/*.js'
          ]),
          plugins:[
            'karma-junit-reporter',
            "karma-jasmine",
            "karma-phantomjs-launcher"
          ],
          junitReporter: {
            outputFile: 'unit.xml',
            suite: 'unit'
          },
          frameworks:[
            "jasmine"
          ],
          browsers:[
            "PhantomJS"
          ],
          singleRun: true
        }
      }
    },

    concat: {
      options: {
        separator: ';',
      },
      dist: {
        src: vendor_js_files.concat([
            'src/**/*.js',
            '!src/**/*.spec.js'
        ]),
        dest: 'build/script.js',
      }
    },

    ngmin: {
      controllers: {
        src: 'build/script.js',
        dest: 'build/script.js'
      }
    },

    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
      },
      build: {
        src: 'build/script.js',
        dest: 'build/script.js'
      }
    },

    compass: {
      dist: {
        options: {
          sassDir: 'src',
          cssDir: 'build'
        }
      }
    },

    watch: {
      scripts: {
        files: vendor_js_files.concat([
            'src/**/*.js',
        ]),
        tasks: [
          'jshint',
          'karma:unit',
          'concat',
          'ngmin'
        ],
        options: {
          interrupt: true,
        },
      },

      styles: {
        files: [
          'src/common/**/*.scss',
          'src/app/**/*.scss'
        ], tasks: [
          'compass'
        ],
        options: {
          interrupt: true,
        },
      }
    }
  });

  require('load-grunt-tasks')(grunt);

  grunt.registerTask('dev', [
    'watch'
  ]);

  grunt.registerTask('prod', [
    'jshint',
    'karma:unit',
    'concat',
    'ngmin',
    'uglify',
    'compass'
  ]);
};