import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.ImageIcon;
import com.sun.image.codec.jpeg.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class imageResizer extends HttpServlet
{
    private String imageDir = "";
	String path = "";

    public final void init( ServletConfig config ) throws ServletException
    {
        // No initialization necessary
          path = config.getServletContext().getRealPath("/") + "photo1.jpg";
    }

    public final void doGet( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        // No difference to us if it's a get or a post.
        this.doPost(req,res);
    }

    public final void doPost( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        try
        {
          int targetWidth=0;
          int targetHeight=0;

          // Get a path to the image to resize.
          // ImageIcon is a kluge to make sure the image is fully 
          // loaded before we proceed.
//          String path = config.getServletContext().getRealPath("/") + "photo1.jpg";
Toolkit.getDefaultToolkit().getImage(path);
//	new ImageIcon(Toolkit.getDefaultToolkit().getImage(path)).getImage();
	//	  Image sourceImage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(path)).getImage();
/*
          // Calculate the target width and height
          float scale = Float.parseFloat(req.getParameter("scale"))/100;
          targetWidth = (int)(sourceImage.getWidth(null)*scale);
          targetHeight = (int)(sourceImage.getHeight(null)*scale);

          BufferedImage resizedImage = this.scaleImage
          (sourceImage,targetWidth,targetHeight);

          // Output the finished image straight to the response as a JPEG!
          res.setContentType("image/jpeg");
          JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder
          (res.getOutputStream());
          encoder.encode(resizedImage);
*/        }
        catch(Exception e)
        {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private BufferedImage scaleImage(Image sourceImage, int width, int height)
    {
        ImageFilter filter = new ReplicateScaleFilter(width,height);
        ImageProducer producer = new FilteredImageSource
        (sourceImage.getSource(),filter);
        Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

        return this.toBufferedImage(resizedImage);
    }

    private BufferedImage toBufferedImage(Image image)
    {
        image = new ImageIcon(image).getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null)
        ,image.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0,image.getWidth(null),image.getHeight(null));
        g.drawImage(image,0,0,null);
        g.dispose();

        return bufferedImage;
    }
}