package com.cplusjuice.anorm;

import com.cplusjuice.anorm.entity.TestEntity;
import com.cplusjuice.anorm.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnormIntegrationTest {

    @Mock
    private Configuration conf;
    private Properties props;
    private ANORM anorm;
    private Query<TestEntity> query;

    @Before
    public void setUp() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/config.properties")) {

            assertNotNull("unable to find config.properties", is);

            props = new Properties();
            props.load(is);

            when(conf.getDriver()).thenReturn(SqlDriver.PG_SQL);
            when(conf.getLocation()).thenReturn(props.getProperty("db.postgres.location"));
            when(conf.getLogin()).thenReturn(props.getProperty("db.postgres.login"));
            when(conf.getPassword()).thenReturn(props.getProperty("db.postgres.pass"));
        }

        anorm = new ANORM(conf);
        query = new Query<>(TestEntity.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createTable() {
        assertTrue(query.createTable());
        assertTrue(query.isTableExists());
    }

    @Test
    public void selectOne() {
//        System.out.println(query.selectOne());
    }
}