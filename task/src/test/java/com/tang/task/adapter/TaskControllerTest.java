package com.tang.task.adapter;

import com.tang.task.domain.mapper.TaskMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

/**
 * 任务相关的接口测试
 *
 * @author he
 * @since 2024-01.05-22:51
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    @DisplayName("当查询参数为空，应该查询成功")
    void should_return_size_more_than_zero_when_query_body_empty() throws Exception {
        Mockito.when(taskMapper.queryAllTask(Mockito.any())).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/task").header("content-type", "application/json").content("{}"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }
}