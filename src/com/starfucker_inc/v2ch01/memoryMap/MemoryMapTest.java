package com.starfucker_inc.v2ch01.memoryMap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;

/**
 * 测试CRC校验和的四种方式
 * @author ayorfree
 * @create 2017-04-04-上午8:37
 */

public class MemoryMapTest {
    public static long checksumInputStream(Path filename) throws IOException{
        try (InputStream in = Files.newInputStream(filename)){
            CRC32 crc = new CRC32();

            int c;
            while ((c = in.read()) != -1)
                crc.update(c);
            return crc.getValue();
        }
    }

    public static long checksumBufferedInputStream(Path filename) throws IOException{
        try (InputStream in = new BufferedInputStream(Files.newInputStream(filename))){
            CRC32 crc = new CRC32();

            int c;
            while ((c = in.read()) != -1)
                crc.update(c);
            return crc.getValue();
        }
    }

    public static long checksumRandomAccessFile(Path filename) throws IOException{
        try (RandomAccessFile file = new RandomAccessFile(filename.toFile(), "r")) {
            long length = file.length();
            CRC32 crc = new CRC32();

            for (long p = 0; p < length; p++) {
                file.seek(p);
                int c = file.readByte();
                crc.update(c);

            }
            return crc.getValue();
        }
    }

    public static long checksumMappiedFile(Path filename) throws IOException{
        try (FileChannel channel = FileChannel.open(filename)) {
            CRC32 crc = new CRC32();
            int length = (int) channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);

            for (int p = 0; p < length; p++) {
                int c = buffer.get();
                crc.update(c);
            }
            return crc.getValue();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("InputStream:");
        long start = System.currentTimeMillis();
        Path filename = Paths.get(args[0]);
        long crcValue = checksumInputStream(filename);
        long end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + "millsseconds");

        System.out.println("Buffered InputStream:");
        start = System.currentTimeMillis();
        crcValue = checksumBufferedInputStream(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + "millsseconds");

        System.out.println("Random Access Files");
        start = System.currentTimeMillis();
        crcValue = checksumRandomAccessFile(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + "millsseconds");

        System.out.println("Mapped File");
        start = System.currentTimeMillis();
        crcValue = checksumMappiedFile(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + "millsseconds");
    }
}
