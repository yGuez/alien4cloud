package alien4cloud.events;

import lombok.Getter;

/**
 * .
 */
@Getter
public class DeploymentNodeInstanceStateChangedEvent extends AlienEvent {

    private static final long serialVersionUID = -1126617350064097857L;

    private String deploymentId;

    private String nodeId;

    private String instanceId;

    private String state;

    public DeploymentNodeInstanceStateChangedEvent(Object source, String deploymentId, String nodeId, String instanceId, String state) {
        super(source);
        this.deploymentId = deploymentId;
        this.nodeId = nodeId;
        this.instanceId = instanceId;
        this.state = state;
    }

}
