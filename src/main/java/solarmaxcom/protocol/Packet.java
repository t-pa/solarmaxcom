/*
 * Copyright (C) 2021 t-pa <t-pa@posteo.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package solarmaxcom.protocol;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import solarmaxcom.protocol.Keys.Key;

/**
 * A Packet which can be send to or received from a SolarMax device. This class can build() valid
 * packets and parse() Strings containing a packet. The protocol description can be found at
 * https://www.solarmax.com/Downloads/MaxComm_Protocol_Description_EN.pdf .
 */
public class Packet {
    public final HashMap<Key, String> payload = new HashMap<>();
    
    public static final int ADR_BROADCAST = 0;
    public static final int ADR_NETWORK_MASTER = 250;
    public static final int ADR_ALTERNATIVE_NETWORK_MASTER = 251;
    public static final int ADR_MAX_DISPLAY = 252;
    public static final int ADR_UNINITIALIZED = 255;
    
    public static final int PORT_USER_DATA = 100;
    public static final int PORT_COMMAND = 200;
    public static final int PORT_MSG_FROM_INTERFACE = 1000;
    
    public static final String STX = "{";
    public static final String ETX = "}";
    public static final String ETB = ")";
    public static final String FS = ";";
    public static final String FRS = "|";
    public static final String US = ":";
    
    public int source = ADR_ALTERNATIVE_NETWORK_MASTER;
    public int destination = ADR_BROADCAST;
    public int port = PORT_USER_DATA;

    public static class ParseException extends Exception {
        public ParseException(String msg) {
            super(msg);
        }
    }
    
    private static int expect(final String subString, final String message, final int pos) throws ParseException {
        if (!message.startsWith(subString, pos)) {
            throw new ParseException("Invalid message: Expected " + subString +
                    " at position " + pos + " in message '" + message + "'.");
        } else {
            return pos + subString.length();
        }
    }
    
    private static int find(final String subString, final String message, final int pos) throws ParseException {
        int pos2 = message.indexOf(subString, pos);
        if (pos2 == -1) {
                throw new ParseException("Invalid message: Could not find " + subString + 
                        " after position " + pos);
        } else {
            return pos2;
        }
    }
    
    private static int crc(final String message) {
        int crcSum = 0;
        for (byte b : message.getBytes(StandardCharsets.ISO_8859_1)) {
            crcSum += b;
        }
        return crcSum & 0xFFFF;
    }
    
    private static void parseFragment(final String message, final Packet packet) throws ParseException {
        int pos = 0;
        try {
            pos = expect(STX, message, pos);
            int crcStart = pos;
            
            packet.source = Integer.parseInt(message.substring(pos, pos+2), 16);
            pos += 2;
            
            pos = expect(FS, message, pos);
            
            packet.destination = Integer.parseInt(message.substring(pos, pos+2), 16);
            pos += 2;
            
            pos = expect(FS, message, pos);

            int length = Integer.parseInt(message.substring(pos, pos+2), 16);
            if (message.length() != length) {
                throw new ParseException("Invalid message: Length " + length + " expected, but " +
                        "message '" + message + "' has length " + message.length() + ".");
            }
            pos += 2;
            
            pos = expect(FRS, message, pos);
            
            int pos2 = find(US, message, pos);
            packet.port = Integer.parseInt(message.substring(pos, pos2), 16);
            pos = pos2 + US.length();
            
            pos2 = message.indexOf(FRS, pos);
            String data = message.substring(pos, pos2);
            pos = pos2 + FRS.length();
            
            int calculatedCrc = crc(message.substring(crcStart, pos));
            int crc = Integer.parseInt(message.substring(pos, pos+4), 16);
            if (crc != calculatedCrc) {
                throw new ParseException("Invalid message: CRC " + crc + " expected, but " +
                        "message '" + message + "' has CRC " + calculatedCrc + ".");
            }
            pos += 4;
            
            expect(ETX, message, pos);
            
            for (String datum : data.split(";")) {
                pos2 = datum.indexOf("=");
                String keyId = (pos2 == -1) ? datum : datum.substring(0, pos2);
                String value = (pos2 == -1) ? null : datum.substring(pos2+1);
                
                Key key = Keys.getKeyById(keyId);
                if (key == null) {
                    throw new ParseException("Invalid message: Key '" + keyId + "' unknown.");
                } else {
                    packet.payload.put(key, value);
                }
            }
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid message: Could not parse number at position " + pos +
                    " in message '" + message + "'.");
        } catch (StringIndexOutOfBoundsException e) {
            throw new ParseException("Invalid message: Message incomplete; last item position " + pos +
                    " in message '" + message + "'.");

        }

    }

    public static Packet parse(final String message) throws ParseException {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(Packet.class);

        final Packet packet = new Packet();
        
        String allFragments = message.replace(ETB, ETX);
        for (String fragment : allFragments.split(ETX)) {
            parseFragment(fragment + ETX, packet);
        }
        
        return packet;
    }
    
    public String build() {
        StringBuilder packet = new StringBuilder();
        
        packet.append(STX);
        int crcStart = packet.length();
        packet.append(String.format("%02X", source));
        packet.append(FS);
        packet.append(String.format("%02X", destination));
        packet.append(FS);
        
        int posLength = packet.length();
        packet.append("XX"); // will be replaced later
        
        packet.append(FRS);
        packet.append(String.format("%X", port));
        packet.append(US);
        
        for (Map.Entry<Key, String> e : payload.entrySet()) {
            packet.append(e.getKey().key);
            if (e.getValue() != null) {
                packet.append("=");
                packet.append(e.getValue());
            }
            packet.append(FS);
        }
        packet.deleteCharAt(packet.length()-1);  // delete final FS
        
        packet.append(FRS);
        int posCRC = packet.length();
        packet.append("XXXX"); // will be replaced later
        packet.append(ETX);
        
        // insert length
        int length = packet.length();
        if (length > 255) {
            throw new IllegalStateException("Packet is longer than 255 characters.");
        }
        packet.replace(posLength, posLength+2, String.format("%02X", length));
        
        // insert crc
        int crcSum = crc(packet.substring(crcStart, posCRC));
        String crc = String.format("%04X", crcSum & 0xFFFF);
        packet.replace(posCRC, posCRC+4, crc);
        
        return packet.toString();
    }
    
    public String format() {
        String s = "Source: " + source + "\n";
        s += "Destination: " + destination + "\n";
        s += "Port: " + port + "\n";
        
        for (Keys.Key k : payload.keySet()) {
            String value = payload.get(k);
            if (value != null) {
                if (!value.equals("") && k instanceof Keys.DataKey) {
                    Keys.DataKey dataKey = (Keys.DataKey) k;
                    String unit = "";
                    if (dataKey instanceof Keys.IntegerKey) {
                        unit = ((Keys.IntegerKey) dataKey).unit;
                    }
                    if (dataKey instanceof Keys.DoubleKey) {
                        unit = ((Keys.DoubleKey) dataKey).unit;
                    }

                    s += k.key + " (" + k.name + "): " + dataKey.decode(value) + " " + unit + "\n";
                } else {
                    s += k.key + " (" + k.name + "): " + value + "\n";
                }
            } else {
                s += "Key " + k.key + "(" + k.name + ") contains null value.\n";
            }
        }
        return s;
    }
    

}
