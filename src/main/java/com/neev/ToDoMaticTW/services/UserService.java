package com.neev.ToDoMaticTW.services;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.models.UsersTasks;
import com.neev.ToDoMaticTW.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public User saveUser(User user){
        List<GrantedAuthority> authorities = new ArrayList<>(){
            {
                add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        };

        List<UsersTasks> tasks = new ArrayList<>(){
            {
                add(new UsersTasks("eating", true));
                add(new UsersTasks("coding", true));
                add(new UsersTasks("sleeping", false));
            }
        };

        user.setAuthorities(authorities);
        user.setTasks(tasks);
        userRepository.save(user);
        return user;
    }

    public Boolean doesUserExistsAlready(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        return user;
    }

    public List<UsersTasks> getTasks(String username) throws Exception{

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Oops Something Went wrong...")
        );
        return user.getTasks();
    }
}
