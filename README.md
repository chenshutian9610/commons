# org.tree:commons:1.0.0
## 控制层：org.tree.commons.support.controller
> **ps: 使用 DebugController 和 ControllerException 需要配置 \<context:component-scan/> 扫描 org.tree.commons.support.controller 包**
---
* DebugController
> 需要在 properties 文件中配置如下参数
```
    debug.enable=true
    debug.packageToScan=控制层所在的包
```
> 使用 /debug.do 就能查看所有前端接口的参数及其注释，需要使用 @Comment
```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    // @Comment 第一种使用方式
    @RequestMapping("/login")
    public String login(@Comment("用户名") String username, @Comment("密码") String password) {
        return "";
    }
    
    // @Comment 第二种使用方式
    @RequestMapping("/login2")
    public String login(LoginVO loginVO) {
        return "";
    }
}

public class LoginVO {
    
    @Comment("用户名")
    private String username;
    
    @Comment("密码")
    private String password;
    
    // getter & setter
}
```

* ControllerException
> 接收控制层所有抛出的错误，并向前端返回一个 Result 对象
```javascript
{
    success : false,
    message : "未知错误",   // 如果配置了 debug.enable = true ，将显示错误的详细信息
    response : {}
}
```

## 持久层工程：org.tree.commons.generate.generator
* TableGenerator
> 正项工程与逆向工程，正项工程只支持 mysql 5.7，逆向工程是对 mybatis-generator 的封装<br>
> * TableGenerator 相关的注解<br>
&emsp;@Table<br>
&emsp;&emsp;&emsp;name ：表的名字，不配的话类名为表名<br>
&emsp;&emsp;&emsp;comment ：表的注释<br>
&emsp;@Column<br>
&emsp;&emsp;&emsp;id ：主键，默认为 false<br>
&emsp;&emsp;&emsp;unique ：唯一键，默认为 false<br>
&emsp;&emsp;&emsp;length ：只对字符串字段有用，默认 40<br>
&emsp;&emsp;&emsp;defaultValue ：默认值<br>
&emsp;&emsp;&emsp;comment ：字段的注释<br>
> * 使用 TableGenerator 逆向工程生成的 Mapper 新增了几个方法<br>
    1. insertBatchSelective(List)               // 批量插入<br>
    2. insertSelectiveWithGeneratedKey(record)  // 插入并返回主键<br>
    3. querySelective(args, example)            // 自定义查询，即 select a, b, c from tb<br>
```
############################## conf.properties ##############################

# 数据库配置（必须）
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///demo?characterEncoding=utf-8
jdbc.username=root
jdbc.password=

# 逆向工程配置（必须）
## 配置后逆向工程将在 org.tree.faster 下生成 model 包和 mapper 包；
## 如果使用 maven 的模块化开发的话需要配置 generate.module，值为模块的目录名
generate.root.package=org.tree.faster

# 逆向工程配置（可选）
## 官方插件，不需要加包名，中间使用逗号隔开
generate.mybatis.plugin=CachePlugin,SerializablePlugin
## 自定义插件，需要使用全限定名，中间使用逗号隔开
generate.other-support.plugin=a.b.MyPluginA,c.d.MyPluginB

/****************************** User.java *******************************/

package generate.model;

import org.tree.commons.generate.annotation.Column;
import org.tree.commons.generate.annotation.Table;

@Table(name = "tb_user", comment = "用户")
public class User {

    @Column(id = true)
    long id;

    @Column(comment = "账户名")
    String username;
    
    @Column(comment = "密码")
    String password;
}

/****************************** Main.java *******************************/

package generate;

import org.tree.commons.generate.generator.TableGenerator;

public class Main {

    // 扫描 generate 及其子包下所有使用了 @Table 的类
    // 然后向指定的数据库创建表，接着使用逆向工程在 org.tree.faster 下生成 model 包和 mapper 包
    public static void main(String[] args) throws Exception {
    
        // TableGenerator 的构参是 packageToScan
        TableGenerator generator = new TableGenerator(Main.class.getPackage().getName());
        
        // 读取配置文件
        generator.setConfigProperties("conf.properties");
        
        generator.forward();    //  正向工程
        generator.reverse();    //  逆向工程
    }
}

/****************************** UserDao *******************************/

@Component
public class UserDao {

    @Autowired
    private UserMapper userMapper;
    
    // select password from tb_user where username = #{username}
    public String getPassword(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualsTo(username);
        List<User> users = userMapper.querySelective(new UserArgs().setPassword(true), userExample);
        return users.size() == 0 ? null : users.get(0).getPassword();
    }
}
```

