angular.module('PICSApp', [
    'PICS.registration',
    'PICS.employeeguard',
    'PICS.directives',
    'PICS.charts',
    'ui.select2',
    'PICS.services',
    'PICS.companyFinder',
    'PICS.translations'
])

.config(function (titleServiceProvider) {
    titleServiceProvider.setPrefix('PICS');
})

.run(function (translationsService, titleService) {
    translationsService.setDevelopmentMode('on');
    titleService.init();
});