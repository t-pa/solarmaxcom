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
 * A lookup table that converts Integers to Strings and vice versa. It is used for LookupKeys,
 * which translate numbers into text. backward(forward(i)) yields i, but forward(backward(s))
 * may yield a String different from s because the textual representation is not unique.
 */
public interface LookupTable {
    public String forward(Integer i);
    public Integer backward(String s);
}
