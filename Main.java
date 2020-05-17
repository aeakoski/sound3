import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ClassLoader loader = Main.class.getClassLoader();
        String[] songs = {"inception.wav", "daftpunk.wav", "rigoletto.wav"};
        System.out.println("Encryption Player!\n");
        SoundManager sm = new SoundManager();
        SoundManager sm_fips = new SoundManager();
        boolean fipsIsEnabled = false;

        try{
            for (String song : songs) {
                sm.addClipToPlaylist(song);

                String filePath = loader.getResource(song).toString();
                filePath = filePath.substring(5);

                InputStream enc_is = encryptMusicFileToInputStream(filePath);
                sm_fips.addInputStreamClipToPlayList(enc_is);

            }



            Scanner scanner = new Scanner(System.in);
            String choice;
            System.out.println("\n\nWelcome to MUSIC PLAYER");
            while (true){
                System.out.println("\n------------------------");
                System.out.println("Playlist, avalible songs");
                if (fipsIsEnabled){
                    System.out.println("IN FIPS MODE");
                }
                for (int songIndex:sm.getSongList()) {
                    System.out.println("Song: " + songIndex);
                }

                System.out.print("Option: ");
                choice = scanner.nextLine();

                if(choice.equals("stop")){
                    if(fipsIsEnabled){
                        sm_fips.stopSound();
                    } else{
                        sm.stopSound();
                    }
                    continue;
                }
                if(choice.equals("exit")){
                    break;
                }
                if(choice.equals("fips")) {
                    fipsIsEnabled = !fipsIsEnabled;
                    System.out.println("FIPS MODE TOGGLED");
                    continue;
                }

                System.out.println("Playing song nr: " + choice);
                if(fipsIsEnabled){
                    sm_fips.playSound(Integer.parseInt(choice));
                } else{
                    sm.playSound(Integer.parseInt(choice));
                }


            }

            System.out.println("Exiting music player");

            scanner.close();

        } catch (Exception e){
            System.out.println("Error loading test.wav");
            System.out.println(e);
        }


    }

    public static InputStream encryptMusicFileToInputStream(String filepth){

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
            System.out.println("Encrypting songs:");
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
