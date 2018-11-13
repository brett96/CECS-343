package Model;

import java.time.LocalDateTime;

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
	
	public int getStart()
	{
		return startTime;
	}
	
	public void setStart(int start)
	{
		startTime = start;
	}
	
	public int getEnd()
	{
		return endTime;
	}
	
	public void setEnd(int end)
	{
		endTime = end;
	}
	
	
}
