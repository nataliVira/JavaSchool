package sbp.kafka.consumer.confirm.dto;


public class ConfirmDto {
    public ConfirmDto() {
    }

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

    public void setCheckSum(long checkSum) {
        this.checkSum = checkSum;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
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
