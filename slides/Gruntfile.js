'use strict';

module.exports = function (grunt) {

    //var proxyRequests = require('grunt-connect-proxy/lib/utils').proxyRequest;
    //var liveReload = require('connect-livereload')({port: 9988});
    grunt.loadNpmTasks('grunt-contrib-watch');
    //grunt.loadNpmTasks("grunt-reload");
    grunt.loadNpmTasks("grunt-contrib-connect");

    grunt.initConfig({
      connect: {
        all: {
          options: {
            port: 9000,
            hostname: "0.0.0.0",
            //keepalive: true,
            middleware: function (connect, options) {

              return [

                // Load the middleware provided by the livereload plugin
                // that will take care of inserting the snippet
                require('connect-livereload')({port: 9988}),

                // Serve the project folder
                connect.static(options.base[0])
              ];
            }
          }
        }
      },
      watch: {
        client: {
          // '**' is used to include all subdirectories
          // and subdirectories of subdirectories, and so on, recursively.
          files: ['index.html'],
          // In our case, we don't configure any additional tasks,
          // since livereload is built into the watch task,
          // and since the browser refresh is handled by the snippet.
          // Any other tasks to run (e.g. compile CoffeeScript) go here:
          tasks: [],
          options: {
            livereload: 9988
          }
        }
      }

        //watch: {
        //    livereload: {
        //        options: {
        //            livereload: {
        //                port: 35729
        //            }
        //        },
        //        files: [
        //            'index.html'
        //        ]
        //    }
        //},
        //reload: {
        //    port: 35729,
        //    liveReload: {},
        //    proxy: {
        //        host: "localhost",
        //        port: 8080
        //    }
        //}

        //connect: {
        //    //proxies: [{context: '/rest/', host: 'localhost', port: 8080}],
        //    options: {
        //        port: 9090,
        //        hostname: '0.0.0.0'
        //    },
        //
        //    livereload: {
        //        options: {
        //            open: true,
        //            middleware: function (connect) {
        //                return [
        //                    proxyRequests,
        //                    liveReload,
        //                    connect.static('tmp'),
        //                    connect().use('/bower_files', connect.static('./bower_files')),
        //                    connect.static('app')
        //                ];
        //            }
        //        }
        //    }
    });

    grunt.registerTask('serve', ['connect', 'watch']);

    //require('matchdep').filterDev('grunt-*').forEach(function (dep) {
    //    grunt.loadNpmTasks(dep);
    //});
    //
    //grunt.registerTask('server', function(target) {
    //    if(target === 'dist') {
    //        return grunt.task.run(['build', 'configureProxies', 'connect:dist']);
    //    }
    //
    //    grunt.task.run([
    //      'clean:tmp',
    //      'bowerInstall',
    //      'html2js',
    //      'configureProxies',
    //      'connect:livereload',
    //      'watch'
    //    ]);
    //});
    //
    //grunt.registerTask('build', [
    //  'clean:dist',
    //  'bowerInstall',
    //  'test:teamcity',
    //  'html2js',
    //  'copy:assets',
    //  'copy:index',
    //  'useminPrepare',
    //  'concat',
    //  'usemin'
    //]);

};