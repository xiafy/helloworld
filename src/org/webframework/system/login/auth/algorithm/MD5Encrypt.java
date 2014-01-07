package org.webframework.system.login.auth.algorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encrypt
  implements IEncrypt
{
  public String encode(String input)
    throws Exception
  {
    return desEncrypt(input);
  }

  public String decode(String input) throws Exception
  {
    return desDecrypt(input);
  }

  public static String desEncrypt(String input)
    throws Exception
  {
    StringBuffer result = new StringBuffer();
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.reset();
      messageDigest.update(input.getBytes("utf-8"));
      byte[] byteArray = messageDigest.digest();

      for (int i = 0; i < byteArray.length; i++)
        if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
          result.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
        else
          result.append(Integer.toHexString(0xFF & byteArray[i]));
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new Exception("MD5 NoSuchAlgorithmException caught!");
    }
    return result.toString();
  }

  public static String desDecrypt(String cipherText)
    throws Exception
  {
    throw new Exception("md5密码不可逆!");
  }
}