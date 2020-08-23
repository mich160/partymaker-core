package db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializerTest {
    @InjectMocks DBInitializer dbInitializer;
    @Mock private Connection mockConnection;
    @Mock private Statement mockStatement;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateTables() throws SQLException
    {
        //Mockito.when(dbInitializer.createTables()).thenReturn();
        //powermock
    }


}
