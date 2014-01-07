package org.webframework.cache.memcached.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public final class ByteBufArrayInputStream extends InputStream
  implements LineInputStream
{
  private ByteBuffer[] bufs;
  private int currentBuf = 0;

  public ByteBufArrayInputStream(List bufs) throws Exception {
    this((ByteBuffer[])bufs.toArray(new ByteBuffer[0]));
  }

  public ByteBufArrayInputStream(ByteBuffer[] bufs) throws Exception {
    if ((bufs == null) || (bufs.length == 0)) {
      throw new Exception("buffer is empty");
    }
    this.bufs = bufs;
    int len = bufs.length;
    for (int i = 0; i < len; i++)
      bufs[i].flip();
  }

  public int read()
  {
    do {
      if (this.bufs[this.currentBuf].hasRemaining())
        return this.bufs[this.currentBuf].get();
      this.currentBuf += 1;
    }while (this.currentBuf < this.bufs.length);

    this.currentBuf -= 1;
    return -1;
  }

  public int read(byte[] buf) {
    int len = buf.length;
    int bufPos = 0;
    do {
      if (this.bufs[this.currentBuf].hasRemaining()) {
        int n = Math.min(this.bufs[this.currentBuf].remaining(), len - bufPos);
        this.bufs[this.currentBuf].get(buf, bufPos, n);
        bufPos += n;
      }
      this.currentBuf += 1;
    }while ((this.currentBuf < this.bufs.length) && (bufPos < len));

    this.currentBuf -= 1;

    if ((bufPos > 0) || ((bufPos == 0) && (len == 0))) {
      return bufPos;
    }
    return -1;
  }

  public String readLine() throws IOException {
    byte[] b = new byte[1];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    boolean eol = false;

    while (read(b, 0, 1) != -1) {
      if (b[0] == 13) {
        eol = true;
      }
      else if (eol) {
        if (b[0] == 10)
          break;
        eol = false;
      }

      bos.write(b, 0, 1);
    }

    if ((bos == null) || (bos.size() <= 0)) {
      throw new IOException(
        "++++ Stream appears to be dead, so closing it down");
    }

    return bos.toString().trim();
  }

  public void clearEOL() throws IOException {
    byte[] b = new byte[1];
    boolean eol = false;
    while (read(b, 0, 1) != -1)
    {
      if (b[0] == 13) {
        eol = true;
      }
      else if (eol) {
        if (b[0] == 10)
          break;
        eol = false;
      }
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("ByteBufArrayIS: ");
    sb.append(this.bufs.length).append(" bufs of sizes: \n");

    for (int i = 0; i < this.bufs.length; i++) {
      sb.append("                                        ").append(i)
        .append(":  ").append(this.bufs[i]).append("\n");
    }
    return sb.toString();
  }
}