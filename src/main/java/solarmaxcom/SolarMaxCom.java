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

package solarmaxcom;

import solarmaxcom.protocol.Packet;
import solarmaxcom.protocol.Keys;
import ch.qos.logback.classic.Level;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main class which parses the command line, reads values from a SolarMax device and optionally
 * sends the received data via MQTT.
 */
@Command(name = "SolarMaxCom", version = "SolarMaxCom version 0.1.0", mixinStandardHelpOptions = true)
public final class SolarMaxCom implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(SolarMaxCom.class);
    private static final int MAX_KEYS_PER_REQUEST = 3;

    @CommandLine.Parameters(paramLabel = "<port>", description = "serial port, e.g. /dev/ttyUSB0")
    private String portDescriptor;
    
    @CommandLine.Parameters(paramLabel = "<keys>",
            description = "comma-separated list of keys to request; leave empty to use all known data keys",
            defaultValue = "")
    private String keyList;
    
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "more verbose output")
    private boolean verbose;
    
    @CommandLine.Option(names = {"-d", "--device"}, description = "device address (default: 0 for broadcast)")
    private int device;
    
    @CommandLine.Option(names = {"-m", "--mqtt"}, description = "MQTT server to send data to")
    private String mqttServer;
    
    @CommandLine.Option(names = {"-r", "--reptime"}, defaultValue = "0",
            description = "repeatedly request data after <reptime> milliseconds until a key is pressed")
    private int reptime;

    private SerialComm smc;
    private Mqtt3BlockingClient mqtt;
    
    @Override
    public void run() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(Level.INFO);
        if (verbose) {
            ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("solarmaxcomm")).setLevel(Level.DEBUG);
        }
        
        if (mqttServer != null) {
            mqtt = Mqtt3Client.builder().serverHost(mqttServer).buildBlocking();
            mqtt.connect();
        }
        
        smc = new SerialComm();
        smc.init(portDescriptor);
        
        HashSet<Keys.Key> keys = new HashSet<>();
        if (keyList.equals("")) {
            logger.info("Testing all known data keys...");
            for (Keys.Key k : Keys.getKeys()) {
                if (k instanceof Keys.DataKey) {
                    keys.add(k);
                }
            }
        } else {
            for (String key : keyList.split(",")) {
                Keys.Key k = Keys.getKeyById(key);
                if (k == null) {
                    logger.warn("Ignoring unknown key " + key);
                } else {
                    keys.add(k);
                }
            }
        }
            
        if (!keys.isEmpty()) {
            do {
                if (reptime > 0) {
                    logger.info("Requesting keys; press Enter to break request loop.");
                } else {
                    logger.info("Requesting keys...");
                }

                Map<Keys.Key, String> reply = requestKeys(device, keys);
                if (mqtt != null) {
                    String online = (reply.isEmpty()) ? "0" : "1";
                    mqtt.publishWith().topic("solarmax/" + device + "/online")
                                    .payload(online.getBytes())
                                    .send();
                }
                for (Keys.Key k : reply.keySet()) {
                    String value = reply.get(k);
                    if (value != null && (k instanceof Keys.DataKey)) {
                        Keys.DataKey dataKey = (Keys.DataKey) k;
                        String unit = "";
                        if (dataKey instanceof Keys.IntegerKey) {
                            unit = ((Keys.IntegerKey) dataKey).unit;
                        }
                        if (dataKey instanceof Keys.DoubleKey) {
                            unit = ((Keys.DoubleKey) dataKey).unit;
                        }

                        logger.info(k.key + " " + k.name + ": " + 
                                dataKey.standardFormat(dataKey.decode(value)) + " " + unit);
                        if (mqtt != null) {
                            mqtt.publishWith().topic("solarmax/" + device + "/" + k.key)
                                    .payload(dataKey.standardFormat(dataKey.decode(value)).getBytes())
                                    .send();
                        }
                    } else {
                        logger.info(k.key + " " + k.name + ": " + value);
                    }
                }

                try {
                    if (reptime > 0) {
                        if (System.in.available() > 0) {
                            reptime = 0;
                        } else {
                            Thread.sleep(reptime);
                            if (System.in.available() > 0) reptime = 0;
                        }
                    }
                } catch (InterruptedException | IOException ex) {}
                
            } while (reptime > 0);
        } else {
            logger.warn("No valid keys.");
        }

        logger.info("Closing serial interface...");
        smc.close();
        if (mqtt != null) {
            mqtt.disconnect();
        }
    }
    
    private Map<Keys.Key, String> requestKeys(final int device, final Set<Keys.Key> keys) {
        Keys.Key[] keyArray = keys.toArray(new Keys.Key[0]);
        HashMap<Keys.Key, String> returnedKeys = new HashMap<>();
        
        for (int i=0; i < keyArray.length; i += MAX_KEYS_PER_REQUEST) {
            Packet request = new Packet();
            request.destination = device;
            for (int k=0; k < MAX_KEYS_PER_REQUEST && k+i < keyArray.length; k++) {
                request.payload.put(keyArray[k+i], null);
            }
            
            String message = request.build();
            logger.debug("Sending request: '" + message + "'");
            String replyMessage = smc.makeRequest(message);
            if (replyMessage.length() > 0) {
                logger.debug("Got reply: '" + replyMessage + "'");
                try {
                    Packet reply = Packet.parse(replyMessage);
                    returnedKeys.putAll(reply.payload);
                    if (logger.isDebugEnabled()) {
                        logger.debug(reply.format());
                    }
                } catch (Packet.ParseException e) {
                    logger.warn("Could not parse packet: " + e);
                }
            } else {
                logger.info("No reply received.");
            }
        }
        
        return returnedKeys;
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new SolarMaxCom()).execute(args);
        System.exit(exitCode);
    }

}
