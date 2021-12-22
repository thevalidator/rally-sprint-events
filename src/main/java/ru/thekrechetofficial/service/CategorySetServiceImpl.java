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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.CategorySet;
import ru.thekrechetofficial.repository.CategoryRepository;
import ru.thekrechetofficial.repository.CategorySetRepository;


/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class CategorySetServiceImpl implements CategorySetService {
    
    private final CategoryRepository catRepository;
    private final CategorySetRepository catSetRepository;

    @Autowired
    public CategorySetServiceImpl(CategoryRepository catRepository, CategorySetRepository carSetRepository) {
        this.catRepository = catRepository;
        this.catSetRepository = carSetRepository;
    }
    
    @Override
    public void createSet(String name) {
        
        CategorySet catSet = new CategorySet();
        catSet.setName(name);
        catSet.setIsActive(false);
        catSet.setIsDeleted(false);
        
        catSetRepository.save(catSet);
    }

    @Override
    public List<CategorySet> getAllNotDeletedCatSets() {
        List<CategorySet> catSets = catSetRepository.findAllByIsDeleted(false);
        for(CategorySet cs: catSets) {
            cs.getCategories().size();
        }
        return catSets;
    }

    @Override
    public List<CategorySet> getAllActiveCatSets() {
        List<CategorySet> catSets = catSetRepository.findAllByIsDeletedAndIsActive(false, true);
        for(CategorySet cs: catSets) {
            cs.getCategories().size();
        }
        return catSets;
    }
    
    @Override
    public CategorySet getCatSet(Long catSetId) {
        CategorySet catSet = catSetRepository.findById(catSetId).orElseThrow();
        catSet.getCategories().size();
        
        return catSet;
    }

    @Override
    public CategorySet getCatSetByEventId(long eventId) {
        CategorySet catSet = catSetRepository.findByEventId(eventId).orElseThrow();
        catSet.getCategories().size();
        return catSet;
    }

    @Override
    public void save(CategorySet catSet) {
        catSetRepository.save(catSet);
    }

}
