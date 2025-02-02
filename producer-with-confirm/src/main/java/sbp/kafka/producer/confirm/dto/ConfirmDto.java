package sbp.kafka.producer.confirm.dto;


public class ConfirmDto {

    private long checkSum;

    private long startTimestamp;

    private long endTimestamp;

    public ConfirmDto(long checkSum, long startTimestamp, long endTimestamp) {
        this.checkSum = checkSum;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    @Override
    public String toString() {
        return "ConfirmDto{" +
                "checkSum=" + checkSum +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }

}
