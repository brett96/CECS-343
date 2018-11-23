package Model;

public class Appointment
{
	private int id;
	private String name;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endYear;
	private int endMonth;
	private int endDay;
	private int startTime;
	private int endTime;
	private int aID;
	
	public Appointment(int id, String name, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay, int startTime, int endTime, int aID)
	{
		this.id = id;
		this.name = name;
		this.startYear = startYear;
		this.startMonth = startMonth;
		this.startDay = startDay;
		this.endYear = endYear;
		this.endMonth = endMonth;
		this.endDay = endDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.aID = aID;
	}
	
	public int getID()
	{
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aID;
		result = prime * result + endDay;
		result = prime * result + endMonth;
		result = prime * result + endTime;
		result = prime * result + endYear;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + startDay;
		result = prime * result + startMonth;
		result = prime * result + startTime;
		result = prime * result + startYear;
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
		Appointment other = (Appointment) obj;
		if (aID != other.aID)
			return false;
		if (endDay != other.endDay)
			return false;
		if (endMonth != other.endMonth)
			return false;
		if (endTime != other.endTime)
			return false;
		if (endYear != other.endYear)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDay != other.startDay)
			return false;
		if (startMonth != other.startMonth)
			return false;
		if (startTime != other.startTime)
			return false;
		if (startYear != other.startYear)
			return false;
		return true;
	}

	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getAID()
	{
		return aID;
	}
	
	public void setAID(int aID)
	{
		this.aID = aID;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	
	public void setStartYear(int year)
	{
		startYear = year;
	}
	
	
	public void setStartMonth(int month)
	{
		startMonth = month;
	}
	
	
	public void setStartDay(int day)
	{
		startDay = day;
	}
	
	public int getStartTime()
	{
		return startTime;
	}
	
	public int getStartDay()
	{
		return startDay;
	}
	
	public int getStartMonth()
	{
		return startMonth;
	}
	
	public int getStartYear()
	{
		return startYear;
	}
	
	
	public void setStart(int start)
	{
		startTime = start;
	}
	
	public int getEndYear()
	{
		return endYear;
	}
	
	public void setEndYear(int year)
	{
		endYear = year;
	}
	
	public int getEndMonth()
	{
		return endMonth;
	}
	
	public void setEndMonth(int month)
	{
		endMonth = month;
	}
	
	public int getEndDay()
	{
		return endDay;
	}
	
	public void setEndDay(int day)
	{
		endDay = day;
	}
	
	public int getEndTime()
	{
		return endTime;
	}
	
	public void setEndTime(int end)
	{
		endTime = end;
	}
	
	@Override
	public String toString() {
		return "Appointment [id=" + id + ", name=" + name + ", startYear=" + startYear + ", startMonth=" + startMonth
				+ ", startDay=" + startDay + ", endYear=" + endYear + ", endMonth=" + endMonth + ", endDay=" + endDay
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", aID=" + aID + "]\n";
	}
	
}
