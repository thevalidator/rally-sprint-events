/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.thekrechetofficial.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.dto.CategoryDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.repository.CategoryRepository;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createCategory(CategoryDto dto) {

        if (!categoryRepository.isExists(dto.getName(), dto.getAbbreviation())) {
            Category cat = new Category();
            cat.setName(dto.getName());
            cat.setAbbreviation(dto.getAbbreviation());

            categoryRepository.save(cat);
        }

    }

    @Override
    public List<Category> getAllCategoryFromSetByEventId(Long eventId) {
        List<Category> categories = categoryRepository.findAllCategoryFromSetByEventId(eventId);
        categories.size();
        return categories;
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getById(Long categoryId) {
        
        Category c = categoryRepository.findById(categoryId).orElseThrow();
        
        return c;
        
    }
    
    

}
