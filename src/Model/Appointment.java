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
	
	public int getStartYear()
	{
		return startYear;
	}
	
	public void setStartYear(int year)
	{
		startYear = year;
	}
	
	public int getStartMonth()
	{
		return startMonth;
	}
	
	public void setStartMonth(int month)
	{
		startMonth = month;
	}
	
	public int getStartDay()
	{
		return startDay;
	}
	
	public void setStartDay(int day)
	{
		startDay = day;
	}
	
	public int getStartTime()
	{
		return startTime;
	}
	
<<<<<<< HEAD
	public void setStartTime(int start)
=======
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
>>>>>>> 652d245590150ae8a34084764375c2e601232d26
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
