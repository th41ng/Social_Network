package com.socialapp.formatters;

import com.socialapp.pojo.Category;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

public class CategoryFormatter implements Formatter<Category> {

    @Override
    public String print(Category category, Locale locale) {
        // Return the name of the category for display
        return category.getName();
    }

    @Override
    public Category parse(String cateId, Locale locale) throws ParseException {
        // Parse the ID into a Category object
        Category c = new Category();
        c.setId(Integer.valueOf(cateId));
        return c;
    }
}
