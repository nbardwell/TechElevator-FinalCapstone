package com.techelevator.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl {
	  


		@Autowired
	    public JavaMailSender emailSender;
	 
	    public void sendSimpleMessage(String to, String adminName, String username, String password) {
	    
	        SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setTo(to); 
	        message.setSubject("Youve been registered"); 
	        message.setText("You have been signed up by " + adminName + "your login credentials are: " + '\n' + '\n' + "username: " + username + '\n' + "password: " + password);
	        emailSender.send(message);
	        
	}
}