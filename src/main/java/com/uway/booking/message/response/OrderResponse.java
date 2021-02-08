package com.uway.booking.message.response;

public class OrderResponse {
	private double amount;
	private String currency;
	private Long receipt;
	private String key;
	private String name;
	private String description;
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Long getReceipt() {
		return receipt;
	}
	public void setReceipt(Long receipt) {
		this.receipt = receipt;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}