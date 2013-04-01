/*global module:false*/
module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    meta: {
        version: '0.1.0',
        banner: '/*! Picsorganizer - v<%= meta.version %> - ' +
            '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '* http://www.picsorganizer.com/\n' +
            '* Copyright (c) <%= grunt.template.today("yyyy") %> ' +
            'Carey Hinoki; Licensed MIT */'
    },
    
    lint: {
        files: [
            'js/vendor/jquery-1.9.1.min.js',
            'js/vendor/bootstrap.js',
            'js/vendor/prettify.js',
            'js/plugins.js',
            'js/pics/**/*.js',
            'js/main.js'
        ]
    },
    
    qunit: {

    },
    
    concat: {
        prod: {
            src: [
                '<banner:meta.banner>',
                '<config:lint.files>'
            ],
            dest: 'js/script.js'
        }
    },
    
    min: {
        prod: {
            src: [
                '<banner:meta.banner>',
                '<config:lint.files>'
            ],
            dest: 'js/script.js'
        }
    },

    compass: {
        debug: {
            src: 'sass',
            dest: 'css',
            outputstyle: 'expanded',
            linecomments: true,
            forcecompile: true
        },
        // unused - recess requires uncompiled files
        prod: {
            src: 'sass',
            dest: 'css',
            outputstyle: 'compressed',
            linecomments: false,
            forcecompile: true
        }
    },
    
    recess: {
        debug: {
            src: [
                'css/vendor/bootstrap.css',
                'css/vendor/bootstrap-responsive.css',
                'css/vendor/font-awesome.css',
                'css/vendor/prettify.css',
                'css/pics.css'
            ],
            dest: 'css/style.css',
            options: {
                compile: true
            }
        },
        prod: {
            src: '<config:recess.debug.src>',
            dest: 'css/style.css',
            options: {
                compress: true
            }
        }
    },

    watch: {
        all: {
            files: [
                '<config:lint.files>',
                '<config:watch.compass.files>'
            ],
            tasks: 'concat compass:debug recess:debug'
        },

        js: {
            files: [
                '<config:lint.files>'
            ],
            tasks: 'concat'
        },

        compass: {
            files: [
                'sass/**/*.scss'
            ],
            tasks: 'compass:debug recess:debug'
        }
    },

    jshint: {
        options: {
            curly: true,
            eqeqeq: true,
            immed: true,
            latedef: true,
            newcap: true,
            noarg: true,
            sub: true,
            undef: true,
            boss: true,
            eqnull: true,
            browser: true
        },
        globals: {
            jQuery: true
        }
    },

    uglify: {}
  });

  grunt.loadNpmTasks('grunt-compass');
  grunt.loadNpmTasks('grunt-recess');

  // grunt
  grunt.registerTask('default', 'Description...', function () {
      grunt.task.run('build-all');
  });
  
  // grunt build-all:prod
  // grunt build-all:debug
  // grunt build-all
  grunt.registerTask('build-all', 'Description...', function (environment) {
      if (environment == 'prod') {
          grunt.task.run('min compass:debug recess:prod');
      } else {
          grunt.task.run('concat compass:debug recess:debug');
      }
  });
  
  // grunt build-js:prod
  // grunt build-js:debug
  // grunt build-js
  grunt.registerTask('build-js', 'Description...', function (environment) {
      if (environment == 'prod') {
          grunt.task.run('min');
      } else {
          grunt.task.run('concat');
      }
  });
  
  // grunt build-compass:prod
  // grunt build-compass:debug
  // grunt build-compass
  grunt.registerTask('build-compass', 'Description...', function (environment) {
      if (environment == 'prod') {
          grunt.task.run('compass:debug recess:prod');
      } else {
          grunt.task.run('compass:debug recess:debug');
      }
  });
  
  // grunt watch-all
  // grunt watch-js
  // grunt watch-compass
  grunt.registerTask('watch-all', 'simple-watch:all');

  grunt.registerTask('watch-js', 'simple-watch:js');

  grunt.registerTask('watch-compass', 'simple-watch:compass');
};
