package test.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class MockServletOutputStream extends ServletOutputStream {

  public ByteArrayOutputStream baos = new ByteArrayOutputStream();

  @Override
  public void write(int b) throws IOException {
    baos.write(b);
  }


  public String getContent() {
    return baos.toString();
  }


}
