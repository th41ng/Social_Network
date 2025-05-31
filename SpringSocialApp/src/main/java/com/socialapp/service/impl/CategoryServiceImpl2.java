/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Category2;
import com.socialapp.repository.CategoryRepository2;
import com.socialapp.service.CategoryService2;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DELL G15
 */
@Service
public class CategoryServiceImpl2 implements CategoryService2 {

    @Autowired
    private CategoryRepository2 categoryRepository2;

    @Override
    public List<Category2> getCategories2() {
        return this.categoryRepository2.getCategories2();
    }
}
