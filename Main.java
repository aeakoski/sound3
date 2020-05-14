import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Array;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        //String s = encrypt("hello");
        //InputStream is_bytes = encrypt_bytes();
        //System.out.println(s);

        bytePlayMusic("/home/koski/Desktop/inception.wav");

        //playMusic("/home/koski/Desktop/test.wav");
        //playMusic("/home/koski/Desktop/inception.wav");

    }


    public static void bytePlayMusic(String filepth){
        InputStream music;
        try{
            music = new FileInputStream(new File(filepth));

            byte[] music_bytes = toByteArray(music);

            System.out.println("Bytes length of .wav");
            System.out.println(music_bytes.length);

            byte[] music_headder = new byte[44];
            byte[] music_data = new byte[music_bytes.length-44];

            for (int i = 0; i < music_bytes.length; i++) {
                if (i < 44){
                    music_headder[i] = music_bytes[i];
                } else {
                    music_data[i-44] = music_bytes[i];
                }
            }

            byte[] encrypted_wav = new byte[music_bytes.length];

            byte[] encrypted_music_data = encrypt_bytes(music_data);

            // encrypted_wav = music_headder_bytes + encrypted_music_bytes
            System.out.println("music_headder.length");
            System.out.println(music_headder.length);
            System.out.println("music_data.length");
            System.out.println(music_data.length);
            System.out.println("encrypted_music_data.length");
            System.out.println(encrypted_music_data.length);

            byte[] fixed_encrypted_music_data = new byte[music_data.length];
            for (int i = 0; i < music_data.length; i++) {
                fixed_encrypted_music_data[i] = encrypted_music_data[i];
            }

            System.out.println("fixed_encrypted_music_data.length");
            System.out.println(fixed_encrypted_music_data.length);

            System.arraycopy(music_headder, 0, encrypted_wav, 0, 44);
            System.arraycopy(fixed_encrypted_music_data, 0, encrypted_wav, 44, music_data.length);

            encrypted_wav[encrypted_wav.length-1] = 0;
            encrypted_wav[encrypted_wav.length-2] = 1;

            System.out.println("encrypted_wav");
            System.out.println(encrypted_wav.length);

            //System.out.println(encrypted_wav[encrypted_wav.length-2]);
            //System.out.println(encrypted_wav[encrypted_wav.length-1]);
            //System.out.println(music_bytes[music_bytes.length-2]);
            //System.out.println(music_bytes[music_bytes.length-1]);

            byte[] regular_wav = new byte[music_bytes.length];
            System.arraycopy(music_headder, 0, regular_wav, 0, 44);
            System.arraycopy(music_data, 0, regular_wav, 44, music_data.length);
            int startAt = 128;
            for (int i = startAt; i < music_bytes.length; i++) {
                regular_wav[i] = encrypted_music_data[i-startAt];
            }
            regular_wav[regular_wav.length-1] = 4;
            regular_wav[regular_wav.length-2] = 4;

            InputStream is_enc_music = new ByteArrayInputStream(regular_wav);
            AudioStream audios = new AudioStream(is_enc_music);

            System.out.println("Here");
            AudioPlayer.player.start(audios);
        }
        catch(Exception e){
            System.out.println("Error");
            System.out.println(e);
        }

    }

    public static void playMusic(String filepath){
        InputStream music;
        try{
            music = new FileInputStream(new File(filepath));
            AudioStream audios = new AudioStream(music);
            AudioPlayer.player.start(audios);
        }
        catch(Exception e){
            System.out.println("Error");
            System.out.println(e);
        }

    }

    private static String secretKey = "boooooooooom!!!!";
    private static String salt = "ssshhhhhhhhhhh!!!!";

    public static String encrypt(String strToEncrypt)
    {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] sb = strToEncrypt.getBytes("UTF-8");


            return Base64.getEncoder().encodeToString(cipher.doFinal(sb));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static byte[] encrypt_bytes(byte[] ba)
    {
        InputStream is = new ByteArrayInputStream(ba);
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            byte[] sb = toByteArray(is);

            return cipher.doFinal(sb);

        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }
}
