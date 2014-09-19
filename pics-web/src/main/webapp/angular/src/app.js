angular.module('PICSApp', [
    'PICS.registration',
    'PICS.directives',
    'PICS.charts',
    'ui.select2',
    'PICS.services',
    'PICS.companyFinder',
    'PICS.translations',
    'ui.bootstrap'
])

.config(function (titleServiceProvider) {
    titleServiceProvider.setPrefix('PICS');
})

.run(function (translationsService, titleService) {
    translationsService.setDevelopmentMode('off');
    titleService.init();
});