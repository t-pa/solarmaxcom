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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

/**
 * Enum-like class which collects all known Keys for communicating with the SolarMax device.
 * All those keys are derived from Keys.Key; those which are used for data transfer (and not only
 * for commands) are derived from Keys.DataKey and can decode and encode the data. All Keys can
 * be enumerated via getKeys().
 */
public final class Keys {
    
    private static final HashMap<String, Key> keys = new HashMap<>();
    
    private Keys() {    
    }
    
    public static Key getKeyById(String key) {
        return keys.get(key);
    }
    
    public static Collection<Key> getKeys() {
        return keys.values();
    }
    
    public static class Key {
        public final String name;
        public final String key;
        
        private Key(String name, String key) {
            this.name = name;
            this.key = key;
            keys.put(key, this);
        }
        
        @Override
        public String toString() {
            return key;
        }
    }
    
    public static abstract class DataKey<T> extends Key {
        public final int length;
        
        private DataKey(String name, String key, int length) {
            super(name, key);
            this.length = length;
        }
        
        public abstract T decode(String encoded);
        public abstract String encode(T value);
        public abstract String standardFormat(T value);
    }
    
    public static class IntegerKey extends DataKey<Integer> {
        public final int offset;
        public final String unit;

        private IntegerKey(String name, String key, int length, int offset, String unit) {
            super(name, key, length);
            this.offset = offset;
            this.unit = unit;
        }
        
        @Override
        public Integer decode(String encoded) {
            if (encoded == null || encoded.equals("")) {
                return null;
            } else {
                return Integer.parseInt(encoded, 16) - offset;
            }
        }

        @Override
        public String encode(Integer value) {
            if (value == null) {
                return "";
            } else {
                if (value + offset < 0) {
                    throw new IllegalArgumentException("Cannot encode " + value +
                            " because it is smaller than the allowed minimum of " + (-offset));
                }
                String encoded = Integer.toString(value + offset, 16);
                if (encoded.length() > length) {
                    throw new IllegalArgumentException("Encoding " + value + " as " +
                            encoded + ": maximum length of " + length + " exceeded.");
                }
                return encoded;
            }
        }

        @Override
        public String standardFormat(Integer value) {
            return value.toString();
        }
        
    }
    
    public static class DoubleKey extends DataKey<Double> {
        public final int offset;
        public final double factor;
        public final String unit;
        private final String stdFormat;

        private DoubleKey(String name, String key, int length, int offset, double factor, String unit) {
            super(name, key, length);
            this.offset = offset;
            this.factor = factor;
            this.unit = unit;

            int decimals = (int) Math.ceil(-Math.log10(factor));
            stdFormat = "%." + decimals + "f";
        }
        
        @Override
        public Double decode(String encoded) {
            if (encoded == null || encoded.equals("")) {
                return null;
            } else {
                return (Integer.parseInt(encoded, 16) - offset) * factor;
            }
        }

        @Override
        public String encode(Double value) {
            if (value == null) {
                return "";
            } else {
                long l = Math.round(value/factor + offset);
                if (l < 0) {
                    throw new IllegalArgumentException("Cannot encode " + value +
                            " because it is smaller than the allowed minimum of " + (-offset*factor));
                }
                String encoded = Long.toString(l, 16);
                if (encoded.length() > length) {
                    throw new IllegalArgumentException("Encoding " + value + " as " +
                            encoded + ": maximum length of " + length + " exceeded.");
                }
                return encoded;
            }
        }

        @Override
        public String standardFormat(Double value) {
            return String.format(stdFormat, value);
        }
        
    }
    
    public static class DateKey extends DataKey<LocalDate> {
        private DateKey(String name, String key, int length) {
            super(name, key, length);
        }
        
        @Override
        public LocalDate decode(String encoded) {
            if (encoded == null || encoded.equals("")) {
                return null;
            } else {
                String[] arr = encoded.split(",");
                int year = Integer.parseInt(arr[0], 16);
                int month = Integer.parseInt(arr[1], 16);
                int day = Integer.parseInt(arr[2], 16);
                return LocalDate.of(year, month, day);
            }
        }

        @Override
        public String encode(LocalDate value) {
            if (value == null) {
                return "";
            } else {
                return Integer.toString(value.getYear(), 16) + "," +
                        Integer.toString(value.getMonthValue(), 16) + "," +
                        Integer.toString(value.getDayOfMonth(), 16);
            }
        }

        @Override
        public String standardFormat(LocalDate value) {
            return value.format(DateTimeFormatter.ISO_DATE);
        }
        
    }
    
    public static class TimeKey extends DataKey<LocalTime> {
        private static final DateTimeFormatter stdFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        private TimeKey(String name, String key, int length) {
            super(name, key, length);
        }
        
