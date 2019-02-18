# code-gen
对MybaitsGenerate工具的拓展，结合开发过程中的经验，通过代码生成规范CRUD。

# 代码生成思路说明
1.通过接口对通用方法进行规范，如BaseService、SqlMapper，对service层和dao层增删改查方法名进行统一，然后通过范型确定对应实体类。
2.对于常用通用方法提供统一实现，避免重复编码，参考AbstractService。

# 实现方式
主要通过实现抽象类PluginAdapter，完成对于Service层代码生成和添加Condition内部类。对于Dao层Mapper类的生成，由于Mybatis不提供接口并且都是通过代码来控制的，所以可以修改源码或通过编码的方式指定Context。
