
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

        //bytePlayMusic("/home/koski/Desktop/inception.wav");
        SoundManager sm = new SoundManager();
        try{
            // /home/koski/sound3/src/inception.wav
            sm.addClipToPlaylist("inception.wav");

            InputStream enc_is = bytePlayMusic("/home/koski/sound3/src/inception.wav");
            sm.addInputStreamClipToPlayList(enc_is);

            sm.playSound(0);
            sm.playSound(1);
        } catch (Exception e){
            System.out.println("Error loading test.wav");
            System.out.println(e);
        }


    }

    public static InputStream bytePlayMusic(String filepth){
        InputStream music;
        try{
            music = new FileInputStream(new File(filepth));

            byte[] music_bytes = Convert.inputStreamToByteArray(music);

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

            byte[] encrypted_music_data = Convert.bytesToEncryptedBytes(music_data); //encrypt_bytes(music_data);

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

            return Convert.byteArrayToInputStream(regular_wav);

        }
        catch(Exception e){
            System.out.println("Error");
            System.out.println(e);
        }
        return null;

    }



}
