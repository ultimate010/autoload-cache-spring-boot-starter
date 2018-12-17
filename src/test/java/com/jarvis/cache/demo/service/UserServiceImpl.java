package com.jarvis.cache.demo.service;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.Magic;
import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDO getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    // @Cache(expire = 600, key = "'userid-list-' + #hash(#args[0])")
    public List<UserDO> listByCondition(UserCondition condition) {
        List<Long> ids = userMapper.listIdsByCondition(condition);
        List<UserDO> list = null;
        if (null != ids && !ids.isEmpty()) {
            list = userMapper.listByIds(ids);
        }
        return list;
    }

    @Override
    // @CacheDeleteTransactional
    //@Transactional(rollbackFor = Throwable.class)
    public Long register(UserDO user) {
        Long userId = userMapper.getUserIdByName(user.getName());
        if (null != userId) {
            throw new RuntimeException("用户名已被占用！");
        }
        userMapper.addUser(user);
        return user.getId();
    }

    @Override
    public Long getUserIdByName(String name) {
        return userMapper.getUserIdByName(name);
    }

    @Override
    public UserDO doLogin(String name, String password) {
        Long userId = userMapper.getUserIdByName(name);
        if (null == userId) {
            throw new RuntimeException("用户不存在！");
        }
        UserDO user = userMapper.getUserById(userId);
        if (null == user) {
            throw new RuntimeException("用户不存在！");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码不正确！");
        }
        return user;
    }

    @Override
    //@CacheDeleteTransactional
    //@Transactional(rollbackFor = Throwable.class)
    public void updateUser(UserDO user) {
        userMapper.updateUser(user);
    }

    @Override
    // @CacheDeleteTransactional
    // @Transactional(rollbackFor = Throwable.class)
    public void deleteUserById(Long userId) {
        userMapper.deleteUserById(userId);
    }

    @Override
    /**
     * 为了测试Magic 支持多个参数的情况
     *
     * @param name
     * @param password
     * @param ids
     * @return
     */
    @Cache(expire = 60, expireExpression = "null == #retVal ? 30: 60",
            key = "'user-testMagic-' + #args[0] + '-' + #args[1] + '-' + #args[2]",
            magic = @Magic(
                    key = "'user-testMagic-' + #args[0] + '-' + #args[1] + '-' + #retVal.id", iterableArgIndex = 2))
    public List<UserDO> testMagic(String name, String password, Long... ids) {
        List<UserDO> list = new ArrayList<>(ids.length);
        for (Long id : ids) {
            list.add(new UserDO(id, name, password));
        }
        return list;
    }

    @Override
    @Cache(expire = 60, expireExpression = "null == #retVal ? 30: 60",
            key = "'user-testMagic-' + #args[0] + '-' + #args[1] + '-' + #args[2]",
            magic = @Magic(
                    key = "'user-testMagic-' + #args[0] + '-' + #args[1] + '-' + #retVal.id", iterableArgIndex = 2))
    public List<UserDO> testMagic(String name, String password, List<Long> ids) {
        List<UserDO> list = new ArrayList<>(ids.size());
        for (Long id : ids) {
            list.add(new UserDO(id, name, password));
        }
        return list;
    }

}
