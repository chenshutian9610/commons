import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class BaseTest {
    @Test
    public void test() throws IOException {
        Set<String> keys=new HashSet<>(650);
        Resource resource=new ClassPathResource("key-word.txt");
        Scanner scanner=new Scanner(resource.getFile());
        while (scanner.hasNext())
            System.out.println(scanner.next());
    }
}
