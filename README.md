# code-gen
对MybaitsGenerate工具的拓展，结合开发过程中的经验，通过代码生成规范CRUD。

# 代码生成思路说明
通过接口对通用方法进行规范，如BaseService、SqlMapper，对service层和dao层增删改查方法名进行统一，然后通过范型确定对应实体类。
