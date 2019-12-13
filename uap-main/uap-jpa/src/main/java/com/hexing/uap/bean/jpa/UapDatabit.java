package com.hexing.uap.bean.jpa;

import javax.persistence.*;

/**
 * Created by lv on 2019/11/6.
 */
@Entity
@Table(name = "UAP_DATABIT")
public class UapDatabit {
    private long id;
    private long databit;
    private String description;
    private long tenantId;
    private String reserved;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "databitId")
    @TableGenerator(table = "UAP_SEQUENCE", name = "databitId", pkColumnValue = "databitId", allocationSize = 100)
    @Column(name = "ID", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "DATABIT")
    public long getDatabit() {
        return databit;
    }

    public void setDatabit(long databit) {
        this.databit = databit;
    }

    @Column(name = "DESCRIPTION", length = 256)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "TENANT_ID")
    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    @Column(name = "RESERVED", length = 128)
    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }
}
