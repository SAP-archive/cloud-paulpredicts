package com.sap.pto.util.configuration;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.sap.pto.dao.ConfigDAO;
import com.sap.pto.dao.entities.Config;

public class ConfigDBAdapter implements ConfigAdapter {
    @Override
    public Properties getByGroup(String group) {
        Properties groupProperties = new Properties();

        List<Config> configs = ConfigDAO.getByGroup(group);
        for (Config config : configs) {
            if (StringUtils.isNotBlank(config.getParamKey()) && StringUtils.isNotBlank(config.getParamValue())) {
                groupProperties.put(config.getParamKey(), config.getParamValue());
            }
        }

        return groupProperties;
    }

    @Override
    public void save(String group, String key, String value) {
        Config config = new Config(group, key, value);
        ConfigDAO.save(config);
    }
}
