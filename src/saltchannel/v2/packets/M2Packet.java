package saltchannel.v2.packets;

import saltchannel.BadPeer;
import saltchannel.util.Deserializer;
import saltchannel.util.Serializer;

/**
 * Data of M2 message, low-level serialization / deserialization.
 * 
 * @author Frans Lundberg
 */
public class M2Packet implements Packet {
    public static final int PACKET_TYPE = 2;
    public boolean noSuchServer;
    public byte[] serverEncKey;
    
    public int getType() {
        return PACKET_TYPE;
    }
    
    public int getSize() {
        return PacketHeader.SIZE + 32;
    }
    
    public boolean hasServerEncKey() {
        return serverEncKey != null;
    }
    
    public void toBytes(byte[] destination, int offset) {
        if (serverEncKey == null) {
            throw new IllegalStateException("serverEncKey not set");
        }
        
        if (serverEncKey.length != 32) {
            throw new IllegalStateException("bad serverEncKey length");
        }
        
        Serializer s = new Serializer(destination, offset);
        PacketHeader header = new PacketHeader(PACKET_TYPE);
        header.setBit(0, noSuchServer);
        
        s.writeHeader(header);        
        s.writeBytes(serverEncKey);
    }
    
    public byte[] toBytes() {
        byte[] result = new byte[getSize()];
        toBytes(result, 0);
        return result;
    }
    
    public static M2Packet fromBytes(byte[] source, int offset) {
        M2Packet p = new M2Packet();
        Deserializer d = new Deserializer(source, offset);
        
        PacketHeader header = d.readHeader();
        int packetType = header.getType();
        if (packetType != PACKET_TYPE) {
            throw new BadPeer("unexpected packet type, " + packetType);
        }
        
        p.noSuchServer = header.getBit(0);
        p.serverEncKey = d.readBytes(32);
        
        return p;
    }
}