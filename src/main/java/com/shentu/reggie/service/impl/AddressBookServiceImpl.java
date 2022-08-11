/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/23
 * Time: 10:08
 * Describe:
 */

package com.shentu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shentu.reggie.entity.AddressBook;
import com.shentu.reggie.mapper.AddressBookMapper;
import com.shentu.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
