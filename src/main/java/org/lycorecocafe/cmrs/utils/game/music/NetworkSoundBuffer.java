package org.lycorecocafe.cmrs.utils.game.music;

import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.AudioStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkSoundBuffer {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // 从 URL 获取 SoundBuffer
    public static CompletableFuture<SoundBuffer> getSoundBufferFromURL(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    OggAudioStream oggStream = new OggAudioStream(inputStream);
                    SoundBuffer soundBuffer;
                    try {
                        ByteBuffer byteBuffer = oggStream.readAll();
                        soundBuffer = new SoundBuffer(byteBuffer, oggStream.getFormat());
                    } finally {
                        oggStream.close();
                    }
                    return soundBuffer;
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    // 从 URL 获取 AudioStream
    public static CompletableFuture<AudioStream> getAudioStreamFromURL(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream();
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
                    byte[] audioData = byteArrayOutputStream.toByteArray();
                    InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

                    // 使用 OggAudioStream 创建 AudioStream
                    return new OggAudioStream(byteArrayInputStream);
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    // 从 URL 获取 InputStream
    public static CompletableFuture<InputStream> getInputStreamFromURL(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream();
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    byte[] audioData = byteArrayOutputStream.toByteArray();
                    return new ByteArrayInputStream(audioData);
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
