angular.module('PICS.directives')

.directive('markerClusterMap', function () {
    return {
        restrict: 'E',
        scope: {
            googleMapConfig: '=',
            markerClustererConfig: '='
        },
        templateUrl: '/angular/src/common/directives/marker-cluster-map/marker-cluster-map.tpl.html',
        link: function (scope, element, attrs) {
            var googleMapConfig = scope.googleMapConfig,
                map = googleMapConfig.map,
                markerClusterer, currentInfoWindow,
                loader = $(element.children()[1]);

            scope.$emit('loader-ready', loader, scope.markerClustererConfig);

            if (!map || isEmptyObject(map)) {
                map = scope.googleMapConfig.map = createGoogleMap(element, googleMapConfig);
            }

            scope.$watch('markerClustererConfig', function (newConfig) {
                if (!newConfig) return;

                refreshMarkerClusterer(map, newConfig);
            }, true);

            function initMarkerClusterer(map, config) {
                markerClusterer = createMarkerClusterer(map, config);
            }

            function createMarkerClusterer(map, config) {
                var markerImage = new google.maps.MarkerImage(
                    config.markerImageUrl,
                    new google.maps.Size(24, 32)
                );

                var markers = createMarkers(map, config.locations, markerImage);

                return new MarkerClusterer(map, markers, config);
            }

            function createGoogleMap($mapOuterContainer, config) {
                var mapContainer = $mapOuterContainer.children()[0];

                $mapOuterContainer.addClass('map-container');

                scope.$emit('map-created', scope.googleMapConfig);

                return new google.maps.Map(mapContainer, config);
            }

            function createMarkers(map, locations, markerImage) {
                var markers = [],
                    location_index, marker;

                angular.forEach(locations, function (location, index) {
                    marker = createMarker(map, location, markerImage, index+1);
                    markers.push(marker);
                });

                return markers;
            }

            function createMarker(map, location, markerImage, labelContent) {
                var infoWindow = createInfoWindow(location);

                var latLng = new google.maps.LatLng(
                    location.coordinates.latitude,
                    location.coordinates.longitude
                );

                var googleMarker = new MarkerWithLabel({
                    map: map,
                    position: latLng,
                    icon: markerImage,
                    labelContent: labelContent,
                    labelInBackground: false,
                    labelAnchor: new google.maps.Point(6, 30),
                    labelClass: 'marker-label'
                });

                google.maps.event.addListener(
                    googleMarker,
                    'click',
                    createMarkerClickHandler(map, googleMarker, infoWindow)
                );

                return googleMarker;
            }

            // TODO: Make contentString a configuration option
            function createInfoWindow(location) {
                var contentString = [
                    '<p class="contractor-name">',
                        '<a href="' + location.link + '" target="_blank">' + location.name + '</a>',
                    '</p>',
                    '<p class="address-and-trade">' + location.address + '</p>',
                    '<p class="address-and-trade">',
                      location.trade,
                    '</p>'
                ].join('');

                return new google.maps.InfoWindow({
                    content: contentString,
                    maxWidth: 250
                });
            }

            function createMarkerClickHandler(map, googleMarker, infoWindow) {
                return function () {
                    if (typeof currentInfoWindow != 'undefined') {
                        currentInfoWindow.close();
                    }

                    infoWindow.open(map, googleMarker);

                    currentInfoWindow = infoWindow;          
                };
            }

            function refreshMarkerClusterer(map, config) {
                if (markerClusterer) {
                    markerClusterer.clearMarkers();
                }

                initMarkerClusterer(map, config);
            }   

            function isEmptyObject(obj){
                for(var prop in obj){ return false;}
                return true;
            }
        }
    };
});