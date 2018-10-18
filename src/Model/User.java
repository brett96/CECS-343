package Model;

import java.time.LocalDate;

public class User 
{
	private String name;
	private String email;
	private int appointmentsID;
	private LocalDate birthday;
	
	/**
	 * Default User constructor
	 * TODO:  Add birthday, username separate from email
	 */
	public User()
	{
		appointmentsID = 0;
		name = null;
		email = null;
		birthday = LocalDate.of(2000, 01, 01);
	}
	
	/**
	 * Constructor for User object
	 * @param id
	 * @param name
	 * @param email
	 */
	public User(String name, String email, int id, int year, int month, int day)
	{
		super();
		this.name = name;
		this.email = email;
		this.appointmentsID = id;
		this.birthday = LocalDate.of(year, month, day);
	}
	

	/**
	 * Returns User id
	 * @return
	 */
	public int getId() 
	{
		return appointmentsID;
	}

	/**
	 * Sets user id
	 * @param id
	 */
	public void setID(int id) {
		appointmentsID = id;
	}
	
	public LocalDate getBirthday()
	{
		return birthday;
	}

	/**
	 * Returns user name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets user name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns user email
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets user email
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + appointmentsID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (appointmentsID != other.appointmentsID)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [appointmentsID=" + appointmentsID + ", Name=" + name + ", Email=" + email + "]";
	}
}
