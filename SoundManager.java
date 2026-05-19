import java.io.*;
import java.util.HashMap;
import javax.sound.sampled.*;

public class SoundManager {
    // 儲存原始數據與格式
    private static HashMap<String, byte[]> soundDataCache = new HashMap<>();
    private static HashMap<String, AudioFormat> formatCache = new HashMap<>();

    public static void loadSounds(String[] fileNames) {
        for (String name : fileNames) {
            try {
                File file = new File("sound" + File.separator + name);
                if (!file.exists()) {
                    System.err.println("找不到音效檔案: " + name);
                    continue;
                }

                // 1. 取得原始音訊串流
                AudioInputStream rawAis = AudioSystem.getAudioInputStream(file);
                AudioFormat baseFormat = rawAis.getFormat();

                // 2. 強制轉換為 PCM 格式 (避免不支援非壓縮格式)
                AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                );

                AudioInputStream ais = AudioSystem.getAudioInputStream(decodedFormat, rawAis);
                
                // 3. 讀取轉換後的數據到 byte[]
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = ais.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }

                // 4. 快取數據與格式
                soundDataCache.put(name, baos.toByteArray());
                formatCache.put(name, decodedFormat);

                ais.close();
                rawAis.close();
                System.out.println("預載成功: " + name);
            } catch (Exception e) {
                System.err.println("無法預載音效: " + name + "，錯誤: " + e.getMessage());
            }
        }
    }

    public static void playSound(String name) {
        if (!soundDataCache.containsKey(name) || !formatCache.containsKey(name)) return;

        new Thread(() -> {
            try {
                byte[] data = soundDataCache.get(name);
                AudioFormat format = formatCache.get(name);
                
                // 5. 重新包裝成 AudioInputStream
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                AudioInputStream ais = new AudioInputStream(bais, format, data.length / format.getFrameSize());
                
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("播放音效失敗: " + name);
                e.printStackTrace();
            }
        }).start();
    }
}