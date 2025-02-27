package sbp.school.kafka.service;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * Класс подсчета контрольной суммы.
 * @version 1.0
 */
public class CheckSumService {

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

}
