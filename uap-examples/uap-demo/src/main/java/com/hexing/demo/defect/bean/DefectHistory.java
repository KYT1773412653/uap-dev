package com.hexing.demo.defect.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Created by Administrator on 2018/7/16 0016.
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefectHistory {
    private long id;
    private long defectId;
    private String state;
    private String handler;
    private long handleTime;
    private String handleResult;
    private String handleSuggestion;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "defectHistoryId")
    @TableGenerator(table = "uap_sequence", name = "defectHistoryId", pkColumnValue = "defectHistoryId", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "defect_id")
    public long getDefectId() {
        return defectId;
    }

    public void setDefectId(long defectId) {
        this.defectId = defectId;
    }

    @Basic
    @Column(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Basic
    @Column(name = "handler")
    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Basic
    @Column(name = "handle_time")
    public long getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(long handleTime) {
        this.handleTime = handleTime;
    }

    @Basic
    @Column(name = "handle_result")
    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    @Basic
    @Column(name = "handle_suggestion")
    public String getHandleSuggestion() {
        return handleSuggestion;
    }

    public void setHandleSuggestion(String handleSuggestion) {
        this.handleSuggestion = handleSuggestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefectHistory that = (DefectHistory) o;

        if (id != that.id) return false;
        if (defectId != that.defectId) return false;
        if (handleTime != that.handleTime) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (handler != null ? !handler.equals(that.handler) : that.handler != null) return false;
        if (handleResult != null ? !handleResult.equals(that.handleResult) : that.handleResult != null) return false;
        if (handleSuggestion != null ? !handleSuggestion.equals(that.handleSuggestion) : that.handleSuggestion != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (defectId ^ (defectId >>> 32));
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (handler != null ? handler.hashCode() : 0);
        result = 31 * result + (int) (handleTime ^ (handleTime >>> 32));
        result = 31 * result + (handleResult != null ? handleResult.hashCode() : 0);
        result = 31 * result + (handleSuggestion != null ? handleSuggestion.hashCode() : 0);
        return result;
    }
}
