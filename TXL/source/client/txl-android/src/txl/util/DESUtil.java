package txl.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.util.Log;

public class DESUtil
{
    
    //static private byte[] rawKeyData = "hundsun_mwa".getBytes();
    private final static String TAG = DESUtil.class.getSimpleName();
    public void createKey(String keyFileName)
    {
         
        SecureRandom sr = new SecureRandom();
         
        KeyGenerator kg;
        try
        {
            kg = KeyGenerator.getInstance("DES");
            kg.init(sr);
            SecretKey key = kg.generateKey();
            byte rawKeyData[] = key.getEncoded();
            FileOutputStream fo = new FileOutputStream(new File(keyFileName));
            fo.write(rawKeyData);
            
        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }
    
    public static void encryString(byte[] rawKeyData, String targetFile,String outputFile)
    {
        SecureRandom sr = new SecureRandom();
         
        try
        {
              
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            FileInputStream fi2 = new FileInputStream(new File(targetFile));
            byte data[] = new byte[fi2.available()];
            fi2.read(data);
            fi2.close();
            byte encryptedData[] = cipher.doFinal(data);
            FileOutputStream fo = new FileOutputStream(new File(outputFile));
            fo.write(encryptedData);
            fo.close();
            
            Log.d(TAG, "DESUtil encryString : "+ new String(data,"UTF-8"));
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static String encryString(byte[] rawKeyData, String data)
    {
        SecureRandom sr = new SecureRandom();         
        String result = "";
        try
        {
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            //byte[] datas = data.getBytes("UTF-8");
            byte[] datas = data.getBytes(); 
            byte[] encryptedData = cipher.doFinal(datas);
            result = new String(encryptedData);
            
            Log.d(TAG, "DESUtil encryString ... : "+ result);
            decryption(rawKeyData,result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public static String decryption(byte[] rawKeyData,String data){
        SecureRandom sr = new SecureRandom();
        
        String result ="";
        try
        {
            
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            
            byte[] encryptedData = data.getBytes();
             
            byte decryptedData[] = cipher.doFinal(encryptedData);
            
            result = new String(decryptedData);
            Log.d(TAG, "DESUtil decryption ... : "+ result);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String decryString(byte[] rawKeyData,String targetFile)
    {
        SecureRandom sr = new SecureRandom();
        FileInputStream fi =null;
        try
        {
            
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            FileInputStream fi2 = new FileInputStream(targetFile);
            byte encryptedData[] = new byte[fi2.available()];
            fi2.read(encryptedData);
            fi2.close();
            byte decryptedData[] = cipher.doFinal(encryptedData);
            
            return new String(decryptedData);
            
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public void encryClass()
    {
        SecureRandom sr = new SecureRandom();
        FileInputStream fi = null;
        byte rawKeyData[];
        try
        {
            fi = new FileInputStream(new File("key.txt"));
            rawKeyData = new byte[fi.available()];
            fi.read(rawKeyData);
            fi.close();
            
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            FileInputStream fi2 = new FileInputStream(new File("DigestPass.class"));
            byte data[] = new byte[fi2.available()];
            fi2.read(data);
            fi2.close();
            byte encryptedData[] = cipher.doFinal(data);
            FileOutputStream fo = new FileOutputStream(new File("DigestPass.class"));
            fo.write(encryptedData);
            fo.close();
            
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
       
    }
    
    public void decryClass()
    {
        SecureRandom sr = new SecureRandom();
        FileInputStream fi;
        try
        {
            fi = new FileInputStream(new File("key.txt"));
            byte rawKeyData[] = new byte[fi.available()];// = new byte[5];
            fi.read(rawKeyData);
            fi.close();
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            FileInputStream fi2 = new FileInputStream(new File("DigestPass.class"));
            byte encryptedData[] = new byte[fi2.available()];
            fi2.read(encryptedData);
            fi2.close();
            byte decryptedData[] = cipher.doFinal(encryptedData);
            
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // FileOutputStream fo = new FileOutputStream(new
        // File("DigestPass.class"));
        // fo.write(decryptedData);
        
       /* MyClassloader mcl = new MyClassloader("E:/");
        Class cl = mcl.loadClass(decryptedData, "com.neusoft.jiami.DigestPass");
        DigestPass dp = cl.newInstance();*/
         
    }
    
    
     
    
    
    /*public static void main(String[] args)
    {
        EncryDecry ed = new EncryDecry();
 
        
        byte[] rawKeyData ="hundsun_mwa".getBytes();
        String fileName = EncryDecry.md5("userDb");
        
        
        EncryDecry.encryString(fileName, fileName);
        EncryDecry.decryUserInfo(ed, rawKeyData, fileName);
    }*/
    
}
