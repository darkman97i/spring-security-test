/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.securitytest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
	private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

	/**
	 * Get hostname
	 */
	public static String getHostname() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			return  "unknown";
		}
	}

	/**
	 * Get host IP
	 */
	public static InetAddress getIpAddress() throws SocketException, UnknownHostException {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress();
		}
	}

	/**
	 * Check port
	 */
	public static boolean isPortOpen(String ip, int port) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(ip, port), 100);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Scan net for open port
	 */
	public static List<String> scanForPort(int port) throws SocketException, UnknownHostException {
		InetAddress localhost = getIpAddress();
		byte[] ip = localhost.getAddress();
		List<String> ret = new ArrayList<>();

		for (int i = 1; i <= 254; i++) {
			try {
				ip[3] = (byte) i;
				InetAddress address = InetAddress.getByAddress(ip);

				if (address.isReachable(100)) {
					String hostIp = address.toString().substring(1);

					if (isPortOpen(hostIp, port)) {
						ret.add(hostIp);
					}
				}
			} catch (Exception e) {
				// None
			}
		}

		return ret;
	}

	/**
	 * Get MAC address
	 */
	public static String getMACAddress() throws SocketException {
		String mac = null;

		for (Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces(); eni.hasMoreElements(); ) {
			NetworkInterface iface = eni.nextElement();
			String name = iface.getName();

			// https://tuxdiary.com/2015/10/28/persistent-network-interface-names-ubuntu/
			if (name.startsWith("eth") || name.startsWith("eno") || name.startsWith("ens") || name.startsWith("enp")
					|| name.startsWith("enx") || name.startsWith("virbr") || name.startsWith("wlan")) {
				byte[] hardAddr = iface.getHardwareAddress();
				StringBuilder sb = new StringBuilder();

				if (hardAddr != null) {
					log.info("Detected network interface: {}", name);
					for (InetAddress inetAddr : Collections.list(iface.getInetAddresses())) {
						log.info("Interface address: {}", inetAddr);
					}

					for (int i = 0; i < hardAddr.length; i++) {
						sb.append(String.format("%02X%s", hardAddr[i], (i < hardAddr.length - 1) ? ":" : ""));
					}

					mac = sb.toString();
					log.info("MAC address: {}", mac);
					break;
				}
			} else {
				if (name.startsWith("venet")) {
					// Prevents problems in virtualized environments, because in Proxmox (OpenVZ) there is no MAC address
					// in virtual network interfaces. If possible generate a MAC based in hostid algorithm.
					mac = "00:OP:EN:00:VZ:00";
				} else if (name.startsWith("lo")) {
					// Ignore
					mac = "CA:FE:00:7F:01:00";
				} else {
					log.warn("Unknown network interface: {}", name);
				}

				try {
					InetAddress addr = InetAddress.getLocalHost();
					byte[] ipaddr = addr.getAddress();

					if (ipaddr.length == 4) {
						StringBuilder sb = new StringBuilder("CA:FE:");
						sb.append(String.format("%02X", ipaddr[1])).append(":");
						sb.append(String.format("%02X", ipaddr[0])).append(":");
						sb.append(String.format("%02X", ipaddr[3])).append(":");
						sb.append(String.format("%02X", ipaddr[2]));
						mac = sb.toString();
					} else {
						log.warn("Hostid for IPv6 addresses not implemented yet");
					}
				} catch (UnknownHostException e) {
					log.warn(e.getMessage(), e);
				}
			}
		}

		if (mac != null) {
			return mac;
		} else {
			throw new SocketException("Missing network interface");
		}
	}
}
