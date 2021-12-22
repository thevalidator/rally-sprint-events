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
package ru.thekrechetofficial.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.CategoryDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.CategorySet;
import ru.thekrechetofficial.service.CategoryService;
import ru.thekrechetofficial.service.CategorySetService;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class CategoryController {

    private final CategoryService catService;
    private final CategorySetService catSetService;

    @Autowired
    public CategoryController(CategoryService catService, CategorySetService catSetService) {
        this.catService = catService;
        this.catSetService = catSetService;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCategories(ModelMap model) {

        model.put("categories", catService.getAllCategories());

        return "categories";
    }

    @GetMapping("/set-of-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCatSets(ModelMap model) {

        model.put("catSets", catSetService.getAllNotDeletedCatSets());

        return "set-of-categories";
    }

    @PostMapping("/add-category")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(CategoryDto dto) {

        catService.createCategory(dto);

        return "redirect:/categories";
    }

    @PostMapping("/add-cat-set")
    public String createCatSet(@RequestParam(name = "name") String name) {

        catSetService.createSet(name);

        return "redirect:/set-of-categories";
    }

    @GetMapping("/edit-category-set")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCatSets(ModelMap model,
            @RequestParam(name = "catSetId") Long catSetId) {

        CategorySet catSet = catSetService.getCatSet(catSetId);
        List<Category> cats = catService.getAllCategories();

        model.put("catSet", catSet);
        model.put("categories", cats);

        return "edit-category-set";
    }

    @PostMapping("/add-category-to-set")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCategoryToCatSet(@RequestParam(name = "catSetId") Long catSetId,
            @RequestParam(name = "categoryId") Long catId,
            RedirectAttributes redirectAttributes) {

        CategorySet catSet = catSetService.getCatSet(catSetId);
        if (!catSet.getIsActive()) {
            Category category = catService.getById(catId);
            catSet.getCategories().add(category);
            catSetService.save(catSet);
        }

        redirectAttributes.addAttribute("catSetId", catSetId);
        return "redirect:/edit-category-set";
    }

    @PostMapping("/delete-category-from-set")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategoryToCatSet(@RequestParam(name = "catSetId") Long catSetId,
            @RequestParam(name = "categoryId") Long catId,
            RedirectAttributes redirectAttributes) {

        CategorySet catSet = catSetService.getCatSet(catSetId);
        if (!catSet.getIsActive()) {
            Category category = catService.getById(catId);
            catSet.getCategories().remove(category);
            catSetService.save(catSet);
        }

        redirectAttributes.addAttribute("catSetId", catSetId);
        return "redirect:/edit-category-set";
    }

    @PostMapping("/confirm-category-set")
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmCatSet(@RequestParam(name = "catSetId") Long catSetId) {

        CategorySet catSet = catSetService.getCatSet(catSetId);
        catSet.setIsActive(true);

        catSetService.save(catSet);
        return "redirect:/set-of-categories";
    }
}
