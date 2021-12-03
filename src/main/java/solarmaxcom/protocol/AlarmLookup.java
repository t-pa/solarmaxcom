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

/**
 * Translate the bit-wise error variable into a textual representation.
 */
public final class AlarmLookup extends BinaryLookupTable {
    
    public AlarmLookup() {
        super("No Error");
        
        setBitstring(0, "External Fault 1");
        setBitstring(1, "Insulation fault DC side");
        setBitstring(2, "Earth fault current too large");
        setBitstring(3, "Fuse failure midpoint Earth");
        setBitstring(4, "External alarm 2");
        setBitstring(5, "Long-term temperature limit");
        setBitstring(6, "Error AC supply");
        setBitstring(7, "External alarm 4");
        setBitstring(8, "Fan failure");
        setBitstring(9, "Fuse failure");
        setBitstring(10, "Failure temperature sensor");
    }
}
