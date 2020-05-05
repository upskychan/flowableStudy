package com.upsky.flowablestudy.idm;

import org.flowable.common.engine.api.management.TableMetaData;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 人员组织测试类.
 */
public class IdmTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    IdmEngine idmEngine;
    IdmEngineConfiguration idmEngineConfiguration;
    IdmIdentityService idmIdentityService;
    IdmManagementService idmManagementService;

    @Before
    public void init() {
        InputStream inputStream = IdmTest.class.getClassLoader().getResourceAsStream("flowable.idm.cfg.xml");
        idmEngine = IdmEngineConfiguration.createIdmEngineConfigurationFromInputStream(inputStream).buildIdmEngine();
        idmEngineConfiguration = idmEngine.getIdmEngineConfiguration();
        idmIdentityService = idmEngine.getIdmIdentityService();
        idmManagementService = idmEngine.getIdmManagementService();
        System.out.println("idmEngine.getName:" + idmEngine.getName());
    }

    @Test
    public void testQueryUser() {
        List<User> userList = idmIdentityService.createUserQuery().list();
        if (userList != null) {
            System.out.println("查询用户总数：" + userList.size());
            for (User user : userList) {
                System.out.println(user.getFirstName() + " " + user.getLastName());
            }
        }
    }

    @Test
    public void testAddUser() {
        UserEntityImpl user = new UserEntityImpl();
        user.setId("user1");
        user.setRevision(0);
        user.setFirstName("FName1");
        user.setLastName("LName1");
        user.setEmail("email@email.com");
        user.setPassword("1");
        idmIdentityService.saveUser(user);
    }

    @Test
    public void testAddGroup() {
        GroupEntityImpl group = new GroupEntityImpl();
        group.setId("group1");
        group.setRevision(0);
        group.setName("测试组1");
        idmIdentityService.saveGroup(group);
    }

    @Test
    public void testCreateMembership() {
        String userId = "user1";
        String groupId = "group1";
        idmIdentityService.createMembership(userId, groupId);
    }

    /**
     * 获取表记录数.
     */
    @Test
    public void getTableCount() {
        Map<String, Long> tableCount = idmManagementService.getTableCount();
        for (String tableName : tableCount.keySet()) {
            System.out.print(tableName + ":");
            System.out.println(tableCount.get(tableName));
        }
    }

    @Test
    public void getTableMetaData() {
        TableMetaData tableMetaData = idmManagementService.getTableMetaData("ACT_ID_USER");
        logger.info(tableMetaData.getTableName() + ":");
        logger.info(tableMetaData.getColumnNames().toString());
        logger.info(tableMetaData.getColumnTypes().toString());
    }
}
