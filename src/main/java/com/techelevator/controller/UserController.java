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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techelevator.model.HouseDAO;
import com.techelevator.model.NoteDAO;
import com.techelevator.model.TeamDAO;
import com.techelevator.model.User;
import com.techelevator.model.UserDAO;

@Controller
public class UserController {

	private UserDAO userDAO;
	private TeamDAO teamDAO;
	private HouseDAO houseDao;
	private NoteDAO noteDao;

	@Autowired
	public UserController(UserDAO userDAO, TeamDAO teamDAO, HouseDAO houseDao, NoteDAO noteDao) {
		this.userDAO = userDAO;
		this.teamDAO = teamDAO;
		this.houseDao= houseDao;
		this.noteDao= noteDao;
	}

	@RequestMapping(path="/users/new", method=RequestMethod.GET)
	public String displayNewAdminForm(ModelMap modelHolder) {
		if( ! modelHolder.containsAttribute("user")) {
			modelHolder.addAttribute("user", new User());
		}
		return "newUser";
	}
	
	@RequestMapping(path="/users", method=RequestMethod.POST)
    public String createAdmin(@Valid @ModelAttribute User user, @RequestParam String teamName, BindingResult result, RedirectAttributes flash) {
        if(result.hasErrors()) {
            flash.addFlashAttribute("user", user);
            flash.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "user", result);
            flash.addFlashAttribute("errorMessage", "Error creating new Admin.");
            return "redirect:/users/new";
        }
        
       int success= userDAO.saveUser(user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), user.getEmail(), user.getRole() );
       if(success ==0) {
    	   flash.addFlashAttribute("message", "New Admin " + user.getFirstName() + " Created Successfully!");
    	   teamDAO.createNewTeam(teamName, user.getUserName());
       }else {
    	   flash.addFlashAttribute("message", "Invalid Registration, Please Try Again");
    	   return "redirect:/users/new";
       }
        
      
        
        return "redirect:/login";
    }
	
	@RequestMapping(path="/newSalesman", method=RequestMethod.GET)
	public String displayNewSalesmanForm(ModelMap modelHolder, RedirectAttributes flash, HttpSession session) {
		
		if (!((User) session.getAttribute("currentUser")).getRole().equals("Admin")) {
			return "/notAuthorized";
		}
		
		if( ! modelHolder.containsAttribute("user")) {
			modelHolder.addAttribute("user", new User());
		}
		if(flash.containsAttribute("message")) {
			
		}
		return "newSalesman";
	}
	
	@RequestMapping(path="/newSalesman", method=RequestMethod.POST)
    public String createNewSalesman(@Valid @ModelAttribute User user, BindingResult result, HttpSession session, RedirectAttributes flash) {
        if(result.hasErrors()) {
            flash.addFlashAttribute("user", user);
            flash.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "user", result);
            flash.addFlashAttribute("errorMessage", "Error creating new Salesman.");
            return "redirect:/newSalesman";
        }
        
        userDAO.saveUser(user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), user.getEmail(), user.getRole() );
        flash.addFlashAttribute("message", "New Salesman " + user.getFirstName() + " Created Successfully!");
        long team_id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
        teamDAO.addSalesmanToTeam(team_id, user.getUserName());
        return "redirect:/newSalesman";
    }
	
	
	
	@RequestMapping(path="/salesman", method=RequestMethod.GET)
	public String showSalesmanPage(HttpSession session, ModelMap modelHolder) {
		modelHolder.put("houses", houseDao.viewAssignedHouses(((User)session.getAttribute("currentUser")).getUserName()));
		return "/salesman";
	}
	
	@RequestMapping(path= {"/admin", "/viewTeam"}, method=RequestMethod.GET)
	public String showTeam(HttpSession session, ModelMap modelHolder) {
		long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
		modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
		return "/viewTeam";
	}
	
	@RequestMapping(path = "/houseDetail", method = RequestMethod.GET)
	public String showHouseDetail(ModelMap modelHolder, @RequestParam long houseId, HttpSession session) {
		String username = ((User) session.getAttribute("currentUser")).getUserName();
		String assignedTo = houseDao.getHouseById(houseId).getAssignmentId();
		if (!username.equals(assignedTo)) {
			return "/notAuthorized";
		}
		modelHolder.put("house", houseDao.getHouseById(houseId));
		return "/houseDetail";
	}
	
	@RequestMapping(path = "/salesData" , method = RequestMethod.GET)
	public String showSalesData(ModelMap modelHolder, HttpSession session, @RequestParam(required = false) String sort) {
		if(sort == null) {
			long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
	     	modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
	     	modelHolder.put("houses", houseDao.viewHouses(((User)session.getAttribute("currentUser")).getUserName()));
		}else if(sort.equals("userId")) {
			long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
	     	modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
			modelHolder.put("houses", houseDao.viewHousesSortedBySalesman(((User)session.getAttribute("currentUser")).getUserName()));
		}else if (sort.equals("resident")) {
			long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
	     	modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
	     	modelHolder.put("houses", houseDao.viewHousesSortedByResident(((User)session.getAttribute("currentUser")).getUserName()));
		}else if(sort.equals("status")) {
			long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
	     	modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
	     	modelHolder.put("houses", houseDao.viewHousesSortedByStatus(((User)session.getAttribute("currentUser")).getUserName()));
		}else {
			long id  = teamDAO.getTeamId(((User)session.getAttribute("currentUser")).getUserName());
	     	modelHolder.put("teamMembers",teamDAO.getAllTeamMembers(id));
	     	modelHolder.put("houses", houseDao.viewHouses(((User)session.getAttribute("currentUser")).getUserName()));
		}
		
		return "/salesData";
	}
	
	@RequestMapping(path="/changePassword", method=RequestMethod.GET)
	public String showChangePassForm() {
		return "/changePassword"; 
	}
	
	@RequestMapping(path="/changePassword", method=RequestMethod.POST)
	public String submitChangePassForm() {
		return "redirect:/changePassword"; 
	}
	
	
}
