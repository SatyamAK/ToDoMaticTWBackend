package com.neev.ToDoMaticTW.services;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.models.UsersTask;
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

        List<UsersTask> tasks = new ArrayList<>();

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

    public List<UsersTask> getTasks(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Oops Something Went wrong...")
        );
        return user.getTasks();
    }

    public List<UsersTask> addTask(String username, UsersTask usersTask) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Oops something went wrong")
        );

        Integer base = 1;
        Integer newId = base + user.getTasks().size();
        usersTask.setId(newId);
        user.getTasks().add(usersTask);
        userRepository.save(user);
        return user.getTasks();
    }

    public List<UsersTask> updateTask(String username, UsersTask usersTask) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Oops something went wrong")
        );

        user.getTasks().get(usersTask.getId() - 1).setTitle(usersTask.getTitle());
        user.getTasks().get(usersTask.getId() - 1).setDone(usersTask.getDone());
        userRepository.save(user);
        return user.getTasks();
    }

    public List<UsersTask> deleteTask(String username, int taskId) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Oops something went wrong")
        );
        List<UsersTask> tasks = new ArrayList<>();

        for(int i=0; i<taskId-1; ++i){
            tasks.add(user.getTasks().get(i));
        }

        if(taskId < user.getTasks().size())
            for(int i=taskId; i<user.getTasks().size(); ++i){
                UsersTask taskToBeAdded = user.getTasks().get(i);
                taskToBeAdded.setId(taskToBeAdded.getId()-1);
                tasks.add(taskToBeAdded);
            }

        user.setTasks(tasks);
        userRepository.save(user);
        return user.getTasks();
    }
}
