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

package guohao.code.generator.actions.impl;

import guohao.code.generator.actions.GenerateAllSetterBase;
import guohao.code.generator.constant.MenuNameConstants;
import org.jetbrains.annotations.NotNull;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GenerateAllSetterFromArgsAction extends GenerateAllSetterBase {

    @NotNull
    @Override
    public String getText() {
        return MenuNameConstants.GENERATE_SETTER_FROM_ARGS_METHOD;
    }

    @Override
    public GeneratorConfig getGeneratorConfig() {
        return new GeneratorConfig() {
            @Override
            public boolean shouldAddDefaultValue() {
                return true;
            }

            @Override
            public boolean fromParam() {
                return true;
            }
        };
    }
}
