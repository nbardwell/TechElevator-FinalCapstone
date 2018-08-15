package com.techelevator.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class JDBCHouseDAO implements HouseDAO {

	private JdbcTemplate jdbcTemplate;
	
	
	
	@Autowired
	public JDBCHouseDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	@Override
	public void createHouse(String address, String resident, String notes, String phone_number, String status) {
		
		jdbcTemplate.update("INSERT INTO house(address, resident, notes, phone_number, status) VALUES (?, ?, ?, ?, ?)",
				address, resident, notes, phone_number, status);
	}
	
	@Override
	public int createHouseByCsv(MultipartFile file) 
	{

		try
		{
			BufferedReader reader = new BufferedReader((Reader) file);
			String currentLine = reader.readLine();
			while(currentLine!=null) 
			{
				String [] houseFields = currentLine.split("|");
				jdbcTemplate.update("INSERT INTO house(address, resident, phone_number, status, notes) VALUES (?, ?, ?, ?, ?)",
						houseFields[0], houseFields[1], houseFields[2], houseFields[3], houseFields[4]);
			
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("There was an error in the csv import method");
			
			return 1;
			
		}
		
		return 0;
	}

	@Override
	public List<House> getHouseByTeam(long teamId) {
		
		return null;
	}
	
	

}
