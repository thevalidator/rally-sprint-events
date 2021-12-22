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
import ru.thekrechetofficial.entity.CategorySet;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
public interface CategorySetService {
    
    void createSet(String name); //List<Category> list
    List<CategorySet> getAllNotDeletedCatSets();
    List<CategorySet> getAllActiveCatSets();
    CategorySet getCatSet(Long catSetId);
    void save(CategorySet catSet);
    CategorySet getCatSetByEventId(long eventId);
    
}
