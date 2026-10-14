package tn.esprit.spring.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public User addUser(User u) {
        logger.info("Method addUser : start");
        logger.debug("addUser : saving {}", u);
        User saved = userRepository.save(u);
        logger.info("Method addUser : end (id={})", saved.getId());
        return saved;
    }

    @Override
    public User retrieveUser(Long id) {
        logger.info("Method retrieveUser : start (id={})", id);
        User u = userRepository.findById(id).orElse(null);
        if (u == null) {
            logger.warn("retrieveUser : no user found with id={}", id);
        } else {
            logger.debug("retrieveUser : found {}", u);
        }
        logger.info("Method retrieveUser : end");
        return u;
    }

    @Override
    public User updateUser(User u) {
        logger.info("Method updateUser : start (id={})", u.getId());
        User updated = userRepository.save(u);
        logger.debug("updateUser : new state of the user {}", updated);
        logger.info("Method updateUser : end");
        return updated;
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Method deleteUser : start (id={})", id);
        try {
            userRepository.deleteById(id);
            logger.info("deleteUser : user id={} deleted", id);
        } catch (Exception e) {
            logger.error("deleteUser : error while deleting id={}", id, e);
            throw e;
        }
    }
}