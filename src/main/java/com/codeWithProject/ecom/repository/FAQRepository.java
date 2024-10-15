package com.codeWithProject.ecom.repository;

import com.codeWithProject.ecom.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface FAQRepository extends JpaRepository<FAQ, Long> {

    //List<FAQ> findAlProductId(Long productId);


}
