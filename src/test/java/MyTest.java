import org.junit.Test;
import xyz.linyh.tokenpool.client.TokenPoolClient;
import xyz.linyh.tokenpool.entity.GptTask;
import xyz.linyh.tokenpool.entity.TaskResult;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class MyTest {

    @Test
    public void test() throws Exception {

        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("nmidjfoasjdfoiasd");
        tokens.add("jisjfoiweidfsfwe");
        tokens.add("wejfiopwjeroiewds");
        tokens.add("wejfiodsofkcxmvokwe");

        TokenPoolClient tokenPoolClient = new TokenPoolClient(tokens, 1, 3);

        String result = tokenPoolClient.<String>addTask((token) -> {
            System.out.println("获取了token，然后调用了" + token);
//            TODO 应该返回一个taskResult？
            return "到达";
        });



    }
}
