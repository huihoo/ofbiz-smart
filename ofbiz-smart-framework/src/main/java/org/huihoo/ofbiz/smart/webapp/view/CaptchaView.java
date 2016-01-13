package org.huihoo.ofbiz.smart.webapp.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.util.CommUtil;

public class CaptchaView implements View {
  private static final String ALPHNUM_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private static final String NUMBER_CHARS = "23456789";
  private static final String LETTER_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ";

  @Override
  public String getContentType() {
    return "image/jpeg";
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
      throws ViewException {
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    response.setContentType(getContentType());
    
    String captchaKey = request.getParameter("cKey");
    if (CommUtil.isEmpty(captchaKey)) {
      captchaKey = "captchaCode";
    }
    
    String captchaType = request.getParameter("cType");
    if (CommUtil.isEmpty(captchaType)) {
      captchaType = "alphanum";
    }
    
    String captchaLengthStr = request.getParameter("cLen");
    if (CommUtil.isEmpty(captchaLengthStr) || !CommUtil.isNumber(captchaLengthStr)) {
      captchaLengthStr = "4";
    }
    
    String wStr = request.getParameter("w");
    if (CommUtil.isEmpty(wStr) || !CommUtil.isNumber(wStr)) {
      wStr = "135";
    }
    
    String hStr = request.getParameter("h");
    if (CommUtil.isEmpty(hStr) || !CommUtil.isNumber(hStr)) {
      hStr = "45";
    }
    
    String source = ALPHNUM_CHARS;
    if ("number".equals(captchaType)) {
      source = NUMBER_CHARS;
    } else if ("letter".equals(captchaType)) {
      source = LETTER_CHARS;
    }
    
    int randomCharsLength = Integer.parseInt(captchaLengthStr);
    if (randomCharsLength <= 0 || randomCharsLength > 10) {
      randomCharsLength = 4;
    }
    
    String captchaCode = generateRandomChars(randomCharsLength, source);
    try {
      renderImage(response.getOutputStream(), Integer.parseInt(wStr), Integer.parseInt(hStr), randomCharsLength, captchaCode);
      request.getSession().setAttribute(captchaKey, captchaCode);
    } catch (IOException e) {
      throw new ViewException(e);
    }
  }


  public String generateRandomChars(int randomCharsLength, String source) {
    if (CommUtil.isEmpty(source)) {
      source = ALPHNUM_CHARS;
    }
    if (randomCharsLength <= 0) {
      randomCharsLength = 4;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < randomCharsLength; i++) {
      sb.append(source.charAt(new Random().nextInt(source.length() - 1)));
    }
    return sb.toString();
  }


  public void renderImage(OutputStream os, int w, int h, int randomCharsLength, String source) throws IOException {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = (Graphics2D) img.getGraphics();
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  
      g2.setColor(Color.GRAY);
      g2.fillRect(0, 0, w, h);
      
      Color c = getRandColor(200, 250);  
      g2.setColor(c); 
      g2.fillRect(0, 2, w, h-4); 
      
      Random random = new Random();
      g2.setColor(getRandColor(160, 200));
      for (int i = 0; i < 60; i++) {
        int x = 0;
        int y = random.nextInt(h - 1);
        int xl = w;
        int yl = random.nextInt(h - 1);
        g2.setColor(getRandColor(150, 160));
        g2.drawLine(x, y, xl,yl);
      }
            
      int fontSize = h - 5;
      
      Font font = new Font("Times", Font.BOLD, fontSize);
      g2.setFont(font);
     
      Random rand = new Random();
      for (int i = 0; i < randomCharsLength; i++) {
        //随机旋转
        AffineTransform affine = new AffineTransform();  
        affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), 
                                                      (w / randomCharsLength) * i + fontSize / 2d, h / 2d);  
        g2.setColor(getRandColor(1, 150));
        g2.setTransform(affine); 
        
        g2.drawChars(source.toCharArray(), i, 1, ((w-10) / randomCharsLength) * i + 5, h / 2 + fontSize / 2 - 10);
      }
      
      ImageIO.write(img, "jpg", os);
    
    } finally {
      if (g2 != null) {
        g2.dispose();
      }
    }
  }


  private static Color getRandColor(int fc, int bc) {
    Random random = new Random();
    if (fc > 255) fc = 255;
    if (bc > 255) bc = 255;
    int r = fc + random.nextInt(bc - fc);
    int g = fc + random.nextInt(bc - fc);
    int b = fc + random.nextInt(bc - fc);
    return new Color(r, g, b);
  }
}
