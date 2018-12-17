
package com.jarvis.cache.demo.test;

import com.jarvis.cache.demo.condition.UserCondition;
import com.jarvis.cache.demo.entity.UserDO;
import com.jarvis.cache.demo.mapper.UserMapper;
import com.jarvis.cache.demo.service.UserService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: jiayu.qiu
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest extends BaseServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    //@Test
    @Transactional
    @Rollback(true)
    public void test1Add() throws Exception {
        UserDO userDO = UserDO.builder().name("tmp").password("aaaa").build();
        Long userId = userService.register(userDO);

        userDO = UserDO.builder().name("tmp2").password("aaaa2").build();
        userId = userService.register(userDO);

        userDO = UserDO.builder().id(userId).name("tmp2").password("aaaa3").build();
        userService.updateUser(userDO);

        userService.doLogin("tmp2", "aaaa3");

        UserCondition condition = new UserCondition();
        Pageable pageable = new PageRequest(0, 10);
        condition.setPageable(pageable);

        List<UserDO> list = userService.listByCondition(condition);
        Assert.assertNotNull(list);
        for (UserDO user : list) {
            System.out.println("list item --->" + user);
        }

        userDO = userService.getUserById(userId);
        System.out.println("detail-->" + userDO);
        userDO = userService.getUserById(userId);
        System.out.println("detail-->" + userDO);
    }

    @Test
    public void testGetUserById() {
        Long userId = 10L;
        UserDO userDO = userService.getUserById(userId);
        System.out.println("detail-->" + userDO);
        userService.deleteUserById(userId);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testMagic() throws Exception {
        for (int i = 0; i < 10; i++) {
            UserDO userDO = UserDO.builder().name("magicTestUser" + i).password("aaaa").build();
            userService.register(userDO);
        }

        UserCondition condition = new UserCondition();
        Pageable pageable = new PageRequest(0, 5);
        condition.setPageable(pageable);

        List<UserDO> list = userService.listByCondition(condition);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 5);
        for (UserDO user : list) {
            Assert.assertNotNull(user);
            System.out.println("list item --->" + user);
        }

        list = userMapper.listByIds2(1L, 2L, 5L, 6L, 101L, 102L);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 4);
        for (UserDO user : list) {
            Assert.assertNotNull(user);
            System.out.println("list item --->" + user);
        }

        list = userMapper.listByIds2(500L, 600L, 700L);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 0);
        for (UserDO user : list) {
            Assert.assertNotNull(user);
            System.out.println("list item --->" + user);
        }

        list = userMapper.listByIds2(500L, 600L, 700L);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 0);
        for (UserDO user : list) {
            Assert.assertNotNull(user);
            System.out.println("list item --->" + user);
        }

    }

    @Test
    @Transactional
    @Rollback(true)
    public void testMagic2() throws Exception {
        List<UserDO> list = userService.testMagic("name", "pwd", 100L, 200L, 300L);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 3);

        list = userService.testMagic("name", "pwd", 100L, 200L, 300L, 400L);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 4);

        List<Long> ids = Arrays.asList(100L, 200L, 300L, 400L, 500L);
        list = userService.testMagic("name", "pwd", ids);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 5);
    }

}
