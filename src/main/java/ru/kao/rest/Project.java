package ru.kao.rest;

import java.util.Calendar;
import java.util.Date;

public class Project {

	private Double cost;
	private Long ownerId;
	private Long forks;
	private Long watchers;
	private Long openIssues;
	private Long size;
	private String language;
	private Date updatedAt;
	private String fullName;

	public Double getCost() {
		return cost;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void seOwnertId(Long ownerId) {
		this.ownerId = ownerId;
		calcCost();
	}

	public Long getForks() {
		return forks;
	}

	public void setForks(Long forks) {
		this.forks = forks;
		calcCost();
	}

	public Long getWatchers() {
		return watchers;
	}

	public void setWatchers(Long watchers) {
		this.watchers = watchers;
		calcCost();
	}

	public Long getOpenIssues() {
		return openIssues;
	}

	public void setOpenIssues(Long openIssues) {
		this.openIssues = openIssues;
		calcCost();
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
		calcCost();
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
		calcCost();
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		calcCost();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public Project(Long ownerId, String fullName, Long forks, Long watchers, Long openIssues, Long size,
			String language, Date updatedAt) {
		this.ownerId = ownerId;
		this.forks = forks;
		this.watchers = watchers;
		this.openIssues = openIssues;
		this.size = size;
		this.language = language;
		this.updatedAt = updatedAt;
		this.fullName = fullName;

		calcCost();
	}

	@Override
	public String toString() {
		return String.format("Project [Cost = %.2f, Name = '%s']", cost, fullName);
	}

	private boolean diffDate(Date updatedAt, Date currDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(updatedAt);
		cal2.setTime(currDate);
		cal2.add(Calendar.YEAR, -2);

		return cal2.after(cal1);
	}

	private Double calcCost() {
		cost = 0.0d;

		if (forks != null) {
			cost += forks * 3;
		}

		if (watchers != null) {
			cost += watchers;
		}

		if (openIssues != null) {
			cost -= openIssues;
		}

		if ((ownerId.longValue()&1L) != 0) {
			cost -= 30;
		}

		if (size != null) {
			cost += (size / 1024) * 0.1;
		}

		if (language != null && "JAVA".equals(language.toUpperCase())) {
			cost += 5;
		}

		if (updatedAt != null && diffDate(updatedAt, new Date())) {
			cost -= 20;
		}

		return cost;
	}
}
