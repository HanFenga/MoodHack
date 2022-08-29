package dev.hanfeng.zhebushigudu.xufangggg.util;

import com.google.common.hash.Hashing;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class RenderUtil2D {
    private static final String URLS = "http://82.157.233.49/HWID/KKGod/hwid.txt";
    private static final List<String> hwids;

    static {
        try {
            hwids=new BufferedReader(new InputStreamReader(new URL(URLS).openStream())).lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void verify(){
        String hwid=getEncryptedHWID("CrackGay");
        if(!hwids.contains(hwid)){
            Method shutdownMethod;
            JOptionPane.showMessageDialog(null, "Not Passed Verify Hwid: " + hwid, "KKHack Protector-VerifyFailure", JOptionPane.PLAIN_MESSAGE, UIManager.getIcon("OptionPane.waringIcon"));
            copyToClipboard(hwid);
            try {
                shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutdownMethod.setAccessible(true);
                shutdownMethod.invoke(null, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void copyToClipboard(String s) {
        StringSelection selection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents((Transferable)selection, (ClipboardOwner)selection);
    }


    public static byte[] rawHWID() throws NoSuchAlgorithmException {
        String main = System.getenv((String)"PROCESS_IDENTIFIER") + System.getenv((String)"PROCESSOR_LEVEL") + System.getenv((String)"PROCESSOR_REVISION") + System.getenv((String)"PROCESSOR_ARCHITECTURE") + System.getenv((String)"PROCESSOR_ARCHITEW6432") + System.getenv((String)"NUMBER_OF_PROCESSORS") + System.getenv((String)"COMPUTERNAME");
        byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance((String)"MD5");
        return messageDigest.digest(bytes);
    }

    public static String Encrypt(String strToEncrypt, String secret) {
        try {
            Cipher cipher = Cipher.getInstance((String)"AES/ECB/PKCS5Padding");
            cipher.init(1, getKey(secret));
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e) {
            System.out.println("Error while encrypting: " + (Object)((Object)e));
            return null;
        }
    }

    public static SecretKeySpec getKey(String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance((String)"SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf((byte[])key, (int)16);
            return new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getEncryptedHWID(String key) {
        try {
            String a = Hashing.sha1().hashString((CharSequence)new String(rawHWID(), StandardCharsets.UTF_8), StandardCharsets.UTF_8).toString();
            String b = Hashing.sha256().hashString((CharSequence)a, StandardCharsets.UTF_8).toString();
            String c = Hashing.sha512().hashString((CharSequence)b, StandardCharsets.UTF_8).toString();
            String d = Hashing.sha1().hashString((CharSequence)c, StandardCharsets.UTF_8).toString();
            return Encrypt(d, "HanFengIsYourFather" + key);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }
}
