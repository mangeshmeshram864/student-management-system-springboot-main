package com.ytp.SmartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ytp.SmartContactManager.dao.ContactRepository;
import com.ytp.SmartContactManager.dao.UserRepository;
import com.ytp.SmartContactManager.entity.Contact;
import com.ytp.SmartContactManager.entity.User;
import com.ytp.SmartContactManager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
    
	@Autowired
	private ContactRepository contactRepository;
	
	//for getting userdata user userRepository
	@Autowired
	private UserRepository userRepository;
	//adding comman data to response
	
	
	
	@ModelAttribute
	public void addCommanDate(Model model,Principal principal)
	{
		String userName=principal.getName();
		System.out.println("username"+userName);
		
		//get the user using username(Email)
		
		User user=userRepository.getUserByUserName(userName);
	    System.out.println(user);
	    model.addAttribute("user", user);
	}
	@RequestMapping("/dashboard")
	public String dashboard(Model model ,Principal principal)
	{
		model.addAttribute("title","User Dashboard");	
	return "normal/user_dashboard";
	
	}
	//open add form handler
	@GetMapping("/add-contact")
	public String opnAddContactForm(Model model)
	{ 
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	
	//PROCESSIONG ADD CONTACT FORM 
	//multipart is used for img upload insted of using int float String bcoz it is image
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal,HttpSession session)
	{
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
	
		//processing and uploading file....
		
		if(file.isEmpty())
		{
		   //if the file is empty then try our msg
			System.out.println("file is empty");
			contact.setImage("registration.png");
			contact.setImage("contact_default.jpg");
			System.out.println("default image is loaded....");
			
		}else
		{
			//upload the file to folder and update the name to contact
		    contact.setImage(file.getOriginalFilename());
		    
		  File saveFile=  new ClassPathResource("static/img").getFile();
		
		  Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		  Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		  
		  System.out.println("img is uploaded");
		}
		
		user.getContacts().add(contact);
		
		contact.setUser(user);
		
		this.userRepository.save(user);
		
		System.out.println("added to database");
		
		
		System.out.println("Data:---"+contact);
		
		//mesage success....dynamically
		session.setAttribute("message",new Message("Your contact is added !!  Add more..", "success"));
		
		}
		catch(Exception e) 
		{ 
			System.out.println("error"+e.getMessage());
			e.printStackTrace();
			//message success...
			session.setAttribute("message",new Message("Something went wrong !!", "danger"));
			
		}
		return "normal/add_contact_form";
		
		
	}
	
	//per page= 20 [n]//@pathvariable for dynamically mnaging the pagination
	//current page =0
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page")Integer page,Model model,Principal principal)
	{
		
		model.addAttribute("title","Show user Contacts");
		
		//showing contact to display
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		Pageable pageable=PageRequest.of(page,5);
		
		Page <Contact> contacts=
				  this.contactRepository.findContactByUser(user.getId(),pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	//showing perticular contact details
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal)
	{
		
		Optional<Contact> contactoptional = this.contactRepository.findById(cId); 
		Contact contact=contactoptional.get();
		
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
		}
		
		
		
		
		return "normal/contact_detail";
	}
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session,Principal principal)
	{
//		Optional<Contact> contactoptional = this.contactRepository.findById(cId);
//	    Contact contact = contactoptional.get();
	    
	   Contact contact=this.contactRepository.findById(cId).get();
	    
	   
	   User user=this.userRepository.getUserByUserName(principal.getName());
	  user.getContacts().remove(user);
	  
	    this.contactRepository.delete(contact);
	    
	    System.out.println("contact deleted successfully...! ");
	    session.setAttribute("message",new Message("contact deleted succesfully...", "success"));
		
		return "redirect:/user/show-contacts/0";	
	}
	//your profile
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
		
		m.addAttribute("title","Profile Page");
		
		return "normal/profile";
	}
	
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m)
	{
		
		m.addAttribute("title","Update contact");
	    Contact contact = this.contactRepository.findById(cid).get();
		
	    m.addAttribute("contact", contact);
	    
	    return "normal/update_form";
	    
	}
	//Update contact handller
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,Model m,HttpSession session,Principal principal)
	{
		try
		{
			//old contact details 
			
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty())
			{
				//file work
				//REWRITE
				//delete old photo
				 File deleteFile=  new ClassPathResource("static/img").getFile();
					
				File file1=new File(deleteFile,oldContactDetail.getImage());
				file1.delete();
				
				//update new photo
				  File saveFile=  new ClassPathResource("static/img").getFile();
					
				  Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				  Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				  
				  contact.setImage(file.getOriginalFilename());
				System.out.println("okkkkkkkkkkkkk");
			}
			else {
				contact.setImage(oldContactDetail.getImage());
			}
			
		    User user = this.userRepository.getUserByUserName(principal.getName());
			
		    contact.setUser(user);
		    
		    this.contactRepository.save(contact);
		    
		    session.setAttribute("message",new Message("your contact is updated","sucess "));
			
		System.out.println(contact.getName());
		
		}catch(Exception e) {e.printStackTrace();}
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
}
