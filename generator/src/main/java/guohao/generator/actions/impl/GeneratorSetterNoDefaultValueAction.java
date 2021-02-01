package guohao.generator.actions.impl;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import guohao.generator.actions.AbstractGeneratorSetterAction;
import guohao.generator.meta.ClassInfo;
import guohao.generator.meta.CustomClassInfo;
import guohao.generator.meta.Library;
import guohao.generator.BundleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GeneratorSetterNoDefaultValueAction extends AbstractGeneratorSetterAction {

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.setter.value.null");
    }

    //region 生成空参数
    @Override
    protected List<ClassInfo> calculateNewSetterParamClassInfos(PsiLocalVariable localVariable, PsiMethod setterMethod) {
        return Collections.singletonList(new CustomClassInfo("", "", "", Library.OTHER));
    }
    //endregion
}
