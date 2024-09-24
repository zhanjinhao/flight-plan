package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.flowable.engine.repository.Deployment;

import java.util.Date;

/**
 * 部署基础数据
 */
@Setter
@Getter
@ToString
public abstract class AbstractDeploymentDto {

  private String deploymentId;

  private String deploymentName;

  private Date deploymentCreateDateTime;

  public void accept(Deployment deployment) {
    this.setDeploymentId(deployment.getId());
    this.setDeploymentName(deployment.getName());
    this.setDeploymentCreateDateTime(deployment.getDeploymentTime());
  }

}
