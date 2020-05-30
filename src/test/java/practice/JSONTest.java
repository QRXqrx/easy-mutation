package practice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import nju.pa.experiment.data.MutationResult;
import nju.pa.experiment.util.PitestParseUtil;
import org.junit.Test;

import java.io.File;

/**
 * This test simply simulate the process of generating a mutation json.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
public class JSONTest {


    @Test
    public void testJSON() {
        File summaryReport = new File("material/pitest-report-example/RBT", "index.html");
        MutationResult mutationResult = PitestParseUtil.parse(summaryReport);
        String s = JSON.toJSONString(mutationResult, SerializerFeature.PrettyFormat);
        System.out.println(s);
    }
}
