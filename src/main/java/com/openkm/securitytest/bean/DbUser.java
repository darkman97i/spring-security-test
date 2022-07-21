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

package com.openkm.securitytest.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkm.securitytest.util.CustomToStringStyle;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@Entity
@Table(name = "OKM_USER")
public class DbUser implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "USR_ID", length = 64)
	private String id;

	@Column(name = "USR_NAME", length = 64)
	private String name = "";

	@Column(name = "USR_PASSWORD", length = 64)
	private String password = "";

	@Column(name = "USR_EMAIL", length = 64)
	private String email = "";

	@Column(name = "USR_ACTIVE")
	@Type(type = "true_false")
	private Boolean active = false;

	@Transient
	protected List<String> roles = new ArrayList<>();

	@JsonIgnore
	@Column(name = "USR_PASSWORD_MD4", length = 64)
	private String passwordMd4 = "";

	@JsonIgnore
	@Column(name = "USR_SECRET", length = 64)
	private String secret;

	@JsonIgnore
	@Column(name = "USR_PASSWORD_CHANGED")
	private Calendar passwordChanged;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "OKM_USER_ROLE",
		joinColumns = @JoinColumn(name = "UR_USER"),
		foreignKey = @ForeignKey(name = "FK_USR_ROL_USER_ID"),
		inverseJoinColumns = @JoinColumn(name = "UR_ROLE"),
		inverseForeignKey = @ForeignKey(name = "FK_USR_ROL_ROLE_ID"),
		indexes = @Index(name = "IDX_USR_ROL", columnList = "UR_ROLE"))
	private Set<DbRole> dbRoles = new HashSet<>();

	public DbUser() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (this.getClass() == obj.getClass()) {
			DbUser other = (DbUser) obj;

			if (this.getId().equals(other.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getActive() ? 1231 : 1237);
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, CustomToStringStyle.SHORT_PREFIX_STYLE)
				.append("id", getId()).append("name", getName())
				.append("password", getPassword()).append("passwordMd4", passwordMd4)
				.append("secret", secret).append("email", getEmail())
				.append("active", getActive()).append("passwordChanged", passwordChanged)
				.append("dbRoles", dbRoles).toString();
	}
}
