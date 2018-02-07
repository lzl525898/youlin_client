package com.nfs.youlin.entity;

public class Person {  
    private String name;  
    private String pinYinName;  
    private int personindex = -1;
  
    public int getPersonindex() {
		return personindex;
	}

	public void setPersonindex(int personindex) {
		this.personindex = personindex;
	}

	public Person(String name) {  
        super();  
        this.name = name;  
    }  
  
	public Person(String name,int index) {  
        super();  
        this.name = name;  
        this.personindex = index;
    } 
	
    public Person(String name, String pinYinName) {  
        super();  
        this.name = name;  
        this.pinYinName = pinYinName;  
    }  
    
    public Person(String name, String pinYinName, int index) {  
        super();  
        this.name = name;  
        this.pinYinName = pinYinName;  
        this.personindex = index;
    } 
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getPinYinName() {  
        return pinYinName;  
    }  
  
    public void setPinYinName(String pinYinName) {  
        this.pinYinName = pinYinName;  
    }  
}  