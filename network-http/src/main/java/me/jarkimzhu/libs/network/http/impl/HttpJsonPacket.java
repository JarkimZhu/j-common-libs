package me.jarkimzhu.libs.network.http.impl;

import me.jarkimzhu.libs.network.Packet;
import me.jarkimzhu.libs.protocol.json.ProtocolConverterFastJsonImpl;

public class HttpJsonPacket extends Packet {
    private String uri;

    public HttpJsonPacket(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String toString(ProtocolConverterFastJsonImpl converter) {
        return converter.toString(this);
    }
}
