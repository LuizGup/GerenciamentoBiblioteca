package com.biblioteca.service;

import com.biblioteca.entity.Users;
import com.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Users> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Users updateUser(Long id, Users user) {
        Users existingUser = findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setStatus(user.getStatus());

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

}
