package com.clicker.scheduler.repository;

import com.clicker.scheduler.domain.entities.VAssemblyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VAssemblyTaskRepository extends JpaRepository<VAssemblyTask, String>, JpaSpecificationExecutor<VAssemblyTask> {

}
