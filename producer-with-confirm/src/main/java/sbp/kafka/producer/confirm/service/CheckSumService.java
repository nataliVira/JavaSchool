package sbp.kafka.producer.confirm.service;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CheckSumService {

    private static final Checksum crc32 = new CRC32();

    public static long getCRC32Checksum(byte[] bytes) {
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

}
