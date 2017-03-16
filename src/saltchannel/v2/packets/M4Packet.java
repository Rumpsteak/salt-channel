package saltchannel.v2.packets;

import saltchannel.BadPeer;
import saltchannel.util.Deserializer;
import saltchannel.util.Serializer;

/**
 * Low-level M3 packet data, serialization/deserialization.
 * 
 * @author Frans Lundberg
 */
public class M4Packet implements Packet {
    public static final int PACKET_TYPE = 4;
    public byte[] clientSigKey;
    public byte[] signature2;
    
    public int getType() {
        return PACKET_TYPE;
    }
    
    public int getSize() {
        return PacketHeader.SIZE + 32 + 64;
    }
    
    public void toBytes(byte[] destination, int offset) {
        if (clientSigKey == null || clientSigKey.length != 32) {
            throw new IllegalStateException("bad clientSigKey");
        }
        
        if (signature2 == null || signature2.length != 64) {
            throw new IllegalStateException("bad signature2");
        }
        
        Serializer s = new Serializer(destination, offset);
        PacketHeader header = new PacketHeader(PACKET_TYPE);
        s.writeHeader(header);
        s.writeBytes(clientSigKey);
        s.writeBytes(signature2);
    }
    
    public byte[] toBytes() {
        byte[] result = new byte[getSize()];
        toBytes(result, 0);
        return result;
    }
    
    public static M4Packet fromBytes(byte[] source, int offset) {
        M4Packet p = new M4Packet();
        Deserializer d = new Deserializer(source, offset);
        PacketHeader header = d.readHeader();
        
        int packetType = header.getType();
        if (packetType != 4) {
            throw new BadPeer("unexpected packet type");
        }
        
        p.clientSigKey = d.readBytes(32);
        p.signature2 = d.readBytes(64);
        
        return p;
    }
}