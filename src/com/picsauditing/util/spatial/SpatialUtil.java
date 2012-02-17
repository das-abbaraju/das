package com.picsauditing.util.spatial;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class SpatialUtil {
	
	public static Polygon parseJsonToPolygon(JSONArray json) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		
		for (Object pointObject : json) {
			coordinates.add(parseJsonToCoordinate((JSONObject) pointObject));
		}
		
		PrecisionModel pModel = new PrecisionModel();
//		LinearRing ring = new LinearRing(coordinates.toArray(Coordinate), pModel, 1);
//		Polygon polygon = new Polygon(ring, holes, factory);
		
		return null;
	}
	
	public static Coordinate parseJsonToCoordinate(JSONObject json) {
		double latitude = Double.parseDouble(json.get("lat").toString());
		double longitude = Double.parseDouble(json.get("lng").toString());
		return new Coordinate(longitude, latitude);
	}
 }
