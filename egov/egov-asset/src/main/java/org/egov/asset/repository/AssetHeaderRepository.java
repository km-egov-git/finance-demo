package org.egov.asset.repository;

import org.egov.asset.model.AssetHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetHeaderRepository extends JpaRepository<AssetHeader, Long> {

}
