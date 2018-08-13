package com.techelevator.model;

public interface UserDAO {

	public void saveUser(String firstName, String lastName, String userName, String password, String email, String role);

	public boolean searchForUsernameAndPassword(String userName, String password);

	public void updatePassword(String userName, String password);

	public Object getUserByUserName(String userName);

}
