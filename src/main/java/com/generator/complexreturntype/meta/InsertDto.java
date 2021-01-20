/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

package com.generator.complexreturntype.meta;

import java.util.Set;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class InsertDto {
    private String addedText;

    private String addMethods;

    private Set<String> importList;


    public String getAddedText() {
        return addedText;
    }

    public void setAddedText(String addedText) {
        this.addedText = addedText;
    }

    public String getAddMethods() {
        return addMethods;
    }

    public void setAddMethods(String addMethods) {
        this.addMethods = addMethods;
    }


    public Set<String> getImportList() {
        return importList;
    }

    public void setImportList(Set<String> importList) {
        this.importList = importList;
    }
}
