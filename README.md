# solarmaxcom
solarmaxcom reads out data from your SolarMax device (in particular solar inverters) and optionally sends it via MQTT. It uses the serial port interface and implements the protocol described in https://www.solarmax.com/Downloads/MaxComm_Protocol_Description_EN.pdf .

## Getting started
Your SolarMax device should be connected to your computer with a serial cable. A Raspberry Pi with a cheap CH340C USB-to-serial adapter works fine.

The available data depends on the SolarMax device. If you do not specify which data keys to read out, solarmaxcom tries all keys it knows about. For a Solarmax 2500SP inverter, this is the result:

```
pi@raspi:~ $ java -jar solarmaxcom.jar /dev/ttyUSB2
13:55:49.217 [main] INFO solarmaxcom.SolarMaxCom - Testing all known data keys...
13:55:49.280 [main] INFO solarmaxcom.SolarMaxCom - Requesting keys...
13:55:49.424 [main] WARN solarmaxcom.SolarMaxCom - Could not parse packet: solarmaxcom.protocol.Packet$ParseException: Invalid message: Key '' unknown.
13:55:49.614 [main] WARN solarmaxcom.SolarMaxCom - Could not parse packet: solarmaxcom.protocol.Packet$ParseException: Invalid message: Key '' unknown.
[...]
13:55:52.153 [main] INFO solarmaxcom.SolarMaxCom - PAC AC output: 138.0 W
13:55:52.154 [main] INFO solarmaxcom.SolarMaxCom - IDC Current DC: 0.51 A
13:55:52.155 [main] INFO solarmaxcom.SolarMaxCom - KMT Energy month: 3 kWh
13:55:52.155 [main] INFO solarmaxcom.SolarMaxCom - SWV Software version: 11200 
13:55:52.157 [main] INFO solarmaxcom.SolarMaxCom - TIME Time: 13:55:31 
13:55:52.158 [main] INFO solarmaxcom.SolarMaxCom - KYR Energy year: 10 kWh
13:55:52.158 [main] INFO solarmaxcom.SolarMaxCom - PRL Relative output: 5 %
13:55:52.159 [main] INFO solarmaxcom.SolarMaxCom - UL1 Voltage phase 1: 227.2 V
13:55:52.160 [main] INFO solarmaxcom.SolarMaxCom - IL1 Current phase 1: 0.85 A
13:55:52.160 [main] INFO solarmaxcom.SolarMaxCom - PDC DC input: 137.5 W
13:55:52.161 [main] INFO solarmaxcom.SolarMaxCom - UDC Voltage DC: 276.7 V
13:55:52.162 [main] INFO solarmaxcom.SolarMaxCom - TYP Type: SolarMax 2500SP 
13:55:52.162 [main] INFO solarmaxcom.SolarMaxCom - UD02 String 2 voltage: 0.0 V
13:55:52.163 [main] INFO solarmaxcom.SolarMaxCom - ID02 String 2 current: 0.00 A
13:55:52.163 [main] INFO solarmaxcom.SolarMaxCom - ADR Network address: 1 
13:55:52.163 [main] INFO solarmaxcom.SolarMaxCom - TKK Temperature power unit 1: 26 °C
13:55:52.165 [main] INFO solarmaxcom.SolarMaxCom - DATE Date: 2021-12-04 
13:55:52.166 [main] INFO solarmaxcom.SolarMaxCom - KHR Operating hours: 83 
13:55:52.167 [main] INFO solarmaxcom.SolarMaxCom - KDY Energy day: 0.7 kWh
13:55:52.167 [main] INFO solarmaxcom.SolarMaxCom - KT0 Energy total: 10 kWh
13:55:52.167 [main] INFO solarmaxcom.SolarMaxCom - ID01 String 1 current: 0.51 A
13:55:52.168 [main] INFO solarmaxcom.SolarMaxCom - UD01 String 1 voltage: 276.7 V
13:55:52.169 [main] INFO solarmaxcom.SolarMaxCom - PIN Installed capacity: 0.0 W
13:55:52.169 [main] INFO solarmaxcom.SolarMaxCom - Closing serial interface...
```

Now you can choose which data keys (PAC, IDC, KMT, ...) to read out. In order to read out AC output power and energy produced today / in total, use PAC, KDY and KT0. You can also set a MQTT broker to send this data to (localhost in this case) and repeat the readout every 5 seconds:

```
pi@raspi:~ $ java -jar solarmaxcom.jar -m localhost -r 5000 /dev/ttyUSB2 PAC,KT0,KDY
13:57:07.422 [main] INFO solarmaxcom.SolarMaxCom - Requesting keys; press Enter to break request loop.
13:57:07.715 [main] INFO solarmaxcom.SolarMaxCom - PAC AC output: 135.0 W
13:57:07.753 [main] INFO solarmaxcom.SolarMaxCom - KT0 Energy total: 10 kWh
13:57:07.761 [main] INFO solarmaxcom.SolarMaxCom - KDY Energy day: 0.7 kWh
13:57:12.764 [main] INFO solarmaxcom.SolarMaxCom - Requesting keys; press Enter to break request loop.
13:57:12.873 [main] INFO solarmaxcom.SolarMaxCom - PAC AC output: 133.0 W
13:57:12.877 [main] INFO solarmaxcom.SolarMaxCom - KT0 Energy total: 10 kWh
13:57:12.883 [main] INFO solarmaxcom.SolarMaxCom - KDY Energy day: 0.7 kWh
[...]
```

## Ethernet connection
Some SolarMax devices have an Ethernet port. This software currently cannot connect to these devices. As they use the same protocol, it should be easy to implement support for Ethernet-connected devices.

## License
This project is licensed under the GNU General Public License, version 3 or later. For details see [LICENSE.txt](./LICENSE.txt).
