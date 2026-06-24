package com.mall.repository;

import com.mall.entity.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

    List<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId);
    Page<AddressBook> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId, Pageable pageable);
}
