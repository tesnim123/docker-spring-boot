package tn.esprit.spring.service;

import tn.esprit.spring.entities.User;

public interface IUserService {

    User addUser(User u);

    User retrieveUser(Long id);

    User updateUser(User u);

    void deleteUser(Long id);
}