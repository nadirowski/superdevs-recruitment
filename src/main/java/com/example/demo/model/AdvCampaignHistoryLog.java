package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "ADV_CAMPAING_HISTORY")
public class AdvCampaignHistoryLog {

    @Id
    @GeneratedValue
    private Long id;

    private String datasource;
    private String campaign;
    private String daily;
    private long clicks;
    private long impressions;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

}
