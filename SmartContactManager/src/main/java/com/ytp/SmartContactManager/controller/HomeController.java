package com.ytp.SmartContactManager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ytp.SmartContactManager.dao.UserRepository;
import com.ytp.SmartContactManager.entity.User;
import com.ytp.SmartContactManager.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
    @Autowired
	private UserRepository userRepository;
	@GetMapping("/")
	
	public String home(Model model )
	
	{
		model.addAttribute("title","Home- Smart Contact Manager");
		return "Home";
    }
	 
	@GetMapping("/about")
 	public String about(Model model )
	
 	{
 		model.addAttribute("title","About- Smart Contact Manager");
 		return "about";
     }
	
	@GetMapping("/signup")
 	public String signup(Model model )
	
 	{
 		model.addAttribute("title","Register- Smart Contact Manager");
 		model.addAttribute("user",new User());
 		return "signup"; 
     }
	//handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1,
			@RequestParam(value="agreement",
			defaultValue = "false")boolean agreement,
			Model model,HttpSession session)
	{
		try {
	
		if(!agreement)
		{
			System.out.println("you have not agreed the terms and conditions");
			throw new Exception("you have not agreed the terms and conditions");
		}
		if(result1.hasErrors())
		{ 
			System.out.println("ERROR "+result1.toString());
			model.addAttribute("user", user);
			return "signup";
		}
		
		user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageURL("");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		
		System.out.println("agreement"+agreement);
		System.out.println("user"+user);
		User result=this.userRepository.save(user);
		session.setAttribute("message",new Message("Successfully Register", "alert-success"));
		model.addAttribute("user",new User());
		return"signup";
	 }
		catch(Exception e)
	 {
		 e.printStackTrace();
		 model.addAttribute("user",user);
		 session.setAttribute("message",new Message("Something went wrong !!"+e.getMessage(), "alert-danger" ));
		 
		return"signup";
	 }
		
	}

    //handler for custom login 
	
	@GetMapping("/signin")
	public String customlogin(Model model)
	{
		model.addAttribute("title", "Login data");
		return "login";
	}
}
