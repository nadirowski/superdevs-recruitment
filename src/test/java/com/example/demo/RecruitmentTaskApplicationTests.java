package com.example.demo;

import com.example.demo.model.AdvCampaignHistoryLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootTest
class RecruitmentTaskApplicationTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads() {
    }

    @Test
    void dataLoadsCorrectly() {
        final List<AdvCampaignHistoryLog> found = entityManager.createQuery("SELECT ahl FROM AdvCampaignHistoryLog ahl ORDER BY ahl.creationDate").getResultList();

        Assertions.assertThat(found).isNotEmpty();
        final AdvCampaignHistoryLog element = found.get(0);
        Assertions.assertThat(element.getCreationDate()).isNotNull();
        Assertions.assertThat(new SimpleDateFormat("yyyy-MM-dd").format(element.getCreationDate())).isEqualTo("2019-01-01");
    }

}
