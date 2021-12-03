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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KeysTest {
    
    @Test
    public void testGetKeyById() {
        assertEquals(Keys.AC_OUTPUT, Keys.getKeyById("PAC"));
        
        assertEquals(Keys.CURRENT_DC, Keys.getKeyById("IDC"));
    }
    
    @Test
    public void testEncodeDecode() {
        LocalDate today = LocalDate.now();
        assertEquals(today, Keys.DATE.decode(Keys.DATE.encode(today)));
        
        LocalTime now = LocalTime.now().with(ChronoField.MILLI_OF_SECOND, 0);
        assertEquals(now, Keys.TIME.decode(Keys.TIME.encode(now)));
        
        assertEquals("50", Keys.TYPE.encode(Keys.TYPE.decode("50")));
        assertEquals("10", Keys.TYPE.encode(Keys.TYPE.decode("10")));
        
        assertEquals("External Fault 1, Earth fault current too large", Keys.SYSTEM_ALARMS.decode(
                Keys.SYSTEM_ALARMS.encode("External Fault 1, Earth fault current too large")));
        
        assertEquals("4ed0", Keys.SYSTEM_STATUS.encode(Keys.SYSTEM_STATUS.decode("4ed0")));
    }
    
}
