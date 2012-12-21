package com.sap.pto.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "Configurations")
@NamedQuery(name = Config.QUERY_BYGROUP, query = "SELECT c FROM Config c WHERE c.paramGroup = :group order by c.paramGroup")
@IdClass(ConfigPK.class)
@Entity
public class Config extends BasicEntity {
    public static final String QUERY_BYGROUP = "getConfigByGroup";

    @Id
    private String paramGroup;
    @Id
    private String paramKey;
    @Column(length = 4000)
    private String paramValue;

    public Config() {
        // just needed for JPA
    }

    public Config(String group, String key, String value) {
        this.paramGroup = group;
        this.paramKey = key;
        this.paramValue = value;
    }

    public String getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(String paramGroup) {
        this.paramGroup = paramGroup;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "Config [paramGroup=" + paramGroup + ", paramKey=" + paramKey + ", paramValue=" + paramValue + "]";
    }

}
