package com.lioncorp.service.consul.impl;

import java.util.Map;
import java.util.Optional;

import com.lioncorp.common.exception.SysException;
import com.lioncorp.service.consul.AbstractConsulService;
import com.lioncorp.service.consul.ConsulService;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.health.Service;

//@Component
public class ConsulServiceImpl extends AbstractConsulService implements ConsulService {



    @Override
    public void register() throws SysException {

        AgentClient agentClient = buildConsul().agentClient();
        try {            
            ImmutableRegCheck check = ImmutableRegCheck.builder().tcp("10.200.130.93"+":"+PORT)
            		.interval(CONSUL_HEALTH_INTERVAL).build();
            ImmutableRegistration.Builder builder = ImmutableRegistration.builder();
            builder.id(CONSUL_ID)
            		.name(CONSUL_NAME)
            		.addTags(CONSUL_TAGS)
//            		.address(url.getHost())
            		.address("10.200.130.93")
            		.port(PORT)
            		.addChecks(check);

            agentClient.register(builder.build());
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    @Override
    public void deregister() throws SysException {

        AgentClient agentClient = buildConsul().agentClient();

        try {
            agentClient.deregister(CONSUL_ID);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    @Override
    public Map<String, Service> services() throws SysException {

        Map<String, Service> serviceMap = buildConsul().agentClient().getServices();

        if (serviceMap != null && serviceMap.size() > 0) {
            if (serviceMap.containsKey("consul")) {
                serviceMap.remove("consul");
            }
        }

        return serviceMap;
    }

    @Override
    public void put(String key, String value) throws SysException {

        KeyValueClient keyValueClient = buildConsul().keyValueClient();

        try {
            keyValueClient.putValue(key, value);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    @Override
    public String get(String key) throws SysException {
        try {
            KeyValueClient keyValueClient = buildConsul().keyValueClient();
            Optional<String> optional = keyValueClient.getValueAsString(key);

            if("Optional.absent()".equals(optional.toString())){
                return "";
            }

            return optional.get();
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    @Override
    public void delete(String key) throws SysException {

        KeyValueClient keyValueClient = buildConsul().keyValueClient();

        try {
            keyValueClient.deleteKey(key);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

}