## 持久层：org.tree.commons.support.mapper
* IntegratedMapper
> 对 Mapper 的封装操作，需要配置 \<context:component-scan/> 扫描 ${rootPackage}.mapper.LocalMapperMap *（逆向工程自动生成）* 和 org.tree.commons.support.mapper.IntegratedMapper 
```java
@Component
public class DaoTest {
    
    @Autowired
    private IntegratedMapper mapper;
    
    public void test() {
        List<User> userList = mapper.selectByExample(new UserExample());
        List<Student> studentList = mapper.selectByExample(new StudentExample());
        List<Teacher> teacherList = mapper.selectByExample(new TeacherExample());
    }
}
```
* UnionSearch
> 连表查询
```java
@Component
public class DaoTest {
    
    @Autowired
    private UnionSearchMapper mapper;
    
    // select tb_user.username, tb_student.student_name 
    // from tb_user, tb_student 
    // where tb_user.id = 1 and tb_user.id = tb_student.id
    public NameDTO getNames() {
        UnionSearch search = new UnionSearch(mapper);
        search.selectColumns(new UserArgs().setUsername(true), new StudentArgs().setStudentName(true));
        search.createCriteria().and(UserEnum.ID, "= 1").and(StudentEnum.ID, "=", UserEnum.ID);
        return search.query(NameDTO.class);
    }
}

/****************************** NameDTO *******************************/

public class NameDTO {
    
    private String username;
    private String studentName;
    
    // getter & setter
}
```

## 实用工具：org.tree.commons.utils
* .VerifyUtils
> 是对 spring mvc 的前端验证的封装，需要依赖 hibernate-validator 包
```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @RequestMapping("/getEmail")
    public String login(@Valid EmailVO emailVO, BindingResult bindingResult) {
        // 当 email 为空或 email 格式错误时，返回相应的错误信息
        if (bindingResult.hasErrors())
            return VerifyUtils.getErrorString(bindingResult);
        return "";
    }
}

public class EmailVO {
    
    @Email // 出自 hibernate-validator 包
    private String email;
    
    // getter & setter
}
```
* PictureCode
> 生成图片验证码
```java
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/getPictureCode")
        public void getPictureCode(HttpServletResponse response) throws IOException {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PictureCode.generate(response.getOutputStream());
        }
}
```
* CollectionUtils / MapUtils / BucketUtils
> 集合类辅助工具
```
// ["hello", "world"]
List<String> list = CollectionUtils.listOf("hello", "world");
Set<String> set = CollectionUtils.of(HashSet::new, "hello", "world");

// {"hello" : "world", "nihao" : "zhonguo"}
Map<String, String> map = MapUtils.put("hello", "world").put("nihao", "zhongguo").build();

// { "hello" : ["world", "china"] }
Map<String, List<String>> bucket = BucketUtils.put("hello", "world").put("hello","china").bulid();
```
* PropertiesUtils
> 读取配置文件，返回一个 Properties 对象<br>
> Properties prop = PropertiesUtils.getProperties("conf.properties");
* MD5
> md5 加密<br>
> MD5.getMD5("hello"); // 直接加密<br>
> MD5.encrypt("hello"); // 加密的字符串长度为 32 则不做处理，否则加密
* RSA
> RSA 加密
```java
public class RSATest {
    public static void main(String[] args) {
        
        // RSA 密钥的长度可以为 1024 和 2048，后者被称为 RSA2
        Map<String, String> keys = RSA.createKeys(1024);
        
        // 公钥加密
        String ciphertext = RSA.publicEncrypt("你好，中国！", keys.get(RSA.PUBLIC_KEY));
        
        // 私钥解密
        String text = RSA.privateDecrypt(ciphertext, keys.get(RSA.PRIVATE_KEY));
        
        System.out.println(String.format("密文：%s%n明文：%s", ciphertext, text));
    }
}
```
* PerformanceUtils
```java
// 性能测试
public class MyTest {
    @Test
    public void test() {
        PerformanceUtils.test(() -> {
            // 测试的内容
        });
    }
}
```