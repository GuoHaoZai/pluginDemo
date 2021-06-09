package guohao.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import guohao.anno.Getter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * getter方法添加器，编译时生成getter方法
 */
@SupportedAnnotationTypes("guohao.anno.Getter") //处理器需要处理的注解
@SupportedSourceVersion(SourceVersion.RELEASE_8) // 处理器支持的源码版本
public class GetterProcessor extends AbstractProcessor {

    /**
     * 主要是用来在编译期打log用的
     */
    private Messager messager;
    /**
     * 提供了待处理的抽象语法树
     */
    private JavacTrees trees;
    /**
     * 封装了创建AST节点的一些方法
     */
    private TreeMaker treeMaker;
    /**
     * 提供了创建标识符的方法
     */
    private Names names;

    /**
     * 通过ProcessingEnvironment来获取编译阶段的一些环境信息
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    /**
     * 实现具体逻辑的地方，也就是对AST进行处理的地方。
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Getter.class);

        for (Element element : set) {
            // 生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            // 处理遍历语法树得到的类定义部分jcClassDecl
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    List<JCTree> methodDecls = jcClassDecl.defs.stream()// 遍历jcTree的所有成员(包括成员变量和成员函数和构造函数)
                            .filter(tree -> tree.getKind().equals(Tree.Kind.VARIABLE))//过滤出成员变量
                            .map(JCTree.JCVariableDecl.class::cast)
                            .peek(jcVariableDecl -> messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed"))
                            .map(jcVariableDecl -> buildGetterMethodDecl(jcVariableDecl)) // 构造method declare
                            .reduce(new ListBuffer<JCTree>(),
                                    ListBuffer::append,
                                    ListBuffer::appendList)
                            .toList();
                    // 将构造好的方法添加到class declare中
                    jcClassDecl.defs = jcClassDecl.defs.prependList(methodDecls);
//                    super.visitClassDef(jcClassDecl);
                }
            });
        }
        return true;
    }

    /**
     * 构建getter方法抽象语法树
     */
    private JCTree.JCMethodDecl buildGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {
        JCTree.JCReturn aThis = treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName()));
        JCTree.JCBlock body = treeMaker.Block(0, List.of(aThis));
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC),
                                   getMethodName(jcVariableDecl),
                                   jcVariableDecl.vartype,
                                   List.nil(),
                                   List.nil(),
                                   List.nil(),
                                   body,
                                   null);
    }

    private Name getMethodName(JCTree.JCVariableDecl jcVariableDecl) {
        String variableName = jcVariableDecl.getName().toString();
        return names.fromString("get" + variableName.substring(0, 1).toUpperCase() + variableName.substring(1, variableName.length()));
    }
}