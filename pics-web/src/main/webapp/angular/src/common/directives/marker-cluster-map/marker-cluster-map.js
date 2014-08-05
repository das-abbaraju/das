angular.module('PICS.directives')

.directive('markerClusterMap', function (mapMarkerService) {
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
                infoWindow = new google.maps.InfoWindow({
                    maxWidth: 250,
                    disableAutoPan: true,
                    id: '',
                    visible: false
                }),
                selectedMarker = null,
                currentMarkerList = [],
                markerClusterer,
                loader = $(element.children()[1]),
                markerIconBase = '/angular/src/app/company-finder/img/';

            scope.$emit('loader-ready', loader, scope.markerClustererConfig);

            if (!map || isEmptyObject(map)) {
                map = scope.googleMapConfig.map = createGoogleMap(element, googleMapConfig);
            }

            scope.$watch('markerClustererConfig', function (newConfig) {
                if (!newConfig) return;

                selectedMarker = null;

                refreshMarkerClusterer(map, newConfig);
            }, true);

            google.maps.event.addListener(
                infoWindow,
                'closeclick',
                function () {
                    if (selectedMarker) {
                        useSmallMarker(currentMarkerList[selectedMarker.id]);

                        infoWindow.visible = false;
                    }
                }
            );

            google.maps.event.addListener(
                map,
                'click',
                function (event) {
                if (selectedMarker && infoWindow.visible) {
                    infoWindow.close();
                    useSmallMarker(currentMarkerList[selectedMarker.id]);

                    infoWindow.visible = false;
                }
            });

            function initMarkerClusterer(map, config) {
                markerClusterer = createMarkerClusterer(map, config);
            }

            function createMarkerClusterer(map, config) {
                var markerImage = new google.maps.MarkerImage(
                    config.markerImageUrl,
                    new google.maps.Size(24, 32)
                );

                currentMarkerList = createMarkers(map, config.locations, markerImage);

                mapMarkerService.setMarkers(currentMarkerList);

                return new MarkerClusterer(map, currentMarkerList, config);
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
                    marker = createMarker(map, location, markerImage, index);
                    markers.push(marker);
                });

                return markers;
            }

            function createMarker(map, location, markerImage, index) {
                var latLng = new google.maps.LatLng(
                    location.coordinates.latitude,
                    location.coordinates.longitude
                );

                var googleMarker = new MarkerWithLabel({
                    map: map,
                    position: latLng,
                    icon: markerImage,
                    labelContent: index + 1,
                    labelInBackground: false,
                    labelAnchor: new google.maps.Point(6, 27),
                    labelClass: 'marker-label',
                    id: index
                });

                google.maps.event.addListener(
                    googleMarker,
                    'click',
                    createMarkerClickHandler(map, googleMarker, location, index)
                );

                google.maps.event.addListener(
                    googleMarker,
                    'mouseover',
                    createMouseOverHandler(googleMarker)
                );

                var mouseoutListener = google.maps.event.addListener(
                    googleMarker,
                    'mouseout',
                    createMouseOutHandler(googleMarker)
                );

                return googleMarker;
            }

            // TODO: Make contentString a configuration option
            function getInfoWindowContent(location, id) {
                var otherTrades = getOtherTradesLabel(location),
                    address = location.formattedAddressBlock.replace('\n', '<br>');

                var contentString = [
                    '<p class="contractor-name">',
                        '<a href="' + location.link + '" target="_blank">' + location.name + '</a>',
                    '</p>',
                    '<p class="primary-trade">',
                        location.primaryTrade,
                    '</p>',
                    '<p class="other-trades">',
                        '<a href="' + location.link + '#trade_cloud" target="_blank">' + otherTrades + '</a>',
                    '</p>',
                    '<p class="address">' + address + '</p>'
                ].join('');

                return contentString;
            }

            function getOtherTradesLabel(location) {
                var totalTrades = location.trades.length,
                    nonPrimaryTrades = totalTrades - 1;

                if (totalTrades == 2) {
                    return nonPrimaryTrades + ' other trade&hellip;';
                } else if (totalTrades > 2) {
                    return nonPrimaryTrades + ' other trades&hellip;';
                } else {
                    return '';
                }
            }

            function createMarkerClickHandler(map, googleMarker, location, index) {
                return function () {
                    infoWindow.setContent(getInfoWindowContent(location, index));

                    infoWindow.id = index;

                    infoWindow.open(map, googleMarker);

                    infoWindow.visible = true;

                    if (selectedMarker) {
                        google.maps.event.trigger(currentMarkerList[selectedMarker.id], 'mouseout');
                    }

                    selectedMarker = googleMarker;
                };
            }

            function createMouseOverHandler(googleMarker) {
                return function () {
                    googleMarker.labelAnchor = new google.maps.Point(5, 37);
                    googleMarker.label.setAnchor();

                    googleMarker.labelClass = 'marker-label-large';
                    googleMarker.label.setStyles();

                    googleMarker.setIcon(markerIconBase + 'marker-danger-light-large.png');
                };
            }

            function createMouseOutHandler(googleMarker) {
                return function () {
                    if ((infoWindow.id != googleMarker.id) || (infoWindow.visible === false)) {
                        useSmallMarker(googleMarker);
                    }
                };
            }

            function useSmallMarker(googleMarker) {
                googleMarker.labelAnchor = new google.maps.Point(5, 28);
                googleMarker.label.setAnchor();

                googleMarker.labelClass = 'marker-label';
                googleMarker.label.setStyles();

                googleMarker.setIcon(markerIconBase + 'marker-danger-light.png');
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