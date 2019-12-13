package com.hexing.uap.bpm.app.repository;

import com.hexing.uap.bpm.app.model.common.model.ActDeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lv on 2019/7/9.
 */

@Repository
public interface ActDeModelRepository extends JpaRepository<ActDeModel, String> {
}
