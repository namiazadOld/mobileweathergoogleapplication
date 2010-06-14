package com.sa.mwa;

public class CustomException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int CONNECTION_FAILED = 1;
	
	private int code;
	
	public CustomException(int code)
	{
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDefaultMessage()
	{
		switch (code)
		{
			case CONNECTION_FAILED:
			{
				return "Connection could not been established.";
			}
			default:
				return getMessage();
		}
	}

}
