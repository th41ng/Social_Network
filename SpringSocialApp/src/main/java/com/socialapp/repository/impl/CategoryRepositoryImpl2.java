/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;
import com.socialapp.pojo.Category2;
import com.socialapp.repository.CategoryRepository2;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class CategoryRepositoryImpl2 implements CategoryRepository2 {
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Category2> getCategories2() {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery("FROM Category2", Category2.class).getResultList();
    }
}