package me.jarkimzhu.libs.network;

/**
 * @author JarkimZhu
 * Created on 2016/2/2.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class Packet {
    private Integer packetId;
    private Integer sequenceId;

    public Integer getPacketId() {
        return packetId;
    }
    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }
    public Integer getSequenceId() {
        return sequenceId;
    }
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }
}