        @Override
        public LocalTime decode(String encoded) {
            if (encoded == null || encoded.equals("")) {
                return null;
            } else {
                String[] arr = encoded.split(",");
                int hour = Integer.parseInt(arr[0], 16);
                int minute = Integer.parseInt(arr[1], 16);
                int second = Integer.parseInt(arr[2], 16);
                return LocalTime.of(hour, minute, second);
            }
        }

        @Override
        public String encode(LocalTime value) {
            if (value == null) {
                return "";
            } else {
                return Integer.toString(value.getHour(), 16) + "," +
                        Integer.toString(value.getMinute(), 16) + "," +
                        Integer.toString(value.getSecond(), 16);
            }
        }

        @Override
        public String standardFormat(LocalTime value) {
            return value.format(stdFormat);
        }
        
    }
    
    public static class LookupKey extends DataKey<String> {

        private final LookupTable lookup;
        
        public LookupKey(String name, String key, int length, LookupTable lookup) {
            super(name, key, length);
            this.lookup = lookup;
        }

        @Override
        public String decode(String encoded) {
            Integer i;
            
            if (encoded == null || encoded.equals("")) {
                i = null;
            } else {
                i = Integer.parseInt(encoded, 16);
            }
            
            return lookup.forward(i);
        }

        @Override
        public String encode(String value) {
            Integer i = lookup.backward(value);
            if (value == null) {
                throw new IllegalArgumentException("Cannot find code for '" + value + "'");
            } else {
                return Integer.toString(i, 16);
            }
        }

        @Override
        public String standardFormat(String value) {
            return value;
        }
        
    }

    public static class UnformattedKey extends DataKey<String> {

        private UnformattedKey(String name, String key, int length) {
            super(name, key, length);
        }
        
        @Override
        public String decode(String encoded) {
            return encoded;
        }

        @Override
        public String encode(String value) {
            return value;
        }

        @Override
        public String standardFormat(String value) {
            return value;
        }
        
    }
    
