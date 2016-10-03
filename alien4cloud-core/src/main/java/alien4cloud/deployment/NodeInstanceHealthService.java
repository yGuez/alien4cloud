package alien4cloud.deployment;

import alien4cloud.events.DeploymentNodeInstanceStateChangedEvent;
import alien4cloud.model.deployment.Deployment;
import alien4cloud.model.deployment.DeploymentTopology;
import alien4cloud.orchestrators.plugin.IOrchestratorPlugin;
import alien4cloud.paas.OrchestratorPluginService;
import alien4cloud.paas.model.PaaSDeploymentContext;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by xdegenne on 27/09/2016.
 */
@Service
@Slf4j
public class NodeInstanceHealthService implements ApplicationListener<DeploymentNodeInstanceStateChangedEvent> {

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private OrchestratorPluginService orchestratorPluginService;

    @Inject
    private DeploymentRuntimeStateService deploymentRuntimeStateService;

    @Override
    public void onApplicationEvent(DeploymentNodeInstanceStateChangedEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("A DeploymentNodeInstanceStateChangedEvent event has been received concerning the deployment <{}> instance <{}> state <{}>", event.getDeploymentId(), event.getInstanceId(), event.getState());
        }

        Deployment deployment = deploymentService.get(event.getDeploymentId());
        if (deployment.getEndDate() != null) {
            if (log.isDebugEnabled()) {
                log.debug("A DeploymentNodeInstanceStateChangedEvent event has been received concerning the deployment <{}> but it seems to be undeployed now, just ignoring", event.getDeploymentId());
            }
            return;
        }
        try {
            IOrchestratorPlugin orchestratorPlugin = orchestratorPluginService.getOrFail(deployment.getOrchestratorId());
            DeploymentTopology deployedTopology = deploymentRuntimeStateService.getRuntimeTopology(deployment.getId());
            PaaSDeploymentContext deploymentContext = new PaaSDeploymentContext(deployment, deployedTopology);
            // FIXME: here we call a workflow but we should call a orchesrator plugin method
            Map<String, Object> inputs = Maps.newHashMap();
            inputs.put("node_instance_id", event.getInstanceId());
            inputs.put("state", event.getState());
            orchestratorPlugin.launchWorkflow(deploymentContext, "set_instance_state", inputs, null);
        } catch(Exception e) {
            log.error("Failed to inform the orcherstrator about the node instance state change", e);
        }

    }

}
