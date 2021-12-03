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

import java.util.HashMap;

/**
 * A simple two-way lookup table that consists of two HashMaps.
 */
public abstract class SimpleLookupTable implements LookupTable {
    private final String UNKNOWN_CODE = "unknown-code-";
    
    private final HashMap<Integer, String> forwardTable = new HashMap<>();
    private final HashMap<String, Integer> backwardTable = new HashMap<>();
    
    @Override
    public String forward(Integer i) {
        String s = forwardTable.get(i);
        if (s == null) {
            s = UNKNOWN_CODE + i.toString();
        }
        return s;
    }
    
    @Override
    public Integer backward(String s) {
        if (s.startsWith(UNKNOWN_CODE)) {
            return Integer.parseInt(s.substring(UNKNOWN_CODE.length()));
        } else {
            return backwardTable.get(s);
        }
    }
    
    protected void add(Integer i, String s) {
        if (forwardTable.containsKey(i) || backwardTable.containsKey(s)) {
            throw new IllegalArgumentException("Key already present.");
        }
        
        forwardTable.put(i, s);
        backwardTable.put(s, i);
    }
}
