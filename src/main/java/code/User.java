package code;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity @Table(name = "users")
public class User {
	
	@Id
	private Long id;
	
	@Column(length = 50, nullable = false, unique = true)
	@NotBlank(message = "E-mail address must not be empty")
	@Email(message = "User must have valid email address")
	private String email;
	
	@Column(length = 20, nullable = false)
	private String firstName;
	
	@Column(length = 20, nullable = false)
	private String lastName;
	
	@Column(length = 10, nullable = false)
	private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public User email(String email) {
		this.email = email;
		return this;
	}
	
	public User firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public User lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public User password(String password) {
		this.password = password;
		return this;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}
	
	public User id(Long id) {
		this.id = id;
		return this;
	}
}
