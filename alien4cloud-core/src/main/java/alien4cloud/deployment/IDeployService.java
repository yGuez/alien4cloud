package alien4cloud.deployment;

import alien4cloud.model.deployment.DeploymentTopology;
import alien4cloud.model.deployment.IDeploymentSource;
import alien4cloud.plugin.aop.Overridable;
import alien4cloud.topology.TopologyValidationResult;

/**
 * Created by xdegenne on 27/09/2016.
 */
public interface IDeployService {
    @Overridable
    String deploy(DeploymentTopology deploymentTopology, IDeploymentSource deploymentSource);

    TopologyValidationResult prepareForDeployment(DeploymentTopology deploymentTopology);
}
