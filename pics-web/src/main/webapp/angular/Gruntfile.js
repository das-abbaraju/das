module.exports = function(grunt) {
    var vendor_js_files = [
        'vendor/jquery/jquery-1.9.1.js',
        'vendor/angular/angular.js',
        'vendor/angular/angular-route.js',
        'vendor/angular/angular-resource.js',
        'vendor/jquery/jquery-ui.js',
        'vendor/bootstrap/bootstrap.js',
        'vendor/d3/d3.js',
        'vendor/bootstrap/bootstrap.tooltip.js',
        'vendor/bootstrap/bootstrap-datepicker/bootstrap-datepicker.js',
        'vendor/hogan-2.0.0.js',
        'vendor/typeahead.js',
        'vendor/jquery.hammer.js',
        'vendor/mmenu/jquery.mmenu.js',
        'vendor/mmenu/jquery.mmenu.searchfield.js',
        'vendor/mmenu/jquery.mmenu.dragopen.js',
        'vendor/pics/core.js'
    ];

    var modules = [
        'src/common/directives/directives.js',
        'src/common/services/charts/charts.js',
        'src/app/employeeguard/employeeguard.js'
    ];

    var dependencies = vendor_js_files.concat(modules);

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
                    files: dependencies.concat([
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
                src: dependencies.concat([
                    'src/**/*.js',
                    '!src/**/*.spec.js'
                ]),
                dest: 'build/script.js'
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
                    cssDir: 'build',
                    noLineComments: false,
                    force: true
                }
            },
            prod: {
                options: {
                    sassDir: 'src',
                    cssDir: 'build',
                    outputStyle: 'compressed',
                    noLineComments: true,
                    force: true
                }
            }
        },

        watch: {
            scripts: {
                files: vendor_js_files.concat([
                    'src/**/*.js',
                    'src/common/*.js',
                ]),
                tasks: [
                    'jshint',
                    'karma:unit',
                    'concat'
                ],
                options: {
                    interrupt: true
                },
            },

            styles: {
                files: [
                    'src/**/*.scss',
                    'vendor/**/*.scss',
                ],
                tasks: [
                    'compass:dist'
                ],
                options: {
                    interrupt: true
                }
            }
        }
    });

    require('load-grunt-tasks')(grunt);

    grunt.registerTask('dev', [
        'jshint',
        'karma:unit',
        'concat',
        'compass:dist'
    ]);

    grunt.registerTask('prod', [
        'jshint',
        'karma:unit',
        'concat',
        'ngmin',
        'uglify',
        'compass:prod'
    ]);
};