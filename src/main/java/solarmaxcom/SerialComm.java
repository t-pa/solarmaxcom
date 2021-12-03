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

import com.fazecast.jSerialComm.SerialPort;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps communicating with a SolarMax device connected via a serial port.
 */
public class SerialComm {
    
    private static final Logger logger = LoggerFactory.getLogger(SerialComm.class);
    
    private static final byte END_BYTE = "}".getBytes(StandardCharsets.ISO_8859_1)[0];

    private SerialPort port = null;
    
    public void init(String portDescriptor) {
        logger.debug("Opening serial port " + portDescriptor);
        port = SerialPort.getCommPort(portDescriptor);
        port.openPort();
        port.setBaudRate(19200);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 3000, 3000);
    }
    
    public void close() {
        if (port != null) {
            port.closePort();
        }
    }
    
    public String makeRequest(String message) {
        byte[] request = message.getBytes(StandardCharsets.ISO_8859_1);
        port.writeBytes(request, request.length);
        
        byte[] responseByte = new byte[1];
        int bytesRead = 0;
        StringBuilder response = new StringBuilder();
        do {
            bytesRead = port.readBytes(responseByte, 1);
            if (bytesRead > 0) {
                response.append(new String(responseByte, StandardCharsets.ISO_8859_1));
            } else {
                logger.debug("Timeout reached while reading from serial port.");
            }
        } while (bytesRead > 0 && responseByte[0] != END_BYTE);
        
        return response.toString();
    }
    
}
