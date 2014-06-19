/*
 * The MIT License
 *
 * Copyright 2014 J. David Mendoza <jdmendozar@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.irsvped.service.impl;

import java.util.Map;
import org.davidmendoza.irsvped.dao.UserDao;
import org.davidmendoza.irsvped.model.Connection;
import org.davidmendoza.irsvped.model.Role;
import org.davidmendoza.irsvped.model.User;
import org.davidmendoza.irsvped.service.BaseService;
import org.davidmendoza.irsvped.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Service
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    private UserDao userDao;

    @Transactional(readOnly = true)
    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Transactional(readOnly = true)
    @Override
    public User getByOpenId(String openId) {
        return userDao.getByOpenId(openId);
    }

    @Override
    public User update(User user) {
        return userDao.update(user);
    }

    @Override
    public User create(User user) {
        return userDao.create(user);
    }

    @Override
    public Role getRole(String authority) {
        return userDao.getRole(authority);
    }

    @Override
    public Connection getConnection(String username) {
        return userDao.getConnection(username);
    }

    @Override
    public Map<String, Object> list(Map<String, Object> params) {
        return userDao.list(params);
    }

    @Override
    public User get(Long userId) {
        return userDao.get(userId);
    }

    @Override
    public String delete(Long userId) {
        User user = userDao.get(userId);
        userDao.delete(user);
        return user.getUsername();
    }

}
