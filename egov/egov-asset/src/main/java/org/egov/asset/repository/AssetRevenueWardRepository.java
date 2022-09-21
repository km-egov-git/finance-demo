package org.egov.asset.repository;

import org.egov.asset.model.AssetLocationRevenueWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssetRevenueWardRepository extends JpaRepository<AssetLocationRevenueWard, Long> {

}
