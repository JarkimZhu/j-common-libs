package me.jarkimzhu.libs.network.http.impl;


import me.jarkimzhu.libs.network.IChannel;
import me.jarkimzhu.libs.network.Packet;
import me.jarkimzhu.libs.protocol.json.ProtocolConverterFastJsonImpl;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author JarkimZhu
 *         Created on 2016/2/2.
 * @version 0.0.1-SNAPSHOT
 * @since JDK1.8
 */
public class HttpJsonChannel implements IChannel<String> {

    private ApacheHttpUtilsClient httpClient;

    private ProtocolConverterFastJsonImpl converter;

    public HttpJsonChannel(int socketTimeout, int maxTotal) {
        httpClient = new ApacheHttpUtilsClient(socketTimeout, maxTotal);
    }

    public HttpJsonChannel(int socketTimeout, int maxTotal, ProtocolConverterFastJsonImpl converter) {
        httpClient = new ApacheHttpUtilsClient(socketTimeout, maxTotal);
        this.converter = converter;
    }

    public HttpJsonChannel(int socketTimeout, int maxTotal, int tryTimes) {
        httpClient = new ApacheHttpUtilsClient(socketTimeout, maxTotal, tryTimes);
    }

    @Override
    public String send(Packet packet) {
        HttpJsonPacket p = (HttpJsonPacket) packet;
        return httpClient.post(p.getUri(), p.toString(converter), "application/json");
    }

    @Override
    public <T> T send(Packet packet, Class<T> clazz) {
        return send(packet, (Type) clazz);
    }

    @Override
    public <T> T send(Packet packet, Type type) {
        String response = send(packet);
        if (response != null) {
            return converter.toDto(response, type);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public ApacheHttpUtilsClient getHttpClient() {
        return httpClient;
    }

    public ProtocolConverterFastJsonImpl getConverter() {
        return converter;
    }

    public void setConverter(ProtocolConverterFastJsonImpl converter) {
        this.converter = converter;
    }
}
