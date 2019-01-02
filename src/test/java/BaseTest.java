/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class BaseTest {
    public static void main(String[] args) throws Exception{
        String str="<javaModelGenerator targetPackage=\"${generate.model.package}\" targetProject=\"${generate.java.target:./src/main/java}\">";
        System.out.println(str.replaceFirst("\\$\\{generate\\.java\\.target.*\\}",""));
        System.out.println();
    }
}
