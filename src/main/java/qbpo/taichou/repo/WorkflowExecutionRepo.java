package qbpo.taichou.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowExecutionRepo extends JpaRepository<WorkflowExecution, Long>{

}
