package practice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import nju.pa.experiment.data.diff.LocationDiff;
import nju.pa.experiment.data.diff.MutantLocation;
import nju.pa.experiment.data.mutation.MutationResult;
import nju.pa.experiment.util.IOUtil;
import nju.pa.experiment.util.MutationDiffUtil;
import nju.pa.experiment.util.PitestParseUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This test simply simulate the process of generating a mutation json.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
public class JSONTest {

    private String outputDirPath = "material/json";

    @Test
    public void testJSON2() {
        String jptJsonPath = "material/json/JPT-RBT-mutation.json";
        String jsonPath = "material/json/RBT-mutation.json";
        try {
            String jsonContent1 = IOUtil.readAllcontent(jsonPath);
            String jsonContent2 = IOUtil.readAllcontent(jptJsonPath);
            MutationResult mutationResult1 = JSON.parseObject(jsonContent1, MutationResult.class);
            MutationResult mutationResult2 = JSON.parseObject(jsonContent2, MutationResult.class);

            List<MutantLocation> locations1 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult1);
            List<MutantLocation> locations2 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult2);

            List<LocationDiff> diffs = MutationDiffUtil.diff(locations1, locations2);

            System.out.println(diffs.size());
            diffs = diffs.stream().distinct().collect(Collectors.toList());
            System.out.println(diffs.size());

            List<LocationDiff> changedDiffs = diffs.stream()
                                                   .filter(LocationDiff::getIsChanged)
                                                   .collect(Collectors.toList());
            List<LocationDiff> deletedDiffs = diffs.stream()
                                                   .filter(LocationDiff::getIsDeleted)
                                                   .collect(Collectors.toList());
            List<LocationDiff> addedDiffs = diffs.stream()
                                                 .filter(LocationDiff::getIsAdded)
                                                 .collect(Collectors.toList());

            Map<String, List<LocationDiff>> mapDiff = new HashMap<>();
            mapDiff.put("changedMutants", changedDiffs);
            mapDiff.put("deletedMutants", deletedDiffs);
            mapDiff.put("addedMutants", addedDiffs);

            File diffJsonFile = new File(outputDirPath, "mutationDiff.json");
            String content = JSON.toJSONString(mapDiff, SerializerFeature.PrettyFormat);
            IOUtil.writeContentIntoFile(diffJsonFile, content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJSON1() {
        File summaryReport = new File("material/pitest-report-example/JPT-RBT", "index.html");
        MutationResult mutationResult = PitestParseUtil.parse(summaryReport);
        String jsonStr = JSON.toJSONString(mutationResult, SerializerFeature.PrettyFormat);

        File jsonFile = new File(outputDirPath, "JPT-RBT-mutation.json");
        try {
            IOUtil.writeContentIntoFile(jsonFile, jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJSON() {
        File summaryReport = new File("material/pitest-report-example/RBT", "index.html");
        MutationResult mutationResult = PitestParseUtil.parse(summaryReport);
        String jsonStr = JSON.toJSONString(mutationResult, SerializerFeature.PrettyFormat);

        File jsonFile = new File(outputDirPath, "RBT-mutation.json");
        try {
            IOUtil.writeContentIntoFile(jsonFile, jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
