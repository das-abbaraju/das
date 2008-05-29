package com.picsauditing.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {

	static public int getWidth(String filename) throws IOException {
		File file = new File(filename);
		Image image = ImageIO.read(file);
		return image.getWidth(null);
	}
}
