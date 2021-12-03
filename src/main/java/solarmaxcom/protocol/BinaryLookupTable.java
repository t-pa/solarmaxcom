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

import java.util.ArrayList;

/**
 * Translates an integer in which each bit signifies a certain text into its textual representation.
 * It is meant to be overridden by a class which sets the bit-texts with definite values.
 */
public abstract class BinaryLookupTable implements LookupTable {

    private final static String DELIMITER = ", ";
    private final ArrayList<String> bitStrings = new ArrayList<>();
    private final String zeroString;
    
    protected BinaryLookupTable(String zeroString) {
        this.zeroString = zeroString;
    }
    
    protected void setBitstring(int bit, String s) {
        bitStrings.add(bit, s);
    }
    
    @Override
    public String forward(Integer i) {
        if (i == 0) {
            return zeroString;
        } else {
            String s = "";
            int bitmask = 1;
            for (int bit=0; bit < bitStrings.size(); bit++) {
                if ((i & bitmask) != 0) {
                    s += (s.length() > 0 ? DELIMITER : "") + bitStrings.get(bit);
                }
                bitmask *= 2;
            }
            return s;
        }
    }

    @Override
    public Integer backward(String s) {
        if (zeroString.equals(s)) {
            return 0;
        } else {
            int i = 0;
            for (String code : s.split(DELIMITER)) {
                int bit = bitStrings.indexOf(code);
                if (bit == -1) {
                    throw new IllegalArgumentException("Bit code " + code + " is unknown.");
                }
                i = i + (1 << bit);
            }
            return i;
        }
    }
    
}
