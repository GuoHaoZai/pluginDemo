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

package com.generator.utils;

/**
 * 菜单名常量
 *
 * @author guohao
 * @since 2021/1/20
 */
public final class MenuNameConstants {
    private MenuNameConstants() {
    }

    public static final String GENERATE_SETTER_METHOD = "Generate all setter with default value";
    public static final String GENERATE_BUILDER_METHOD = "Generate builder chain call";
    public static final String GENERATE_SETTER_METHOD_NO_DEAULT_VALUE = "Generate all setter no default value";
    public static final String GENERATE_CONVERTER_FROM_METHOD = "Generate setter getter converter";

    public static final String ASSERTALLPROPS = "Assert all getters";
    public static final String BUILDER_METHOD_NAME = "builder";
}
