package com.common.queue.kafka;

import java.io.Serializable;
import java.util.Date;

public class KafkaMessageManager implements Serializable{
	
	private String address;
	
	private Integer id;
	
	private Date date;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "KafkaMessageManager [address=" + address + ", id=" + id + ", date=" + date + "]";
	};
	
	

}
