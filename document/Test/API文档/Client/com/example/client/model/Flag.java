package com.example.client.model;

/**
 * <p>This class is the flag which value will be set
 * true if the request is made by viewer, and be set 
 * false when request is handled by models.</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class Flag
{
	boolean value;
	
	/**
	 * constructor
	 * */
	public Flag(boolean value)
	{
		this.value = value;
	}
	
	/**
	 * get value
	 * */
	public boolean getValue()
	{
		return value;
	}
	
	/**
	 * set value
	 * */
	public void setValue(boolean value)
	{
		this.value = value;
	}
}
