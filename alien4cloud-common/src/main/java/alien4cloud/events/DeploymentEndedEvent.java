package alien4cloud.events;

import lombok.Getter;

/**
 * .
 */
@Getter
public class DeploymentEndedEvent extends AlienEvent {

    private static final long serialVersionUID = -1126617350064097857L;

    private String deploymentId;

    public DeploymentEndedEvent(Object source, String deploymentId) {
        super(source);
        this.deploymentId = deploymentId;
    }

}
