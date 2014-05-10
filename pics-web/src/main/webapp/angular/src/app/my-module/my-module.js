// Add PICS.myModule to app.js
// Also add to gruntfile
angular.module('PICS.myModule', [
    'ngRoute',
    'ngResource'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    // Add this url to decorators.xml under index.jsp

    // Add this line to struts.xml:
    // <action name="my-url" class="com.picsauditing.struts.controller.AngularLoaderAction" method="load"></action>

    $routeProvider
        .when('/my-url.action', {
            templateUrl: '/angular/src/app/my-module/my-view/my-view.tpl.html'
        });
});