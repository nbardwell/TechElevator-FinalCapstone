package com.techelevator.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techelevator.model.House;
import com.techelevator.model.HouseDAO;
import com.techelevator.model.TeamDAO;
import com.techelevator.model.User;

@Controller
public class HouseController {
	
	private HouseDAO houseDAO;
	private TeamDAO teamDao;
	
	@Autowired
	public HouseController(HouseDAO houseDAO, TeamDAO teamDao) {
		this.houseDAO = houseDAO;
		this.teamDao = teamDao;
	}
	
	
	@RequestMapping(path = "/addHouses",method = RequestMethod.GET)
	public String displayAddHousePage(HttpSession session) {
		if(session.getAttribute("currentUser") == null) {
			return "redirect:/login?destination=/addHouses";
		} else if (!((User) session.getAttribute("currentUser")).getRole().equals("Admin")) {
			return "/notAuthorized";
		}
		return "/addHouses";
	}
	
	@RequestMapping(path = "/addHouses",method = RequestMethod.POST)
	public String addNewHouses(@Valid @ModelAttribute House house,@RequestParam String creatorId,
									  BindingResult result,
									  RedirectAttributes flash) {
		if(result.hasErrors()) {
			flash.addFlashAttribute("house",house);
			flash.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX+ "house", result);
            flash.addFlashAttribute("errorMessage", "Error creating new House.");
			return "redirect:/addHouses";
		} 
		
			houseDAO.createHouse(house.getAddress(), house.getResident(),  house.getPhoneNumber(), house.getStatus(), creatorId);
			flash.addFlashAttribute("message", "New House " + house.getAddress() + " Created Successfully!");
			return "redirect:/addHouses";
		
		
	}
	
	@RequestMapping(path = "/textArea",method = RequestMethod.POST)
	public String addNewHousesByCsv(@Valid @RequestParam String textArea, RedirectAttributes flash, HttpSession session) {
		int success = houseDAO.createHouseMultiple(textArea, ((User)session.getAttribute("currentUser")).getUserName());	
		if(success==0) {
			flash.addFlashAttribute("message", "Houses Created Successfully!");
		}else {
			flash.addFlashAttribute("message", "Unable to create new houses");
		}
		
		return "redirect:/addHouses";
	}
	
	@RequestMapping(path ="/viewHouses", method = RequestMethod.GET)
	public String viewHouse(ModelMap modelHolder, HttpSession session) {
		long id  = teamDao.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
		modelHolder.put("teamMembers",teamDao.getAllTeamMembers(id));
		modelHolder.put("houses", houseDAO.viewHouses(((User)session.getAttribute("currentUser")).getUserName()));
		return "/viewHouses";
	}
	
	@RequestMapping(path = "/updateAssignment", method = RequestMethod.POST)
	public String updateAssignment(@RequestParam long houseId, @RequestParam String assignmentId, RedirectAttributes flash) {
		if(assignmentId.equals("")) {
			assignmentId = null;
		}
		int success = houseDAO.updateAssignment(houseId, assignmentId);
		if(success==0) {
			flash.addFlashAttribute("message", "Houses Created Successfully!");
		}else {
			flash.addFlashAttribute("message", "Unable to create new houses");
		}
		
		return "redirect:/viewHouses";
	}
	
}
