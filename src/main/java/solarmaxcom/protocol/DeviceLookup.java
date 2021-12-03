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
 * Translates device code into device name. The codes are listed in
 * https://www.solarmax.com/Downloads/MaxComm_Protocol_Description_EN.pdf .
 */
public final class DeviceLookup extends SimpleLookupTable {
    public DeviceLookup() {
        add(20812, "SolarMax 1440TS-SV MT");
        add(20809, "SolarMax 1080TS-SV MT");
        add(20806, "SolarMax 720TS-SV MT");
        add(20803, "SolarMax 360TS-SV MT");
        add(20712, "SolarMax 1440TS-SV ST");
        add(20709, "SolarMax 1080TS-SV ST");
        add(20706, "SolarMax 720TS-SV ST");
        add(20703, "SolarMax 360TS-SV ST");
        add(20700, "SolarMax 360TS-SV");
        add(20255, "SolarMax 20HT2");
        add(20254, "SolarMax 18MT3 A");
        add(20252, "SolarMax 15MT3 A");
        add(20250, "SolarMax 12MT2 A");
        add(20240, "SolarMax 18MT3 SV");
        add(20215, "SolarMax 8MT2");
        add(20213, "SolarMax 15MT2");
        add(20211, "SolarMax 13MT2");
        add(20210, "SolarMax 10MT2");
        add(11025, "SolarMax 3600SP");
        add(11020, "SolarMax 3000SP");
        add(11015, "SolarMax 2500SP");
        add(11010, "SolarMax 2000SP");
        add(11005, "SolarMax 1500SP");
        add(11000, "SolarMax 1000SP");
        add(10300, "MaxCount");
        add(10210, "MaxMeteo plus2T");
        add(10200, "MaxMeteo");
        add(20653, "SolarMax 4TP");
        add(20652, "SolarMax 5TP2");
        add(20651, "SolarMax 6TP2");
        add(20650, "SolarMax 7TP2");
        add(20640, "SolarMax 5000P");
        add(20635, "SolarMax 4600P");
        add(20630, "SolarMax 4000P");
        add(20620, "SolarMax 3000P");
        add(20610, "SolarMax 2000P");
        add(20512, "SolarMax 1320TS-SV MT");
        add(20509, "SolarMax 990TS-SV MT");
        add(20506, "SolarMax 660TS-SV MT");
        add(20503, "SolarMax 330TS-SV MT");
        add(20412, "SolarMax 1320TS-SV ST");
        add(20409, "SolarMax 990TS-SV ST");
        add(20406, "SolarMax 660TS-SV ST");
        add(20403, "SolarMax 330TS-SV ST");
        add(20318, "SolarMax 300TS MT");
        add(20316, "SolarMax 300TS ST");
        add(20314, "SolarMax 100TS");
        add(20312, "SolarMax 80TS");
        add(20310, "SolarMax 50TS");
        add(20266, "SolarMax 32HT2");
        add(20262, "SolarMax 32HT4");
        add(20260, "SolarMax 30HT4");
        add(20258, "SolarMax 25HT4");
        add(20257, "SolarMax 25HT2");
        add(20256, "SolarMax 20HT4");
        add(20208, "SolarMax 15MT3");
        add(20206, "SolarMax 13MT3");
        add(20202, "SolarMax 10MT");
        add(20100, "SolarMax 20S");
        add(20110, "SolarMax 35S");
        add(20040, "SolarMax 6000S");
        add(20030, "SolarMax 4200S");
        add(20020, "SolarMax 3000S");
        add(20010, "SolarMax 2000S");
        add(11120, "SolarMax 60SHT-S");
        add(11115, "SolarMax 50SHT-S");
        add(11110, "SolarMax 60SHT");
        add(11105, "SolarMax 50SHT");
        add(11100, "SolarMax 30SHT");
        add(11095, "SolarMax 28SHT");
        add(11090, "SolarMax 25SHT");
        add(11085, "SolarMax 22SHT");
        add(11080, "SolarMax 20SHT");
        add(11075, "SolarMax 17SHT");
        add(11070, "SolarMax 15SMT");
        add(11065, "SolarMax 13SMT");
        add(11060, "SolarMax 10SMT");
        add(11055, "SolarMax 8SMT");
        add(11050, "SolarMax 6SMT");
        add(11045, "SolarMax 6000SP");
        add(11040, "SolarMax 5000SP");
        add(11035, "SolarMax 4600SP");
        add(11030, "SolarMax 4000SP");
        add(6010, "SolarMax 6000C");
        add(6000, "SolarMax 6000E");
        add(4200, "SolarMax 4200C");
        add(4010, "SolarMax 4000C");
        add(4001, "SolarMax 4000");
        add(4000, "SolarMax 4000E");
        add(3010, "SolarMax 3000C");
        add(3001, "SolarMax 3000E");
        add(3000, "SolarMax 3000");
        add(2010, "SolarMax 2000C");
        add(2001, "SolarMax 2000E");
        add(2000, "SolarMax 2000");
        add(330, "SolarMax 330C-SV");
        add(300, "SolarMax 300C");
        add(126, "SolarMax 125");
        add(101, "SolarMax 100");
        add(100, "SolarMax 100C");
        add(80, "SolarMax 80C");
        add(61, "SolarMax 60");
        add(50, "SolarMax 50C");
        add(46, "SolarMax 45");
        add(41, "SolarMax 40");
        add(35, "SolarMax 35C");
        add(31, "SolarMax 30");
        add(30, "SolarMax 30C");
        add(25, "SolarMax 25C");
        add(21, "SolarMax 20");
        add(20, "SolarMax 20C");
    }
}
