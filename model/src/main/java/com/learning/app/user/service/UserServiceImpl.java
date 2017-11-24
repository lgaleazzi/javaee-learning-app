package com.learning.app.user.service;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.utils.DataValidation;
import com.learning.app.common.utils.PasswordEncryption;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;
import com.learning.app.user.repository.UserRepository;

import javax.inject.Inject;
import javax.validation.Validator;

public class UserServiceImpl implements UserService
{
    @Inject
    UserRepository userRepository;

    @Inject
    Validator validator;

    @Override
    public User add(User user)
    {
        DataValidation.validateEntityFields(validator, user);

        user.setPassword(PasswordEncryption.encryptPassword(user.getPassword()));

        return userRepository.add(user);
    }

    @Override
    public User findById(Long id)
    {
        User user = userRepository.findById(id);
        if (user == null)
        {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public void update(User user)
    {
        User existentUser = findById(user.getId());
        user.setPassword(existentUser.getPassword());

        DataValidation.validateEntityFields(validator, user);

        userRepository.update(user);
    }

    @Override
    public void updatePassword(Long id, String password)
    {
        User user = findById(id);
        user.setPassword(PasswordEncryption.encryptPassword(password));

        userRepository.update(user);
    }

    @Override
    public User findByEmail(String email)
    {
        User user = userRepository.findByEmail(email);
        if (user == null)
        {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public User findByEmailAndPassword(String email, String password)
    {
        User user = findByEmail(email);

        if (!user.getPassword().equals(PasswordEncryption.encryptPassword(password)))
        {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public PaginatedData<User> findByFilter(final UserFilter userFilter)
    {
        return userRepository.findByFilter(userFilter);
    }

    @Override
    public void delete(Long id)
    {
        if (!userRepository.idExists(id))
        {
            throw new UserNotFoundException();
        }

        userRepository.delete(id);
    }
}
