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
                'css/font-awesome-ie7.min.css',
                'css/font-awesome.min.css'
            ],
            dest: 'css/bootstrap-menu.css',
            options: {
                compile: true
            }
        },
        prod: {
            src: '<%= recess.debug.src %>',
            dest: 'css/bootstrap-menu.css',
            options: {
                compress: true
            }
        }
    },

    watch: {
        all: {
            files: [
                '<%= lint.files %>',
                '<%= watch.compass.files %>'
            ],
            tasks: 'concat compass:debug recess:debug'
        },

        js: {
            files: [
                '<%= lint.files %>'
            ],
            tasks: 'concat'
        },

        compass: {
            files: [
                'sass/**/*.scss'
            ],
            tasks: 'compass:debug recess:debug'
        }
    }
  });

  grunt.loadNpmTasks('../../../node_modules/grunt-contrib-jshint');
  grunt.loadNpmTasks('../../../node_modules/grunt-contrib-watch');
  grunt.loadNpmTasks('../../../node_modules/grunt-contrib-concat');
  grunt.loadNpmTasks('../../../node_modules/grunt-contrib-uglify');
  grunt.loadNpmTasks('../../../node_modules/grunt-compass');
  grunt.loadNpmTasks('../../../node_modules/grunt-recess');

  // grunt
  grunt.registerTask('default', 'Description...', function () {
      grunt.task.run('build-all');
  });

  // grunt build-all:prod
  // grunt build-all:debug
  // grunt build-all
  grunt.registerTask('build-all', 'Description...', function (environment) {
      if (environment == 'prod') {
          grunt.task.run('compass:debug', 'recess:prod');
      } else {
          // recess:debug isn't working
//        grunt.task.run('compass:debug', 'recess:debug');
          grunt.task.run('compass:debug');
      }
  });


  // grunt build-compass:prod
  // grunt build-compass:debug
  // grunt build-compass
  grunt.registerTask('build-compass', 'Description...', function (environment) {
      if (environment == 'prod') {
          grunt.task.run('compass:debug', 'recess:prod');
      } else {
          // recess:debug isn't working
          // grunt.task.run('compass:debug', 'recess:debug');
          grunt.task.run('compass:debug');
      }
  });

  // grunt watch-all
  // grunt watch-js
  // grunt watch-compass
  grunt.registerTask('watch-all', 'simple-watch:all');

  grunt.registerTask('watch-js', 'simple-watch:js');

  grunt.registerTask('watch-compass', 'simple-watch:compass');
};
