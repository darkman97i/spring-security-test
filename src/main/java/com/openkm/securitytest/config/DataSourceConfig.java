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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.securitytest.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import  com.openkm.securitytest.util.NetworkUtils;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is required because in the case of the Docker startup services the database maybe is not up while openkm
 * is trying to connect for this reason it is applied a delayed logic waiting for the availability of the database connection
 */
@Configuration
@Profile("checkDatabaseConnection")
public class DataSourceConfig {
	private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);
	private static final Pattern hostAndPortPattern = Pattern.compile("[.\\w]+:\\d+");
	private static final int WAIT_TIME = 5000;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.url}")
	private String url;

	@Bean(name = "dataSource")
	@Primary
	public DataSource dataSource() {
		checkConnection();

		return DataSourceBuilder.create()
				.type(HikariDataSource.class)
				.driverClassName(driverClassName)
				.username(username)
				.password(password)
				.url(url)
				.build();
	}

	/**
	 * Check database connection
	 */
	private void checkConnection() {
		try {
			HostAndPort hap = getHostAndPort(url);
			boolean isReady;
			int tries = 0;

			do {
				log.info("Trying to connect to {}:{}", hap.host, hap.port);
				isReady = NetworkUtils.isPortOpen(hap.host, hap.port);

				if (!isReady) {
					log.warn("Database connection still not ready...");
					Thread.sleep(WAIT_TIME);
				}
			} while (!isReady && tries++ < 10);

			if (isReady) {
				log.info("*** Database connection ready after {} tries ({} seconds) ***", tries, tries * WAIT_TIME / 1000);
			} else {
				log.error("*** Unable to connect to database after {} tries ({} seconds) ***", tries, tries * WAIT_TIME / 1000);
			}
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	public HostAndPort getHostAndPort(String url) {
		Matcher matcher = hostAndPortPattern.matcher(url);

		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			if (start >= 0 && end >= 0) {
				String hostAndPort = url.substring(start, end);
				String[] data = hostAndPort.split(":");
				return new HostAndPort(data[0], Integer.parseInt(data[1]));
			}
		}

		throw new IllegalArgumentException("Couldn't find pattern '" + hostAndPortPattern.pattern() + "' in '" + url + "'");
	}

	private static class HostAndPort {
		public String host;
		public int port;

		public HostAndPort(String host, int port) {
			this.host = host;
			this.port = port;
		}
	}
}
