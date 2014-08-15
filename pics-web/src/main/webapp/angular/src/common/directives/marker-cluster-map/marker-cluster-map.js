angular.module('PICS.directives')

.directive('markerClusterMap', function (mapMarkerService) {
    return {
        restrict: 'E',
        scope: {
            googleMapConfig: '=',
            markerClustererConfig: '='
        },
        replace: true,
        templateUrl: '/angular/src/common/directives/marker-cluster-map/marker-cluster-map.tpl.html',
        link: function (scope, element, attrs) {
            var googleMapConfig = scope.googleMapConfig,
                map = googleMapConfig.map,
                info_window = $('.info-window'),
                currentInfoWindow = null,
                infoWindowVisible = false,
                selectedMarker = null,
                currentMarkerList = [],
                markerClusterer,
                loader = $(element.children()[2]),
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
                map,
                'click',
                closeInfoWindow()
            );

            google.maps.event.addListener(
                map,
                'center_changed',
                closeInfoWindow()
            );

            google.maps.event.addListener(
                map,
                'zoom_changed',
                closeInfoWindow()
            );

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
                var mapContainer = $mapOuterContainer.children()[1];

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
            function getInfoWindowContent(location) {
                var otherTrades = getOtherTradesLabel(location),
                    address = location.formattedAddressBlock.replace('\n', '<br>');

                var contentString = [
                    '<div class="info-window-content">' +
                        '<p class="contractor-name">',
                            '<a href="' + location.link + '" target="_blank">' + location.name + '</a>',
                        '</p>',
                        '<p class="primary-trade">',
                            location.primaryTrade,
                        '</p>',
                        '<p class="other-trades">',
                            '<a href="' + location.link + '#trade_cloud" target="_blank">' + otherTrades + '</a>',
                        '</p>',
                        '<p class="address">' + address + '</p>' +
                    '</div>'
                ].join('');

                return contentString;
            }

            function getOtherTradesLabel(location) {
                var nonPrimaryTrades = location.trades.length - 1;

                if (nonPrimaryTrades == 1) {
                    return nonPrimaryTrades + ' other trade&hellip;';
                } else if (nonPrimaryTrades > 1) {
                    return nonPrimaryTrades + ' other trades&hellip;';
                } else {
                    return '';
                }
            }

            function createMarkerClickHandler(map, googleMarker, location, index) {
                return function () {
                    var MARKER_LARGE_HEIGHT = 80,
                        overlay = new google.maps.OverlayView();

                    overlay.draw = function() {};
                    overlay.setMap(map);

                    var proj = overlay.getProjection(),
                        pos = googleMarker.getPosition(),
                        p = proj.fromLatLngToContainerPixel(pos);

                    $('.info-window-content').remove();

                    info_window.append(getInfoWindowContent(location));
                    info_window.css('left', p.x - (info_window.width() / 2) - parseInt(info_window.css('padding-left')));
                    info_window.css('top', p.y - info_window.height() - MARKER_LARGE_HEIGHT);
                    info_window.css('display', 'block');

                    currentInfoWindow = index;

                    infoWindowVisible = true;

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
                    if ((currentInfoWindow != googleMarker.id) || (!infoWindowVisible)) {
                        useSmallMarker(googleMarker);
                    }
                };
            }

            function closeInfoWindow() {
                return function () {
                    if (selectedMarker && infoWindowVisible) {
                        info_window.css('display', 'none');
                        useSmallMarker(currentMarkerList[selectedMarker.id]);

                        infoWindowVisible = false;
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