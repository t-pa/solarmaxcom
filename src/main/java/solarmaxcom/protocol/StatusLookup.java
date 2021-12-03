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
 * Status codes from https://github.com/ardexa/solarmax-inverters/blob/master/solarmax_ardexa.py.
 */
public final class StatusLookup extends SimpleLookupTable {
    public StatusLookup() {
        add(20001, "Running (20001)");
        add(20002, "Irradiance too low (20002)");
        add(20003, "Startup (20003)");
        add(20004, "MPP operation (20004)");
        add(20006, "Maximum power (20006)");
        add(20007, "Temperature limitation (20007)");
        add(20008, "Mains operation (20008)");
        add(20009, "Idc limitation (20009)");
        add(20010, "Iac limitation (20010)");
        add(20011, "Test mode (20011)");
        add(20012, "Remote controlled (20012)");
        add(20013, "Restart delay (20013)");
        add(20014, "External limitation (20014)");
        add(20015, "Frequency limitation (20015)");
        add(20016, "Restart limitation (20016)");
        add(20017, "Booting (20017)");
        add(20018, "Insufficient boot power (20018)");
        add(20019, "Insufficient power (20019)");
        add(20021, "Uninitialized (20021)");
        add(20022, "Disabled (20022)");
        add(20023, "Idle (20023)");
        add(20024, "Powerunit not ready (20024)");
        add(20050, "Program firmware (20050)");
        add(20101, "Device error 101 (20101)");
        add(20102, "Device error 102 (20102)");
        add(20103, "Device error 103 (20103)");
        add(20104, "Device error 104 (20104)");
        add(20105, "Insulation fault DC (20105)");
        add(20106, "Insulation fault DC (20106)");
        add(20107, "Device error 107 (20107)");
        add(20108, "Device error 108 (20108)");
        add(20109, "Vdc too high (20109)");
        add(20110, "Device error 110 (20110)");
        add(20111, "Device error 111 (20111)");
        add(20112, "Device error 112 (20112)");
        add(20113, "Device error 113 (20113)");
        add(20114, "Ierr too high (20114)");
        add(20115, "No mains (20115)");
        add(20116, "Frequency too high (20116)");
        add(20117, "Frequency too low (20117)");
        add(20118, "Mains error (20118)");
        add(20119, "Vac 10min too high (20119)");
        add(20120, "Device error 120 (20120)");
        add(20121, "Device error 121 (20121)");
        add(20122, "Vac too high (20122)");
        add(20123, "Vac too low (20123)");
        add(20124, "Device error 124 (20124)");
        add(20125, "Device error 125 (20125)");
        add(20126, "Error ext. input 1 (20126)");
        add(20127, "Fault ext. input 2 (20127)");
        add(20128, "Device error 128 (20128)");
        add(20129, "Incorr. rotation dir. (20129)");
        add(20130, "Device error 130 (20130)");
        add(20131, "Main switch off (20131)");
        add(20132, "Device error 132 (20132)");
        add(20133, "Device error 133 (20133)");
        add(20134, "Device error 134 (20134)");
        add(20135, "Device error 135 (20135)");
        add(20136, "Device error 136 (20136)");
        add(20137, "Device error 137 (20137)");
        add(20138, "Device error 138 (20138)");
        add(20139, "Device error 139 (20139)");
        add(20140, "Device error 140 (20140)");
        add(20141, "Device error 141 (20141)");
        add(20142, "Device error 142 (20142)");
        add(20143, "Device error 143 (20143)");
        add(20144, "Device error 144 (20144)");
        add(20145, "df/dt too high (20145)");
        add(20146, "Device error 146 (20146)");
        add(20147, "Device error 147 (20147)");
        add(20148, "Device error 148 (20148)");
        add(20150, "Ierr step too high (20150)");
        add(20151, "Ierr step too high (20151)");
        add(20153, "Device error 153 (20153)");
        add(20154, "Shutdown 1 (20154)");
        add(20155, "Shutdown 2 (20155)");
        add(20156, "Device error 156 (20156)");
        add(20157, "Insulation fault DC (20157)");
        add(20158, "Device error 158 (20158)");
        add(20159, "Device error 159 (20159)");
        add(20160, "Device error 160 (20160)");
        add(20161, "Device error 161 (20161)");
        add(20163, "Device error 163 (20163)");
        add(20164, "Ierr too high (20164)");
        add(20165, "No mains (20165)");
        add(20166, "Frequency too high (20166)");
        add(20167, "Frequency too low (20167)");
        add(20168, "Mains error (20168)");
        add(20169, "Vac 10min too high (20169)");
        add(20170, "Device error 170 (20170)");
        add(20171, "Device error 171 (20171)");
        add(20172, "Vac too high (20172)");
        add(20173, "Vac too low (20173)");
        add(20174, "Device error 174 (20174)");
        add(20175, "Device error 175 (20175)");
        add(20176, "Error DC polarity (20176)");
        add(20177, "Device error 177 (20177)");
        add(20178, "Device error 178 (20178)");
        add(20179, "Device error 179 (20179)");
        add(20180, "Vdc too low (20180)");
        add(20181, "Blocked external (20181)");
        add(20185, "Device error 185 (20185)");
        add(20186, "Device error 186 (20186)");
        add(20187, "Device error 187 (20187)");
        add(20188, "Device error 188 (20188)");
        add(20189, "L and N interchanged (20189)");
        add(20190, "Below-average yield (20190)");
        add(20191, "Limitation error (20191)");
        add(20198, "Device error 198 (20198)");
        add(20199, "Device error 199 (20199)");
        add(20999, "Device error 999 (20999)");
    }
}
