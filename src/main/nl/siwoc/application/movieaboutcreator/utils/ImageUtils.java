/*******************************************************************************
 * Copyright (c) 2019 Niek Knijnenburg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package nl.siwoc.application.movieaboutcreator.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

	// for TheMovieDB image conversion
	private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
	private static final ColorModel RGB_OPAQUE =
	    new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

	public static void convertToJpg(File input, File output) throws Exception {
		Image img = Toolkit.getDefaultToolkit().createImage(input.getAbsolutePath());
		writeImageToJpgFile(img, output);
	}
	
	public static void writeImageFromURLToJpgFile(String url, File output) throws Exception {
		byte[] imagedata = getImageFromURL(url);
		Image img = Toolkit.getDefaultToolkit().createImage(imagedata);
		writeImageToJpgFile(img, output);
	}

	public static void writeByteArrayToJpgFile(byte[] imagedata, File output) throws Exception {
		Image img = Toolkit.getDefaultToolkit().createImage(imagedata);
		writeImageToJpgFile(img, output);
	}

	public static void writeImageToJpgFile(Image img, File output) throws Exception {

		PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
		pg.grabPixels();
		int width = pg.getWidth(), height = pg.getHeight();

		DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
		WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
		BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);

		ImageIO.write(bi, "jpg", output);
	}

	public static void writeImageToJpgFileWithQuality(Image img, File output) throws Exception {
		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(0.9f);
		
		PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
		pg.grabPixels();
		int width = pg.getWidth(), height = pg.getHeight();

		DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
		WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
		BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);
	
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(new FileImageOutputStream(output));
		writer.write(null, new IIOImage(bi, null, null), jpegParams);
	}
	
	public static void main(String[] args) throws Exception {
		String url = "https://image.tmdb.org/t/p/w342/e4L9XUCmNhiwbLICMiISh73PySF.jpg";
		//String url = "http://www.moviemeter.nl/images/cover/77000/77133.300.jpg";

		String to = "O:/downloads/123Test/about9.jpg";
		writeImageFromURLToJpgFile(url, new File(to));
		convertToJpg(new File("generated/about.png"), new File(to));
	}

	public static byte[] getImageFromURL(String imageUrl) throws Exception {
		byte[] image = null;
		if (imageUrl != null) {
			HttpURLConnection conn = null;

			// call image api
			try {
				URL url = new URL(imageUrl);
				LOG.trace("HTTP imageUrl call: " + url);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				InputStream is = conn.getInputStream();
				image = IOUtils.toByteArray(is);

			} catch (IOException e) {
				throw new Exception(e);
			} finally {
				conn.disconnect();
			}
		}
		return image;

	}
}
