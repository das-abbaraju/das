angular.module('PICSApp', [
    'PICS.filters',
    'PICS.employeeguard',
    'PICS.directives',
    'PICS.charts',
    'ui.select2',
    'PICS.translations'
])

.run(function (translationsService) {
    translationsService.setDevelopmentMode('off');
});
