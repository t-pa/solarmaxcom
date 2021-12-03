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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author t-pa <t-pa@posteo.de>
 */
public class PacketTest {
    @Test
    public void testBuild() {
        Packet p = new Packet();
        p.payload.put(Keys.DC_INPUT, "0");
        assertEquals("{FB;00;18|64:PDC=0|04A7}", p.build());
    }
    
    @Test
    public void testCrc() {
        assertThrows(Packet.ParseException.class, () -> Packet.parse("{FB;00;18|64:PDC=0|04A8}"));
    }
    
    @Test
    public void testParse() throws Packet.ParseException {
        Packet p = Packet.parse("{FB;00;1D|64:CLR;PAC=2d|0632}");
        assertEquals("2d", p.payload.get(Keys.AC_OUTPUT));
        assertEquals(true, p.payload.containsKey(Keys.CLEAR_ENERGY_COUNTERS));
    }
}
