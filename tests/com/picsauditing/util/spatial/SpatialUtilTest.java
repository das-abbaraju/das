package com.picsauditing.util.spatial;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("unchecked")
public class SpatialUtilTest {

	@Test
	public void testJsonToPolygon() throws Exception {
		JSONArray json = new JSONArray();
		json.add(createPoint(-80.190262, 25.774252));
		json.add(createPoint(-66.118292, 18.466465));
		json.add(createPoint(-64.75737, 32.321384));

		Polygon polygon = SpatialUtil.parseJsonToPolygon(json);
		// assertEquals(3, polygon.getExteriorRing().getNumPoints());
	}
	
	private JSONObject createPoint(double x, double y) {
		JSONObject point = new JSONObject();
		point.put("lat", y);
		point.put("lng", x);
		return point;
	}
}
