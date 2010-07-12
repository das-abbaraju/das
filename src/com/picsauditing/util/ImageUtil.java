package com.picsauditing.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class ImageUtil {
	
	public final static String[] supportedExtensions = {"jpg", "jpeg", "gif", "png"};
	
	/**
	 * Resizes an image
	 * 
	 * @param image
	 * 			The image to resize
	 * @param width
	 * 			The width that we would like to resize the image to
	 * @param height
	 * 			The height that we would like to resize the image to
	 * @param maintainRatio
	 * 			If true the aspect ratio will be maintained during the resizing
	 * 			As a result the image will not be resized directly to the provided
	 * 			width/height, but will resize the biggest side to the matched size
	 * 			and resize the other side according to the ratio.
	 * @return
	 * 			Returns a BufferedImage that has been resized to the given specifications
	 */
	public static BufferedImage resize(BufferedImage image, int width,
			int height, boolean maintainRatio) {
		BufferedImage resizedImage = null;

		float imageWidth = image.getWidth();
		float imageHeight = image.getHeight();
		float bigSide;
		float ratio, diff;
		int newWidth = 0, newHeight = 0;
		if (maintainRatio) {
			if (imageWidth > imageHeight) {
				ratio = imageHeight / imageWidth;
				bigSide = imageWidth;
			} else {
				ratio = imageWidth / imageHeight;
				bigSide = imageHeight;
			}
			ratio = (float) ((int) (ratio * 100)) / 100;
			if (ratio == 1) {
				newWidth = width;
				newHeight = height;
			} else {
				diff = bigSide - Math.max(width, height);
				diff = diff * ratio;
				if (bigSide == imageWidth) {
					newWidth = width;
					newHeight = (int) (imageHeight - diff);
				} else {
					newHeight = height;
					newWidth = (int) (imageWidth - diff);
				}
			}
		} else {
			newWidth = width;
			newHeight = height;
		}
		resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g2 = resizedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, 0, 0, newWidth, newHeight, null);
		g2.dispose();

		return resizedImage;
	}
	
	/**
	 * Creates a BufferedImage from a file
	 * 
	 * @param file
	 * 			The file to read from
	 * @return
	 * 		A BufferedImage
	 */
	public static BufferedImage createBufferedImage(File file){
		BufferedImage image = null;
		try {
			//File imageFile = new File(ftpDir+partialPath+imageName+"."+extension);//FileUtils.picsUploadLogic(ftpDir, partialPath, imageName, extension, false);
			image = ImageIO.read(file);
			if(image==null)
				throw new Exception();
		} catch (Exception e) {
			// File found or other exception
			System.out.println("Could not create BufferedImage "+file.getName());
		}
		return image;		
	}
	
	/**
	 * Creates a BufferedImage from a given file path
	 * 
	 * @param ftpDir
	 * 			The path to the ftpDir
	 * @param partialPath
	 * 			The path from ftpDir to the file
	 * @param imageName
	 * 			The name of the file
	 * @param extension
	 * 			The extension of the file
	 * @return
	 * 			Returns a BufferedImage created from the file/path given
	 */
	public static BufferedImage createBufferedImage(String ftpDir, String partialPath, String imageName, String extension){
			File imageFile = new File(ftpDir+partialPath+imageName+"."+extension);
			return createBufferedImage(imageFile);
	}
	
	/**
	 * Writes an image to a file with the specified quality
	 * @param image
	 * 			The BufferedImage to read from
	 * @param type
	 * 			The file type you want to write the image to
	 * @param quality
	 * 			The quality, must be between 0 and 1
	 * 			1 is full quality and max file size
	 * @return
	 * 			Returns the file handle of the newly written image
	 */
	public static File writeImageWithQuality(BufferedImage image, String type, float quality){
		if(!FileUtils.checkFileExtension(type, supportedExtensions)){
			System.out.println("Not a supported file type");
			return null;
		}
		if(quality < 0 || quality > 1){
			System.out.println("Quality must be in the range (0,1)");
			return null;
		}
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(type);
		FileImageOutputStream outStream = null;
		File f = new File("tmp.tmp");
		ImageWriter writer = null;
		if (iter.hasNext()) {
			writer = iter.next();
		}		
		if(writer==null){
			System.out.println("Could not find valid image writer for type "+type);
			return null;
		}
		
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(quality);
		try {
			outStream = new FileImageOutputStream(f);
			writer.setOutput(outStream);

			IIOImage renderedImage = new IIOImage(image, null, null);
			writer.write(null, renderedImage, iwp);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			System.out.println("Could not write image");
		} finally{
			image.flush();
			writer.dispose();
		}		
		return f;		
	}
	
	/**
	 * Crops the image to the given size
	 * The x,y params are the start point and the extend out
	 * to width in the x direction and height in y direction
	 * 
	 * @param image
	 * 			The BufferedImage to crop
	 * @param x
	 * 			The x coordinate of the top left corner of the to be cropped image
	 * @param y
	 * 			The y coordinate of the top left corner of the to be cropped image
	 * @param width
	 * 			The width of the to be cropped image
	 * @param height
	 * 			The height of the to be cropped image
	 * @return
	 * 			The cropped image
	 */
	public static BufferedImage crop(BufferedImage image, int x, int y,
			int width, int height){
		return image.getSubimage(x, y, width, height);		
	}
	
	/**
	 * Crops an image and then resizes it as well
	 * 
	 * @param image
	 * 			The BufferedImage to crop
	 * @param x
	 * 			The x coordinate of the top left corner of the to be cropped image
	 * @param y
	 * 			The y coordinate of the top left corner of the to be cropped image
	 * @param width
	 * 			The width of the to be cropped image
	 * @param height
	 * 			The height of the to be cropped image
	 * @param croppedWidth
	 * 			The width to resize the cropped image to
	 * @param croppedHeight
	 * 			The height to resize the cropped image to
	 * @return
	 * 			The cropped and resized image
	 */
	public static BufferedImage cropResize(BufferedImage image, int x, int y,
			int width, int height, int croppedWidth, int croppedHeight){
		image = crop(image, x, y, width, height);
		return resize(image, croppedWidth, croppedHeight, true);
	}

}
