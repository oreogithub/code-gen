package com.oreo.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class ModelConditionPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerClass condition = new InnerClass("Condition");
        condition.setStatic(true);
        condition.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addInnerClass(condition);
        return true;
    }
}
