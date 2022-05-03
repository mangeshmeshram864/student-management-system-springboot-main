package com.ytp.SmartContactManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ytp.SmartContactManager.dao.UserRepository;
import com.ytp.SmartContactManager.entity.User;

public class UserDetailsSeviceImpl implements UserDetailsService {

	

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

	User user=userRepository.getUserByUserName(username);

	if(user==null)
	{
		throw new UsernameNotFoundException("Could not found user!!");
	}
	CustomeUserDetails customUserDetails=new CustomeUserDetails(user);
	
	return customUserDetails;
	}

}
