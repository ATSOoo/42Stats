package com.fluchtens.stats.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fluchtens.stats.campus.Campus;
import com.fluchtens.stats.campus.CampusRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampusRepository campusRepository;
    
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public long getUsersCount() {
        return this.userRepository.count();
    }

    public Double getUsersAverageLevel() {
        List<User> users = this.userRepository.findAll();
        if (users.isEmpty()) {
            return 0.0;
        }
        return this.userRepository.findAverageLevel();
    }

    public List<User> getCampusUsers(int id, String poolMonth, String poolYear, Pageable pageable) {
        Optional<Campus> campusOptional = this.campusRepository.findById(id);
        if (!campusOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campus not found");
        }

        if (poolMonth != null && poolYear != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("level").descending());
            return this.userRepository.findByCampusPool(id, poolMonth, poolYear, pageable);
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("level").descending());
        return this.userRepository.findByCampusId(id, pageable);
    }

    public int getCampusUsersCount(int id, String poolMonth, String poolYear) {
        Optional<Campus> campusOptional = this.campusRepository.findById(id);
        if (!campusOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campus not found");
        }

        if (poolMonth != null && poolYear != null) {
            return this.userRepository.countByCampusPool(id, poolMonth, poolYear);
        }

        return this.userRepository.countByCampus(id);
    }
}
