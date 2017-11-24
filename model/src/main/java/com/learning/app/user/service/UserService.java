package com.learning.app.user.service;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;

import javax.ejb.Local;

@Local
public interface UserService
{
    User add(User user);

    User findById(Long id);

    void update(User user);

    void updatePassword(Long id, String password);

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    PaginatedData<User> findByFilter(UserFilter userFilter);

    void delete(Long id);
}