    public static final Key CLEAR_ENERGY_COUNTERS = new Key("Clear energy counters", "CLR");
    public static final IntegerKey NETWORK_ADDRESS = new IntegerKey("Network address", "ADR", 4, 0, "");
    public static final DateKey DATE = new DateKey("Date", "DATE", 10);
    public static final IntegerKey DATE_DAY = new IntegerKey("Date day", "DDY", 4, 0, "d");
    public static final IntegerKey DATE_MONTH = new IntegerKey("Date month", "DMT", 4, 0, "m");
    public static final IntegerKey DATE_YEAR = new IntegerKey("Date year", "DYR", 4, 0, "a");
    public static final DoubleKey PULSE_COUNTER_1_DAY = new DoubleKey("Pulse counter 1 day", "I1D", 8, 0, 0.1, "kWh");
    public static final DoubleKey PULSE_COUNTER_1_POWER = new DoubleKey("Pulse counter 1 power", "I1P", 8, 0, 0.5, "W");
    public static final IntegerKey PULSE_COUNTER_1_SCALING = new IntegerKey("Pulse counter 1 scaling", "I1S", 4, 0, "");
    public static final DoubleKey PULSE_COUNTER_1_TOTAL = new DoubleKey("Pulse counter 1 total", "I1T", 8, 0, 0.1, "kWh");
    public static final DoubleKey PULSE_COUNTER_1_YEAR = new DoubleKey("Pulse counter 1 year", "I1Y", 8, 0, 0.1, "kWh");
    public static final DoubleKey PULSE_COUNTER_2_DAY = new DoubleKey("Pulse counter 2 day", "I2D", 8, 0, 0.1, "kWh");
    public static final DoubleKey PULSE_COUNTER_2_POWER = new DoubleKey("Pulse counter 2 power", "I2P", 8, 0, 0.5, "W");
    public static final IntegerKey PULSE_COUNTER_2_SCALING = new IntegerKey("Pulse counter 2 scaling", "I2S", 4, 0, "");
    public static final DoubleKey PULSE_COUNTER_2_TOTAL = new DoubleKey("Pulse counter 2 total", "I2T", 8, 0, 0.1, "kWh");
    public static final DoubleKey PULSE_COUNTER_2_YEAR = new DoubleKey("Pulse counter 2 year", "I2Y", 8, 0, 0.1, "kWh");
    public static final DoubleKey CURRENT_DC = new DoubleKey("Current DC", "IDC", 4, 0, 0.01, "A");
    public static final DoubleKey CURRENT_PHASE_1 = new DoubleKey("Current phase 1", "IL1", 4, 0, 0.01, "A");
    public static final DoubleKey CURRENT_PHASE_2 = new DoubleKey("Current phase 2", "IL2", 4, 0, 0.01, "A");
    public static final DoubleKey CURRENT_PHASE_3 = new DoubleKey("Current phase 3", "IL3", 4, 0, 0.01, "A");
    public static final DoubleKey ENERGY_DAY = new DoubleKey("Energy day", "KDY", 8, 0, 0.1, "kWh");
    public static final IntegerKey OPERATING_HOURS = new IntegerKey("Operating hours", "KHR", 8, 0, "");
    public static final IntegerKey ENERGY_MONTH = new IntegerKey("Energy month", "KMT", 8, 0, "kWh");
    public static final IntegerKey ENERGY_TOTAL = new IntegerKey("Energy total", "KT0", 8, 0, "kWh");
    public static final IntegerKey ENERGY_YEAR = new IntegerKey("Energy year", "KYR", 8, 0, "kWh");
    public static final DoubleKey AC_OUTPUT = new DoubleKey("AC output", "PAC", 8, 0, 0.5, "W");
    public static final DoubleKey INSTALLED_CAPACITY = new DoubleKey("Installed capacity", "PIN", 8, 0, 0.5, "W");
    public static final IntegerKey RELATIVE_OUTPUT = new IntegerKey("Relative output", "PRL", 4, 0, "%");
    public static final IntegerKey SOLAR_RADIATION = new IntegerKey("Solar radiation", "RAD", 4, 0, "W/m²");
    public static final DoubleKey SOLAR_ENERGY_DAY = new DoubleKey("Solar energy day", "RDY", 4, 0, 0.1, "kWh/m²");
    public static final DoubleKey SOLAR_ENERGY_TOTAL = new DoubleKey("Solar energy total", "RT0", 4, 0, 0.1, "kWh/m²");
    public static final DoubleKey SOLAR_ENERGY_YEAR = new DoubleKey("Solar energy year", "RYR", 4, 0, 0.1, "kWh/m²");
    public static final IntegerKey SOFTWARE_VERSION = new IntegerKey("Software version", "SWV", 4, 0, "");
    public static final IntegerKey TIME_HOUR = new IntegerKey("Time hour", "THR", 4, 0, "h");
    public static final TimeKey TIME = new TimeKey("Time", "TIME", 8);
    public static final IntegerKey TEMPERATURE_POWER_UNIT_2 = new IntegerKey("Temperature power unit 2", "TK2", 4, 0, "°C");
    public static final IntegerKey TEMPERATURE_POWER_UNIT_3 = new IntegerKey("Temperature power unit 3", "TK3", 4, 0, "°C");
    public static final IntegerKey TEMPERATURE_POWER_UNIT_1 = new IntegerKey("Temperature power unit 1", "TKK", 4, 0, "°C");
    public static final IntegerKey TIME_MINUTE = new IntegerKey("Time minute", "TMI", 4, 0, "min");
    public static final IntegerKey MAINS_CYCLE_DURATION = new IntegerKey("Mains cycle duration", "TNP", 4, 0, "µs");
    public static final IntegerKey TEMPERATURE_SOLAR_CELLS = new IntegerKey("Temperature solar cells", "TSZ", 4, 32767, "°C");
//    public static final IntegerKey TYPE = new IntegerKey("Type", "TYP", 4, 0, "");
    public static final DoubleKey VOLTAGE_DC = new DoubleKey("Voltage DC", "UDC", 4, 0, 0.1, "V");
    public static final DoubleKey VOLTAGE_PHASE_1 = new DoubleKey("Voltage phase 1", "UL1", 4, 0, 0.1, "V");
    public static final DoubleKey VOLTAGE_PHASE_2 = new DoubleKey("Voltage phase 2", "UL2", 4, 0, 0.1, "V");
    public static final DoubleKey VOLTAGE_PHASE_3 = new DoubleKey("Voltage phase 3", "UL3", 4, 0, 0.1, "V");
    public static final IntegerKey START_UPS = new IntegerKey("Start ups", "CAC", 8, 0, "");
    public static final IntegerKey ERROR_1_NUMBER = new IntegerKey("Error 1 number", "E11", 8, 0, "");
    public static final IntegerKey ERROR_1_DAY = new IntegerKey("Error 1 day", "E1D", 4, 0, "d");
    public static final IntegerKey ERROR_1_HOUR = new IntegerKey("Error 1 hour", "E1h", 4, 0, "h");
    public static final IntegerKey ERROR_1_MONTH = new IntegerKey("Error 1 month", "E1M", 4, 0, "m");
    public static final IntegerKey ERROR_1_MINUTE = new IntegerKey("Error 1 minute", "E1m", 4, 0, "min");
    public static final IntegerKey ERROR_2_NUMBER = new IntegerKey("Error 2 number", "E21", 8, 0, "");
    public static final IntegerKey ERROR_2_DAY = new IntegerKey("Error 2 day", "E2D", 4, 0, "d");
    public static final IntegerKey ERROR_2_HOUR = new IntegerKey("Error 2 hour", "E2h", 4, 0, "h");
    public static final IntegerKey ERROR_2_MONTH = new IntegerKey("Error 2 month", "E2M", 4, 0, "m");
    public static final IntegerKey ERROR_2_MINUTE = new IntegerKey("Error 2 minute", "E2m", 4, 0, "min");
    public static final IntegerKey ERROR_3_NUMBER = new IntegerKey("Error 3 number", "E31", 8, 0, "");
    public static final IntegerKey ERROR_3_DAY = new IntegerKey("Error 3 day", "E3D", 4, 0, "d");
    public static final IntegerKey ERROR_3_HOUR = new IntegerKey("Error 3 hour", "E3h", 4, 0, "h");
    public static final IntegerKey ERROR_3_MONTH = new IntegerKey("Error 3 month", "E3M", 4, 0, "m");
    public static final IntegerKey ERROR_3_MINUTE = new IntegerKey("Error 3 minute", "E3m", 4, 0, "min");
    public static final DoubleKey ENERGY_LAST_DAY = new DoubleKey("Energy last day", "KLD", 8, 0, 0.1, "kWh");
    public static final IntegerKey ENERGY_LAST_MONTH = new IntegerKey("Energy last month", "KLM", 8, 0, "kWh");
    public static final IntegerKey ENERGY_LAST_YEAR = new IntegerKey("Energy last year", "KLY", 8, 0, "kWh");
    public static final IntegerKey LANGUAGE = new IntegerKey("Language", "LAN", 8, 0, "");
    public static final DoubleKey DC_INPUT = new DoubleKey("DC input", "PDC", 8, 0, 0.5, "W");
    public static final DoubleKey GENERATED_FREQUENCY = new DoubleKey("Generated frequency", "TNF", 4, 0, 0.1, "Hz");
    public static final DoubleKey STRING_1_VOLTAGE = new DoubleKey("String 1 voltage", "UD01", 4, 0, 0.1, "V");
    public static final DoubleKey STRING_1_CURRENT = new DoubleKey("String 1 current", "ID01", 4, 0, 0.01, "A");
    public static final DoubleKey STRING_2_VOLTAGE = new DoubleKey("String 2 voltage", "UD02", 4, 0, 0.1, "V");
    public static final DoubleKey STRING_2_CURRENT = new DoubleKey("String 2 current", "ID02", 4, 0, 0.01, "A");
    public static final DoubleKey STRING_3_VOLTAGE = new DoubleKey("String 3 voltage", "UD03", 4, 0, 0.1, "V");
    public static final DoubleKey STRING_3_CURRENT = new DoubleKey("String 3 current", "ID03", 4, 0, 0.01, "A");
//    public static final IntegerKey SYSTEM_ALARMS = new IntegerKey("System alarms", "SAL", 4, 0, "");
//    public static final IntegerKey SYSTEM_STATUS = new IntegerKey("System status", "SYS", 4, 0, "");
    public static final IntegerKey ERROR_CODE_1 = new IntegerKey("Error code 1", "EC01", 4, 0, "");
    public static final IntegerKey ERROR_CODE_2 = new IntegerKey("Error code 2", "EC02", 4, 0, "");
    public static final IntegerKey ERROR_CODE_3 = new IntegerKey("Error code 3", "EC03", 4, 0, "");
    public static final IntegerKey ERROR_CODE_4 = new IntegerKey("Error code 4", "EC04", 4, 0, "");
    public static final IntegerKey ERROR_CODE_5 = new IntegerKey("Error code 5", "EC05", 4, 0, "");
    public static final IntegerKey ERROR_CODE_6 = new IntegerKey("Error code 6", "EC06", 4, 0, "");
    public static final IntegerKey ERROR_CODE_7 = new IntegerKey("Error code 7", "EC07", 4, 0, "");
    public static final IntegerKey ERROR_CODE_8 = new IntegerKey("Error code 8", "EC08", 4, 0, "");
    public static final IntegerKey BUILD_NUMBER = new IntegerKey("Build number", "BDN", 4, 0, "");
    
    public static final LookupKey TYPE = new LookupKey("Type", "TYP", 4, new DeviceLookup());
    public static final LookupKey SYSTEM_STATUS = new LookupKey("System status", "SYS", 4, new StatusLookup());
    public static final LookupKey SYSTEM_ALARMS = new LookupKey("System alarms", "SAL", 4, new AlarmLookup());
}
