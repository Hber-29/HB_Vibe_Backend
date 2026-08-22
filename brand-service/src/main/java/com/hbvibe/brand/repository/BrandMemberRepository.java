package com.hbvibe.brand.repository;

import com.hbvibe.brand.entity.BrandMember;
import com.hbvibe.brand.entity.BrandRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandMemberRepository extends JpaRepository<BrandMember,String> {
    long countByUserIdAndRole(String userId, BrandRole role);
}
